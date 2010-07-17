package com.wesabe.api.accounts.analytics;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.wesabe.api.accounts.analytics.TagHierarchy.Node;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.Money;

public class TagHierarchyBuilder {
	public enum TagImportanceScheme implements Comparator<Entry<Tag, SumOfMoney>>,
		Function<SumOfMoney, Integer> {
		RANK_BY_AMOUNT {
			@Override
			public int compare(Entry<Tag, SumOfMoney> o1, Entry<Tag, SumOfMoney> o2) {
				return o2.getValue().getAmount().compareTo(o1.getValue().getAmount());
			}

			@Override
			public Integer apply(SumOfMoney sum) {
				return sum.getAmount().getValue().abs().negate().multiply(FACTOR).intValue();
			}
		},
		RANK_BY_COUNT {
			@Override
			public int compare(Entry<Tag, SumOfMoney> o1, Entry<Tag, SumOfMoney> o2) {
				return Integer.valueOf(o2.getValue().getCount()).compareTo(o1.getValue().getCount());
			}

			@Override
			public Integer apply(SumOfMoney sum) {
				return 0 - sum.getCount();
			}
		};
		
		private static BigDecimal FACTOR = new BigDecimal("10000");
	}
	
	public static enum HierarchyType implements Function<MonetarySummary, SumOfMoney>, Predicate<Txaction> {
		EARNINGS {
			@Override
			public SumOfMoney apply(MonetarySummary summary) {
				return summary.getEarnings();
			}

			@Override
			public boolean apply(Txaction txaction) {
				return txaction.getAmount().signum() > 0;
			}
		},
		SPENDING {
			@Override
			public SumOfMoney apply(MonetarySummary summary) {
				return summary.getSpending();
			}
			
			@Override
			public boolean apply(Txaction txaction) {
				return txaction.getAmount().signum() < 0;
			}
		},
		NET {
			@Override
			public SumOfMoney apply(MonetarySummary summary) {
				return summary.getNet();
			}
			
			@Override
			public boolean apply(Txaction txaction) {
				return true;
			}
		}
	}
	
	public static final Tag OTHER = new Tag("other tags") {
		@Override
		public int hashCode() {
			return "other".hashCode();
		};
	};
	
	public static final Tag UNTAGGED = new Tag("untagged") {
		@Override
		public int hashCode() {
			return "untagged".hashCode();
		};
	};
	
	private final TagSummarizer tagSummarizer;
	private final CurrencyExchangeRateMap exchangeRateMap;
	
	@Inject
	public TagHierarchyBuilder(TagSummarizer tagSummarizer, CurrencyExchangeRateMap exchangeRateMap) {
		this.tagSummarizer = tagSummarizer;
		this.exchangeRateMap = exchangeRateMap;
	}
	
	public TagHierarchy build(Iterable<Txaction> txactions, Currency currency, TagImportanceScheme tagImportanceScheme, HierarchyType hierarchyType, Set<Tag> filteredTags, int maxTags) {
		final Map<Tag, MonetarySummary> tagSummaries = tagSummarizer.summarize(txactions, currency);
		final Map<Tag, Integer> tagRankings = Maps.newHashMap(
			Maps.transformValues(
				Maps.transformValues(tagSummaries, hierarchyType),
				tagImportanceScheme
			)
		);
		
		Money total = Money.zero(currency);
		int totalCount = 0;
		final List<Txaction> taggedTxactions = Lists.newArrayList();
		final List<Txaction> untaggedTxactions = Lists.newArrayList();
		for (Txaction txaction : Iterables.filter(txactions, hierarchyType)) {
			if (isAnalyzable(txaction) && !hasFilteredTag(txaction, filteredTags)) {
				total = total.add(txaction.getConvertedAmountByFilteringTags(filteredTags, currency, exchangeRateMap).abs());
				totalCount++;
				if (txaction.isTagged()) {
					taggedTxactions.add(txaction);
				} else {
					untaggedTxactions.add(txaction);
				}
			}
		}
		
		final List<Node> nodes = Lists.newArrayList(build(tagRankings, taggedTxactions, tagImportanceScheme, currency, maxTags));
		Money sum = Money.zero(currency);
		for (Txaction txaction : untaggedTxactions) {
			sum = sum.add(txaction.getConvertedAmount(currency, exchangeRateMap).abs());
		}
		
		if (!untaggedTxactions.isEmpty()) {
			nodes.add(new Node(UNTAGGED, new SumOfMoney(sum, untaggedTxactions.size()), ImmutableList.<Node>of()));
		}
		
		return new TagHierarchy(nodes, new SumOfMoney(total, totalCount));
	}

	private boolean hasFilteredTag(Txaction txaction, Set<Tag> filteredTags) {
		for (TaggedAmount taggedAmount : txaction.getTaggedAmounts()) {
			if (filteredTags.contains(taggedAmount.getTag())) {
				return true;
			}
		}
		return false;
	}

	private List<Node> build(Map<Tag, Integer> tagRankings, Collection<Txaction> txactions, TagImportanceScheme tagImportanceScheme, Currency currency, int maxTags) {
		// build multimap
		final Multimap<Tag, Txaction> txactionsByTag = groupByMostImportantTags(txactions, tagRankings);
		
		// calculate the top tags for the multimap
		final Set<Tag> topTags = calculateTopTags(txactionsByTag, tagImportanceScheme, currency, maxTags);
		
		// remove top tags from rankings
		for (Tag tag : topTags) {
			tagRankings.remove(tag);
		}
		
		// compress multimap to the top tags + "other"
		compressToTopTags(txactionsByTag, topTags);
		
		// summarize each set of txactions
		final List<Node> nodes = Lists.newArrayList();
		for (Tag tag : txactionsByTag.keySet()) {
			final Collection<Txaction> taggedTxactions = txactionsByTag.get(tag);
			
			final Node node = new Node(tag,
				new SumOfMoney(sumForTag(taggedTxactions, tag, currency), taggedTxactions.size()),
				build(tagRankings, taggedTxactions, tagImportanceScheme, currency, maxTags)
			);
			nodes.add(node);
		}
		
		return nodes;
	}

	private Set<Tag> calculateTopTags(Multimap<Tag, Txaction> txactionsByTag,
		final TagImportanceScheme tagImportanceScheme, Currency currency, int maxTags) {
		// sum up the transactions for each tag
		final Map<Tag, SumOfMoney> sumsByTag = Maps.newHashMap();
		for (Tag tag : txactionsByTag.keySet()) {
			final Collection<Txaction> taggedTxactions = txactionsByTag.get(tag);
			sumsByTag.put(tag, new SumOfMoney(sumForTag(taggedTxactions, tag, currency), taggedTxactions.size()));
		}
		
		// sort the tags using the tag importance scheme
		final List<Entry<Tag, SumOfMoney>> sortedSumsByTag = Lists.newArrayList(sumsByTag.entrySet());
		Collections.sort(sortedSumsByTag, tagImportanceScheme);
		
		// pick X top tags
		final Set<Tag> topTags = Sets.newHashSetWithExpectedSize(maxTags);
		for (Entry<Tag, SumOfMoney> entry : sortedSumsByTag) {
			topTags.add(entry.getKey());
			if (topTags.size() >= maxTags) {
				break;
			}
		}
		return topTags;
	}

	private void compressToTopTags(Multimap<Tag, Txaction> txactionsByTag, Set<Tag> topTags) {
		for (Tag tag : Lists.newLinkedList(txactionsByTag.keySet())) {
			if (!topTags.contains(tag)) {
				txactionsByTag.putAll(OTHER, txactionsByTag.get(tag));
				txactionsByTag.removeAll(tag);
			}
		}
	}

	private Multimap<Tag, Txaction> groupByMostImportantTags(Collection<Txaction> txactions, Map<Tag, Integer> tagRankings) {
		// add
		final Multimap<Tag, Txaction> txactionsByTag = ArrayListMultimap.create();
		for (Txaction txaction : txactions) {
			final List<Tag> splitTags = Lists.newArrayListWithExpectedSize(txaction.getTaggedAmounts().size());
			int highestRanking = Integer.MAX_VALUE;
			Tag highestTag = null;

			for (TaggedAmount taggedAmount : txaction.getTaggedAmounts()) {
				if (taggedAmount.isSplit()) {
					splitTags.add(taggedAmount.getTag());
				} else {
					final Integer ranking = tagRankings.get(taggedAmount.getTag());
					if ((ranking != null) && (ranking < highestRanking)) {
						highestRanking = ranking;
						highestTag = taggedAmount.getTag();
					}
				}
			}
			
			if (highestTag != null) {
				txactionsByTag.put(highestTag, txaction);
			}
			
			for (Tag tag : splitTags) {
				if (tagRankings.containsKey(tag)) {
					txactionsByTag.put(tag, txaction);
				}
			}
		}
		return txactionsByTag;
	}

	private Money sumForTag(final Collection<Txaction> txactions, Tag tag, Currency currency) {
		Money sum = Money.zero(currency);
		for (Txaction txaction : txactions) {
			if (tag == OTHER) {
				sum = sum.add(txaction.getConvertedAmount(currency, exchangeRateMap).abs());
			} else {
				for (TaggedAmount taggedAmount : txaction.getTaggedAmounts()) {
					if (tag.equals(taggedAmount.getTag())) {
						sum = sum.add(taggedAmount.getConvertedAmount(currency, exchangeRateMap).abs());
					}
				}
			}
			
		}
		return sum;
	}
	
	private boolean isAnalyzable(Txaction txaction) {
		return !(txaction.isDeleted() || txaction.isTransfer() || txaction.isDisabled());
	}
}

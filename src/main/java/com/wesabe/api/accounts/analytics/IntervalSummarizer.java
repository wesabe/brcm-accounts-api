package com.wesabe.api.accounts.analytics;

import static com.google.common.base.Preconditions.*;

import java.util.Collection;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

import org.joda.time.Interval;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.Money;

public class IntervalSummarizer {
	private final CurrencyExchangeRateMap exchangeRateMap;

	@Inject
	public IntervalSummarizer(CurrencyExchangeRateMap exchangeRateMap) {
		this.exchangeRateMap = checkNotNull(exchangeRateMap);
	}
	
	public ImmutableMap<Interval, MonetarySummaryWithSplits> summarize(Iterable<Txaction> txactions,
		Interval dateRange, IntervalType intervalType, Currency currency, Set<Tag> filteredTags) {

		final Set<Tag> validFilteredTags = fixNullTags(filteredTags);
		
		final Multimap<Interval, Money> groupedAmounts = ArrayListMultimap.create();
		final Map<Interval, Multimap<Tag, Money>> groupedSplitAmounts = Maps.newHashMap();
		for (Txaction txaction : txactions) {
			if (isAnalyzable(txaction)) {
				final Interval interval = intervalType.currentInterval(txaction.getDatePosted());
				final Money filteredAmount = txaction.getConvertedAmountByFilteringTags(
					validFilteredTags, currency, exchangeRateMap);

				groupedAmounts.put(interval, filteredAmount);
				
				if (!groupedSplitAmounts.containsKey(interval)) {
					groupedSplitAmounts.put(interval, ArrayListMultimap.<Tag, Money>create());
				}
				
				for (TaggedAmount taggedAmount : txaction.getTaggedAmounts()) {
					groupedSplitAmounts.get(interval).put(taggedAmount.getTag(), taggedAmount.getConvertedAmount(currency, exchangeRateMap));
				}
			}
		}
		
		final ImmutableMap.Builder<Interval, MonetarySummaryWithSplits> results = ImmutableMap.builder();
		
		for (Interval summaryInterval : intervalType.getIntervals(dateRange)) {
			final Collection<Money> amounts = groupedAmounts.get(summaryInterval);
			final Multimap<Tag, Money> splits = groupedSplitAmounts.get(summaryInterval);
			results.put(summaryInterval, MonetarySummaryWithSplits.summarize(amounts, currency, splits == null ? ArrayListMultimap.<Tag, Money>create() : splits));
		}

		return results.build();
	}

	private Set<Tag> fixNullTags(Set<Tag> filteredTags) {
		if (filteredTags == null) {
			return ImmutableSet.of();
		}
		
		return filteredTags;
	}

	private boolean isAnalyzable(Txaction txaction) {
		return !(txaction.isDeleted() || txaction.isTransfer() || txaction.isDisabled());
	}
}

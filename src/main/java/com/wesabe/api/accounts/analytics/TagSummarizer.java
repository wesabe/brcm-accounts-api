package com.wesabe.api.accounts.analytics;

import java.util.Collection;
import java.util.Currency;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.Money;

public class TagSummarizer {
	private final CurrencyExchangeRateMap exchangeRateMap;

	@Inject
	public TagSummarizer(CurrencyExchangeRateMap exchangeRateMap) {
		this.exchangeRateMap = exchangeRateMap;
	}

	public ImmutableMap<Tag, MonetarySummary> summarize(Iterable<Txaction> txactions, Currency currency) {
		final Multimap<Tag, Money> groupedAmounts = ArrayListMultimap.create();
		for (Txaction txaction : txactions) {
			if (isAnalyzable(txaction)) {
				for (TaggedAmount taggedAmount : txaction.getTaggedAmounts()) {
					final Tag tag = taggedAmount.getTag();
					final Money amount = taggedAmount.getConvertedAmount(currency, exchangeRateMap);
					
					groupedAmounts.put(tag, amount);
				}
			}
		}
		
		final ImmutableMap.Builder<Tag, MonetarySummary> results = ImmutableMap.builder();
		for (Tag tag : groupedAmounts.keySet()) {
			final Collection<Money> amounts = groupedAmounts.get(tag);
			results.put(
				tag,
				MonetarySummary.summarize(amounts, currency)
			);
		}
		
		return results.build();
	}
	
	private boolean isAnalyzable(Txaction txaction) {
		return !(txaction.isDeleted() || txaction.isTransfer() || txaction.isDisabled());
	}
}

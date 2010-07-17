package com.wesabe.api.accounts.analytics;

import java.util.Collection;
import java.util.Currency;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.util.money.Money;

public class MonetarySummaryWithSplits extends MonetarySummary {
	
	public static MonetarySummaryWithSplits summarize(Collection<Money> amounts, Currency currency, Multimap<Tag, Money> splits) {
		int spendingCount = 0, earningsCount = 0;
		Money spendingSum = Money.zero(currency), earningsSum = Money.zero(currency);
		
		for (Money amount : amounts) {
			if (amount.signum() > 0) {
				earningsCount++;
				earningsSum = earningsSum.add(amount);
			} else if (amount.signum() < 0) {
				spendingCount++;
				spendingSum = spendingSum.add(amount.abs());
			}
		}
		
		final ImmutableMap.Builder<Tag, MonetarySummary> splitSummaries = ImmutableMap.builder();
		for (Tag tag : splits.keySet()) {
			final Collection<Money> splitValues = splits.get(tag);
			splitSummaries.put(tag, MonetarySummary.summarize(splitValues, currency));
		}
		
		return new MonetarySummaryWithSplits(
				new SumOfMoney(spendingSum, spendingCount),
				new SumOfMoney(earningsSum, earningsCount),
				splitSummaries.build()
		);
	}
	
	private final Map<Tag, MonetarySummary> splitSummaries;
	
	public MonetarySummaryWithSplits(SumOfMoney spending, SumOfMoney earnings, Map<Tag, MonetarySummary> splitSummaries) {
		super(spending, earnings);
		this.splitSummaries = ImmutableMap.copyOf(splitSummaries);
	}
	
	public Map<Tag, MonetarySummary> getSplitSummaries() {
		return splitSummaries;
	}
}

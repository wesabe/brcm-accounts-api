package com.wesabe.api.accounts.analytics;

import java.util.Collection;
import java.util.Currency;

import com.wesabe.api.util.money.Money;

public class MonetarySummary {
	private final SumOfMoney spending, earnings, net;
	
	public static MonetarySummary summarize(Collection<Money> amounts, Currency currency) {
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
		
		return new MonetarySummary(
				new SumOfMoney(spendingSum, spendingCount),
				new SumOfMoney(earningsSum, earningsCount)
		);
	}
	
	public MonetarySummary(SumOfMoney spending, SumOfMoney earnings) {
		this.spending = spending;
		this.earnings = earnings;
		this.net = calculateNet(spending, earnings);
	}
	
	public SumOfMoney getSpending() {
		return spending;
	}
	
	public SumOfMoney getEarnings() {
		return earnings;
	}
	
	public SumOfMoney getNet() {
		return net;
	}
	
	private SumOfMoney calculateNet(SumOfMoney spending, SumOfMoney earnings) {
		final Money amount = earnings.getAmount().subtract(spending.getAmount());
		final int count = earnings.getCount() + spending.getCount();
		return new SumOfMoney(amount, count);
	}

}
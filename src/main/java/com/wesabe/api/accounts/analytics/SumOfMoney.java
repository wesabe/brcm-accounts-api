package com.wesabe.api.accounts.analytics;

import com.wesabe.api.util.money.Money;

/**
 * A class representing a sum of money.
 * 
 * @author coda
 * @see Money
 */
public final class SumOfMoney {
	private final Money amount;
	private final int count;
	
	/**
	 * Creates a new sum.
	 * 
	 * @param amount the amount of money
	 * @param count the number of items
	 */
	public SumOfMoney(Money amount, int count) {
		this.amount = amount;
		this.count = count;
	}
	
	/**
	 * Returns the amount.
	 * 
	 * @return the amount
	 */
	public Money getAmount() {
		return amount;
	}

	/**
	 * Returns the number of items.
	 * 
	 * @return the number of items
	 */
	public int getCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return amount.toPlainString() + amount.getCurrency().getCurrencyCode() + "/" + count;
	}
}

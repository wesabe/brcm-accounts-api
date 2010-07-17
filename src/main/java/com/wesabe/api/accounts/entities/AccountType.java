package com.wesabe.api.accounts.entities;

import java.util.Arrays;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public enum AccountType {
	UNKNOWN(1, "Unknown", true, true),
	CHECKING(2, "Checking", true, true),
	MONEY_MARKET(3, "Money Market", true, true),
	CREDIT_CARD(4, "Credit Card", true, true),
	SAVINGS(5, "Savings", true, true),
	CREDIT_LINE(7, "Credit Line", true, true),
	BROKERAGE(8, "Brokerage", true, true),
	CASH(9, "Cash", false, false),
	MANUAL(10, "Manual", true, false),
	INVESTMENT(11, "Investment", true, true),
	CERTIFICATE(12, "Certificate of Deposit", true, true),
	LOAN(13, "Loan", true, true),
	MORTGAGE(14, "Mortgage", true, true);	

	final private int value;
	final private String name;
	final private boolean hasBalance;
	final private boolean hasUploads;

	private AccountType(int value, String name, boolean hasBalance, boolean hasUploads) {
		this.value = value;
		this.name  = name;
		this.hasBalance = hasBalance;
		this.hasUploads = hasUploads;
	}
	
	public int getValue() {
		return value;
	}

	public static AccountType byValue(int value) {
		return VALUE_LOOKUP.get(value);
	}
	
	private static final Function<AccountType, Integer> VALUE_FUNCTION =
		new Function<AccountType, Integer>(){
			@Override
			public Integer apply(AccountType status) {
				return status.getValue();
			}
		};

	private static final Map<Integer, AccountType> VALUE_LOOKUP =
		Maps.uniqueIndex(
			Arrays.asList(AccountType.class.getEnumConstants()),
			VALUE_FUNCTION
		);

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	public boolean hasBalance() {
		return hasBalance;
	}
	
	public boolean hasUploads() {
		return hasUploads;
	}
	
	public boolean hasContinuousBalance() {
		return hasBalance && !hasUploads;
	}
}

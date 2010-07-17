package com.wesabe.api.util.money;

import java.text.MessageFormat;
import java.util.Currency;

public class CurrencyMismatchException extends RuntimeException {
	private static final long	serialVersionUID	= -7842199785712808166L;
	private final String action;
	private final Currency baseCurrency;
	private final Currency otherCurrency;
	
	public CurrencyMismatchException(String action, Currency baseCurrency,
			Currency otherCurrency) {
		super(MessageFormat.format(
			"Cannot {0} {1} and {2} amounts.",
			action,
			baseCurrency.getCurrencyCode(),
			otherCurrency.getCurrencyCode()
		));
		this.action = action;
		this.baseCurrency = baseCurrency;
		this.otherCurrency = otherCurrency;
	}

	public String getAction() {
		return action;
	}
	
	public Currency getBaseCurrency() {
		return baseCurrency;
	}
	
	public Currency getOtherCurrency() {
		return otherCurrency;
	}
	
}

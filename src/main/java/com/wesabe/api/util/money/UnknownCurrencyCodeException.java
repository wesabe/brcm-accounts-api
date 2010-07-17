/**
 * 
 */
package com.wesabe.api.util.money;

public class UnknownCurrencyCodeException extends RuntimeException {
	private static final long	serialVersionUID	= 6451480342623775732L;
	private final String		currencyCode;

	public UnknownCurrencyCodeException(String currencyCode, Exception cause) {
		super("Unknown currency code: " + currencyCode, cause);
		this.currencyCode = currencyCode;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}
}
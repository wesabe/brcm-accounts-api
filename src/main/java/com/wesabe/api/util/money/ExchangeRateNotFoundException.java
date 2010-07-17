package com.wesabe.api.util.money;

import java.util.Currency;

import org.joda.time.DateTime;

/**
 * An exception raised when a currency exchange rate cannot be found or
 * fudged.
 * 
 * @author coda
 *
 */
public class ExchangeRateNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 6484187044392202293L;
	private final Currency sourceCurrency, targetCurrency;
	private final DateTime requestedDate;
	
	/**
	 * Creates a new {@link ExchangeRateNotFoundException}.
	 * 
	 * @param sourceCurrency the currency we tried to convert from
	 * @param targetCurrency the currency we tried to convert to
	 * @param requestedDate the date of the transaction
	 */
	public ExchangeRateNotFoundException(Currency sourceCurrency,
			Currency targetCurrency, DateTime requestedDate) {
		super("Unable to find usable currency exchange rate from "
			+ sourceCurrency.getCurrencyCode() + " to "
			+ targetCurrency.getCurrencyCode() + " on "
			+ requestedDate.toLocalDate().toString());
		this.sourceCurrency = sourceCurrency;
		this.targetCurrency = targetCurrency;
		this.requestedDate = requestedDate;
	}
	
	/**
	 * The requested date for which no rate could be found.
	 * 
	 * @return the requested date
	 */
	public DateTime getRequestedDate() {
		return requestedDate;
	}
	
	/**
	 * The requested source currency for which no rate could be found.
	 * 
	 * @return the requested source currency
	 */
	public Currency getSourceCurrency() {
		return sourceCurrency;
	}
	
	/**
	 * The requested target currency for which no rate could be found.
	 * 
	 * @return the requested target currency
	 */
	public Currency getTargetCurrency() {
		return targetCurrency;
	}
}
package com.wesabe.api.util.money;

import java.util.Currency;


/**
 * A convertible amount of money.
 * 
 * @author coda
 */
public interface Monetary {

	public abstract Money getAmount() throws UnknownCurrencyCodeException;

	public abstract Money getConvertedAmount(Currency target,
			CurrencyExchangeRateMap exchangeRates)
			throws ExchangeRateNotFoundException, UnknownCurrencyCodeException;

}
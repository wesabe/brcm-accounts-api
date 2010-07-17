package com.wesabe.api.util.money;

import java.math.BigDecimal;
import java.util.Currency;

import org.joda.time.DateTime;

/**
 * A currency exchange rate map which is only populated with exchange rates to USD. Exchange rates
 * are then calculated using the USD rate as a pivot.
 * <p>
 * This is not an accurate model of exchange rates, but it's close enough to have worked so far. The
 * next version of this should drop this class in favor of finer-grained exchange rate data.
 * 
 * @author coda
 * @deprecated Not an accurate model of exchange rates. Build something better.
 */
@Deprecated
public class DollarPivotCurrencyExchangeRateMap extends CurrencyExchangeRateMap {
	private static final Currency USD = Currency.getInstance("USD");
	
	@Override
	public void addExchangeRate(Currency source, Currency target, DateTime date, BigDecimal rate) {
		if (!source.equals(USD)) {
			throw new IllegalArgumentException("Can only accept exchange rates from USD.");
		}
		super.addExchangeRate(source, target, date, rate);
	}
	
	@Override
	public BigDecimal getExchangeRate(Currency source, Currency target, DateTime date)
			throws ExchangeRateNotFoundException {
		final BigDecimal a = super.getExchangeRate(target, USD, date);
		final BigDecimal b = super.getExchangeRate(source, USD, date);
		return b.divide(a, MATH_CONTEXT);
	}
}

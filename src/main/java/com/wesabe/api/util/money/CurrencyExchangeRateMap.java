package com.wesabe.api.util.money;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Currency;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.joda.time.DateTime;


/**
 * A map of currency-to-currency exchange rates.
 *
 * This class is thread-safe.
 *
 * @author coda
 *
 */
public class CurrencyExchangeRateMap {
	
	/**
	 * A threadsafe map of {@link Currency} to {@link Currency} to
	 * {@link DateTime} to {@link BigDecimal}s.
	 * 
	 * @author coda

	 */
	@SuppressWarnings("serial")
	private static class CurrencyToCurrencyToDateToRateMap extends
			ConcurrentHashMap<Currency, CurrencyToDateToRateMap> {
		
		/**
		 * Inserts a {@literal currency->currency->date->rate} path into the
		 * rates tree in a thread-safe manner.
		 * <p>
		 * By creating a full submap first, and using
		 * {@link ConcurrentHashMap#putIfAbsent(Object, Object)}, the path with
		 * the minumum number of links is created atomically. For example, if
		 * {@code source} does not exist in the rates tree, an entire {@literal
		 * currency->date->rate} submap is inserted. If it does, an entire
		 * {@literal date->rate} sub-submap is conditionally inserted. If that
		 * already exists, the existing submap is updated with the date and rate
		 * (in a destructive manner - newly inserted rates take precedence over
		 * existing ones).
		 * 
		 * @param source the currency from which the money is being converted
		 * @param target the currency to which the money is being converted
		 * @param date the date on which the money is being converted
		 * @param rate the exchange rate between {@code source} and
		 * 				{@code target}
		 */
		public void addRate(Currency source, Currency target, DateTime date,
				BigDecimal rate) {
			// build a full outer map
			final DateToRateMap innerMap = new DateToRateMap(date, rate);
			final CurrencyToDateToRateMap outerMap = new CurrencyToDateToRateMap(target, innerMap);

			// insert the parts of the map which don't already exist
			final CurrencyToDateToRateMap existingOuterMap = super.putIfAbsent(source, outerMap);
			if (existingOuterMap != null) {
				final DateToRateMap existingInnerMap = existingOuterMap
						.putIfAbsent(target, outerMap.get(target));
				if (existingInnerMap != null) {
					existingInnerMap.put(date, rate);
				}
			}
		}
	}
	
	/**
	 * A threadsafe map of {@link Currency} to {@link DateTime} to
	 * {@link BigDecimal}s.
	 * 
	 * @author coda
	 */
	@SuppressWarnings("serial")
	private static class CurrencyToDateToRateMap extends ConcurrentHashMap<Currency,DateToRateMap> {
		public CurrencyToDateToRateMap(Currency target, DateToRateMap rates) {
			super();
			this.put(target, rates);
		}
	}
	
	/**
	 * A threadsafe map of {@link DateTime} to {@link BigDecimal}s.
	 * 
	 * @author coda
	 *
	 */
	@SuppressWarnings("serial")
	private static class DateToRateMap extends ConcurrentSkipListMap<Long, BigDecimal> {
		
		public DateToRateMap(DateTime date, BigDecimal rate) {
			super();
			this.put(date, rate);
		}
		
		public BigDecimal put(DateTime key, BigDecimal value) {
			return super.put(keyTransform(key), value);
		}
		
		/**
		 * Given a date, returns the closest rate.
		 * 
		 * @param date a given date
		 * @return the rate on the date closest to {@code date}
		 */
		public BigDecimal getClosest(DateTime date) {
			final long key = keyTransform(date);
			
			final BigDecimal exactMatch = super.get(key);
			if (exactMatch != null) {
				return exactMatch;
			}
			
			final Entry<Long, BigDecimal> ceiling = super.ceilingEntry(key);
			final Entry<Long, BigDecimal> floor = super.floorEntry(key);
			
			if (ceiling == null) {
				return floor.getValue();
			} else if (floor == null) {
				return ceiling.getValue();
			} else if (ceiling.getKey() - key < key - floor.getKey()) {
				return ceiling.getValue();
			}
			return floor.getValue();
		}
		
		private long keyTransform(DateTime key) {
			return key.getMillis();
		}
		
	}
	
	protected static final MathContext MATH_CONTEXT = MathContext.DECIMAL32;
	
	private final CurrencyToCurrencyToDateToRateMap rates =
							new CurrencyToCurrencyToDateToRateMap();

	/**
	 * Returns the exchange rate from one currency into another on a particular
	 * date.
	 *
	 * Given two currencies, {@code source} and {@code target}, returns the
	 * constant multiplier by which a unit of {@code source} should be
	 * multiplied in order to find its value in {@code target} on {@code date}.
	 *
	 * @param source the currency from which the money is being converted
	 * @param target the currency to which the money is being converted
	 * @param date the date on which the money is being converted
	 * @return the exchange rate between {@code source} and {@code target}
	 * @throws ExchangeRateNotFoundException if the exchange rate cannot be found
	 */
	public BigDecimal getExchangeRate(Currency source, Currency target, DateTime date)
			throws ExchangeRateNotFoundException {

		if (source.equals(target)) {
			return BigDecimal.ONE;
		}

		final CurrencyToDateToRateMap subRates = rates.get(source);
		if (subRates != null) {
			final DateToRateMap currencies = subRates.get(target);
			if (currencies != null) {
				return currencies.getClosest(date);
			}
		}
		
		throw new ExchangeRateNotFoundException(source, target, date);
	}

	/**
	 * Adds a currency exchange rate between two currencies for a particular
	 * date. Also adds the inverse relationship (e.g., from
	 * <code>targetCurrency</code> to <code>sourceCurrency</code>, using the
	 * inverse of <code>rate</code>).
	 *
	 * @param source the currency to convert from
	 * @param target the currency to convert to
	 * @param date the date on which the rate was effective
	 * @param rate the ratio of <code>from:to</code>
	 */
	public void addExchangeRate(Currency source, Currency target, DateTime date, BigDecimal rate) {
		if (rate.signum() > 0) {
			rates.addRate(source, target, date, rate);
			// REVIEW coda@wesabe.com -- Oct 30, 2008: Reevaluate our modeling of inverse exchange rates.
			// This may or may not be an accurate model of currency exchange rates.
			// Is the Yen-to-Dollars rate the same thing as the inverse of the
			// Dollars-to-Yen rate? What would it mean if it wasn't?
			rates.addRate(target, source, date, inverseRate(rate));
		}
	}

	/**
	 * All {@link Currency} types for which this
	 * {@link CurrencyExchangeRateMap} has <em>any</em> rates.
	 *
	 * @return supported currencies
	 */
	public Set<Currency> getCurrencies() {
		return rates.keySet();
	}

	/**
	 * Returns <code>true</code> if this {@link CurrencyExchangeRateMap} has any
	 * rates for the given {@link Currency}.
	 *
	 * @param currency a given currency
	 * @return whether or not the currency is supported
	 */
	public boolean isSupported(Currency currency) {
		return rates.containsKey(currency);
	}
	
	/**
	 * Calculates the inverse of a given exchange rate.
	 * 
	 * @param rate the non-zero exchange rate
	 * @return the inverse rate
	 */
	private BigDecimal inverseRate(BigDecimal rate) {
		final BigDecimal invertableOne = BigDecimal.ONE;
		return invertableOne.divide(rate, MATH_CONTEXT);
	}
}

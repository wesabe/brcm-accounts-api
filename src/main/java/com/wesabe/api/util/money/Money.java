package com.wesabe.api.util.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import org.joda.time.DateTime;

import com.google.common.base.Objects;


// REVIEW coda@wesabe.com -- Nov 1, 2008: The one thing which slows this class
// down is the BigDecimal math. While that's nice for doing stuff like rounding,
// formatting, etc., it's far more complicated than we actually need. A
// potential optimization would be to drop BigDecimal in favor of storing the
// amount in minor units, and then dividing by #getDefaultFractionDigits() for
// presentation purposes. This would allow us to use simple primitives for
// calculations at the expense of increasing the implementation complexity.

/**
 * An amount of money in a particular currency.
 *
 * @author coda
 *
 */
public final class Money implements Comparable<Money> {
	private final BigDecimal amount;
	private final Currency currency;
	
	/**
	 * Returns a zero-amount {@link Money} instance in the specified currency.
	 * 
	 * @param currency a given currency
	 * @return a zero-amount {@link Money} in {@code currency}
	 */
	public static Money zero(Currency currency) {
		return new Money(BigDecimal.ZERO, currency);
	}

	/**
	 * Creates a new {@link Money} object with a given amount and currency.
	 *
	 * @param amount the amount of money
	 * @param currency the currency of the money
	 */
	public Money(BigDecimal amount, Currency currency) {
		this.amount = normalizeScale(amount, currency);
		this.currency = currency;
	}

	/**
	 * Returns a {@link Money} equal to {@code this + addend}. If {@code this}
	 * and {@code addend} are of different currencies, a
	 * {@link CurrencyMismatchException} is thrown.
	 * 
	 * @param addend value to be added to this {@link Money}
	 * @return {@code this + addend}
	 * @throws CurrencyMismatchException if {@code this} and {@code addend} have
	 * 			different currencies
	 */
	public Money add(Money addend) throws CurrencyMismatchException {
		if (currency.equals(addend.currency)) {
			return new Money(this.amount.add(addend.amount), this.currency);
		}
		throw new CurrencyMismatchException("add", currency, addend.currency);
	}

	/**
	 * Returns {@code true} if {@code this} is zero, {@code false} otherwise.
	 * 
	 * @return {@code true} if {@code this == 0}
	 */
	public Boolean isZero() {
		return amount.signum() == 0;
	}

	/**
	 * Returns a {@link Money} of the same currency as {@code this} whose value
	 * is {@code 0 - this}.
	 * 
	 * @return {@code 0 - this}
	 */
	public Money negate() {
		return new Money(amount.negate(), currency);
	}

	/**
	 * Returns a {@link Money} equal to {@code this - subtrahend}. If
	 * {@code this} and {@code subtrahend} are of different currencies, a
	 * {@link CurrencyMismatchException} is thrown.
	 * 
	 * @param subtrahend value to be subtracted from this {@link Money}
	 * @return {@code this - subtrahend}
	 * @throws CurrencyMismatchException if {@code this} and {@code subtrahend}
	 * 			have different currencies
	 */
	public Money subtract(Money subtrahend) throws CurrencyMismatchException {
		if (currency.equals(subtrahend.currency)) {
			return new Money(this.amount.subtract(subtrahend.amount), this.currency);
		}
		throw new CurrencyMismatchException("subtract", currency, subtrahend.currency);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Money) {
			final Money that = (Money) obj;
			return Objects.equal(amount, that.amount)
					&& Objects.equal(currency, that.currency);
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(amount, currency);
	}

	/**
	 * Returns this {@link Money} object as a string, formatted as a currency in
	 * the default locale.
	 *
	 * @return the formatted currency
	 * @see Money#toString(Locale)
	 * @see Locale#getDefault()
	 * @deprecated Use {@link Money#toString(Locale)} instead.
	 */
	@Override
	@Deprecated
	public String toString() {
		return toString(Locale.getDefault());
	}

	/**
	 * Returns this {@link Money} object as a string, formatted in the specified
	 * locale.
	 *
	 * @param locale the locale for which the currency should be formatted
	 * @return the locale-formatted amount
	 */
	public String toString(Locale locale) {
		return MoneyFormat.of(currency, locale).format(amount);
	}
	
	/**
	 * Returns this {@code Money} object as a string, formatted as a decimal
	 * amount (e.g., {@code -329.88}.
	 * 
	 * @return the decimal-formatted amount
	 */
	public String toPlainString() {
		return amount.toPlainString();
	}

	/**
	 * Gets the amount apart from the currency. This should only be used for
	 * serializing to the database, not for doing math operations.
	 * 
	 * @return the raw amount
	 */
	public BigDecimal getValue() {
		return amount;
	}

	/**
	 * Gets the currency apart from the amount.
	 * 
	 * @return the raw currency
	 */
	public Currency getCurrency() {
		return currency;
	}
	
    /**
     * Returns a {@link Money} in the target currency, using the exchange rate
     * on a particlar day to perform any conversions.
     * 
     * @param exchangeRates the map of currency exchange rates
     * @param targetCurrency the currency to which {@code this} will be converted
     * @param date the date to use for the exchange rates
     * @return a single-currency {@link Money} in the target currency
     */
	public Money convert(CurrencyExchangeRateMap exchangeRates,
			Currency targetCurrency, DateTime date)
			throws ExchangeRateNotFoundException {
		
		if (targetCurrency.equals(currency)) {
			return this;
		} else if (isZero()) {
			return zero(targetCurrency);
		}
		
		return new Money(
			amount.multiply(
				exchangeRates.getExchangeRate(currency, targetCurrency, date)
			),
			targetCurrency
		);
	}

	/**
	 * Returns the signum function of this {@link Money}.
	 *
	 * @return {@code -1}, {@code 0}, or {@code 1} as the value of this
	 * 		   {@link Money}  is negative, zero, or positive.
	 */
	public int signum() {
		return amount.signum();
	}

	/**
	 * Returns the absolute value of the amount of this {@link Money} in the
	 * same currency.
	 *
	 * @return the absolute value of this {@link Money}
	 */
	public Money abs() {
		return signum() < 0 ? new Money(amount.abs(), currency) : this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Money o) {
		if (!o.currency.equals(currency)) {
			throw new CurrencyMismatchException("compare", currency, o.currency);
		}
		return amount.compareTo(o.amount);
	}
	
	/**
	 * Normalizes {@code amount}'s scale to the number of fraction digits for
	 * {@code currency} (e.g., USD has 2 fraction digits -- {@code $100.00}).
	 * 
	 * @param amount an amount of money with an unspecified scale
	 * @param currency the currency whose scale {@code amount} should be set to
	 * @return an amount with the samel scale as {@code currency}
	 */
	private BigDecimal normalizeScale(BigDecimal amount, Currency currency) {
		return amount.setScale(
			currency.getDefaultFractionDigits(),
			RoundingMode.HALF_EVEN
		);
	}

	/**
     * Returns a {@link Money} whose value is {@code this * multiplicand} and whose currency is that
     * of this {@link Money}.
     * 
     * @param  multiplicand value to be multiplied by this {@link Money}
     * @return {@code this * multiplicand}
     */
	public Money multiply(int multiplicand) {
		return new Money(amount.multiply(new BigDecimal(multiplicand)), currency);
	}
}

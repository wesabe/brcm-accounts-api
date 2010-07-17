package com.wesabe.api.util.money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.NumberFormat;

public class MoneyFormat {
	private static final char REGULAR_SPACE = ' ';
	private static final char NON_BREAKING_SPACE = '\u00a0';

	private interface MoneyFormatBuilder {
		public abstract NumberFormat build(Currency currency);
	}
	
	private static com.ibm.icu.util.Currency getICUCurrency(Currency currency) {
		return com.ibm.icu.util.Currency.getInstance(currency.getCurrencyCode());
	}
	
	// Don't use parentheses to indicate negative amounts. It just confuses old
	// people.
	private static class USFormatBuilder implements MoneyFormatBuilder {
		@Override
		public NumberFormat build(Currency currency) {
			final DecimalFormat nf = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
			nf.setCurrency(getICUCurrency(currency));
			nf.setNegativePrefix(nf.getNegativePrefix().replace("(", "-"));
			nf.setNegativeSuffix("");
      nf.setRoundingIncrement(0.0);
			
			return nf;
		}
	}
	
	private static final ImmutableMap<Locale, MoneyFormatBuilder> FORMATS = ImmutableMap.of(
		Locale.US, (MoneyFormatBuilder) new USFormatBuilder()
	);
	
	private final NumberFormat format;
	
	public static MoneyFormat of(Currency currency, Locale locale) {
		if (FORMATS.containsKey(locale)) {
			return new MoneyFormat(FORMATS.get(locale).build(currency));
		}
		
		return buildDefaultFormat(currency, locale);
	}

	private static MoneyFormat buildDefaultFormat(Currency currency, Locale locale) {
		final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		nf.setCurrency(getICUCurrency(currency));
		return new MoneyFormat(nf);
	}

	private MoneyFormat(NumberFormat format) {
		this.format = format;
	}

	public String format(BigDecimal amount) {
		return replaceNonBreakingSpaces(format.format(amount));
	}

	private String replaceNonBreakingSpaces(final String s) {
		// REVIEW coda@wesabe.com -- Apr 9, 2009: Figure out what to do about non-breaking spaces.
		// It stands to reason that non-breaking spaces are the technically correct
		// choice, but they're a pain in the ass to deal with in tests, etc.
		return s.replace(NON_BREAKING_SPACE, REGULAR_SPACE);
	}
	
}

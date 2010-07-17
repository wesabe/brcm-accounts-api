package com.wesabe.api.util.money.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.ExchangeRateNotFoundException;

@RunWith(Enclosed.class)
public class CurrencyExchangeRateMapTest {
	
	public static class A_Map_With_A_Rate {
		private static final BigDecimal dollarToEuroRate = decimal("0.8901");
		private static final BigDecimal euroToDollarRate = decimal("1.123469");
		
		private CurrencyExchangeRateMap rates;

		@Before
		public void setRates() {
			this.rates = singleRateMap(USD, EUR, jun15th, dollarToEuroRate);
		}

		@Test
		public void shouldHaveTheRate() throws Exception {
			assertEquals(dollarToEuroRate, rates.getExchangeRate(USD, EUR, jun15th));
		}

		@Test
		public void shouldHaveTheInverseRate() throws Exception {
			assertEquals(euroToDollarRate, rates.getExchangeRate(EUR, USD, jun15th));
		}

		@Test
		public void shouldSupportTheRate() throws Exception {
			assertTrue(rates.isSupported(USD));
		}

		@Test
		public void shouldSupportTheInverseRate() throws Exception {
			assertTrue(rates.isSupported(EUR));
		}

		@Test
		public void shouldNotSupportSomeOtherRate() throws Exception {
			assertFalse(rates.isSupported(NOK));
		}
		
		@Test
		public void shouldHaveAListOfSupportedCurrencies() throws Exception {
			List<String> currencies = new ArrayList<String>();
			for (Currency currency : rates.getCurrencies()) {
				currencies.add(currency.getCurrencyCode());
			}
			Collections.sort(currencies);
			assertArrayEquals(new String[] { "EUR", "USD" }, currencies.toArray());
		}
	}
	
	public static class A_Map_With_A_Stub_Rate {
		private static final BigDecimal stubRate = decimal("0.0");
		
		private CurrencyExchangeRateMap rates;
		
		@Before
		public void setRates() {
			this.rates = singleRateMap(USD, EUR, jun15th, stubRate);
		}
		
		@Test
		public void shouldNotHaveTheRate() throws Exception {
			assertFalse(rates.isSupported(USD));
		}
		
		@Test
		public void shouldNotHaveTheInverseRate() throws Exception {
			assertFalse(rates.isSupported(EUR));
		}
	}

	public static class Getting_A_Rate_For_An_Unsupported_Target_Currency {
		private static final BigDecimal dollarToEuroRate = decimal("0.8901");
		
		private CurrencyExchangeRateMap rates;

		@Before
		public void setRates() {
			this.rates = singleRateMap(USD, EUR, jun15th, dollarToEuroRate);
		}

		@Test
		public void shouldThrowAnException() throws Exception {
			boolean exceptionThrown = false;
			try {
				rates.getExchangeRate(EUR, NOK, jun15th);
			} catch (ExchangeRateNotFoundException e) {
				exceptionThrown = true;
				assertEquals(jun15th, e.getRequestedDate());
				assertEquals(EUR, e.getSourceCurrency());
				assertEquals(NOK, e.getTargetCurrency());
				assertEquals("Unable to find usable currency exchange rate from EUR to NOK on 2008-06-15", e.getMessage());
			}
			assertTrue(exceptionThrown);
		}
	}
	
	public static class Getting_A_Rate_For_An_Unsupported_Source_Currency {
		private static final BigDecimal dollarToEuroRate = decimal("0.8901");
		
		private CurrencyExchangeRateMap rates;

		@Before
		public void setRates() {
			this.rates = singleRateMap(USD, EUR, jun15th, dollarToEuroRate);
		}

		@Test
		public void shouldThrowAnException() throws Exception {
			boolean exceptionThrown = false;
			try {
				rates.getExchangeRate(NOK, EUR, jun15th);
			} catch (ExchangeRateNotFoundException e) {
				exceptionThrown = true;
				assertEquals(jun15th, e.getRequestedDate());
				assertEquals(NOK, e.getSourceCurrency());
				assertEquals(EUR, e.getTargetCurrency());
				assertEquals("Unable to find usable currency exchange rate from NOK to EUR on 2008-06-15", e.getMessage());
			}
			assertTrue(exceptionThrown);
		}
	}

	public static class Getting_A_Rate_For_A_Date_Which_Is_Earlier_Than_We_Have {
		private static final BigDecimal dollarToEuroRate = decimal("0.8901");

		private CurrencyExchangeRateMap rates;

		@Before
		public void setRates() {
			this.rates = singleRateMap(USD, EUR, jun15th, dollarToEuroRate);
		}

		@Test
		public void shouldReturnTheEarliestRate() throws Exception {
			assertEquals(dollarToEuroRate, rates.getExchangeRate(USD, EUR, jun14th));
		}
	}

	public static class Getting_An_Identity_Rate {
		private CurrencyExchangeRateMap rates;
		
		@Before
		public void setRates() {
			this.rates = new CurrencyExchangeRateMap();
		}

		@Test
		public void shouldBeOne() throws Exception {
			assertEquals(BigDecimal.ONE, rates.getExchangeRate(EUR, EUR, now()));
		}
	}

	public static class Getting_A_Rate_For_A_Date_Which_Is_Later_Than_We_Have {
		private static final BigDecimal dollarToEuroRate = decimal("0.8901");
		
		private CurrencyExchangeRateMap rates = new CurrencyExchangeRateMap();

		@Before
		public void setRates() {
			this.rates = singleRateMap(USD, EUR, jun14th, dollarToEuroRate);
		}

		@Test
		public void shouldReturnTheLatestRate() throws Exception {
			assertEquals(dollarToEuroRate, rates.getExchangeRate(USD, EUR, jun15th));
		}
	}

	public static class Getting_A_Rate_For_A_Date_Which_Is_Equidistant_Between_Two_Rates {
		private static final BigDecimal lowDollarToEuroRate = decimal("0.8901");
		private static final BigDecimal highDollarToEuroRate = decimal("0.9031");
		
		private CurrencyExchangeRateMap rates;
		

		@Before
		public void setRates() {
			this.rates = new CurrencyExchangeRateMap();
			this.rates.addExchangeRate(USD, EUR, dec31st, lowDollarToEuroRate);
			this.rates.addExchangeRate(USD, EUR, jan2nd, highDollarToEuroRate);
		}

		@Test
		public void shouldReturnTheEarlierRate() throws Exception {
			assertEquals(lowDollarToEuroRate, rates.getExchangeRate(USD, EUR, jan1st));
		}
	}

	public static class Getting_A_Rate_For_A_Date_Which_Is_Between_Two_Rates_But_Closer_To_The_Earlier_Rate {
		private static final BigDecimal lowDollarToEuroRate = decimal("0.8901");
		private static final BigDecimal highDollarToEuroRate = decimal("0.9031");
		
		private CurrencyExchangeRateMap rates;

		@Before
		public void setRates() {
			this.rates = new CurrencyExchangeRateMap();
			this.rates.addExchangeRate(USD, EUR, jun14th, lowDollarToEuroRate);
			this.rates.addExchangeRate(USD, EUR, jun17th, highDollarToEuroRate);
		}

		@Test
		public void shouldReturnTheEarlierRate() throws Exception {
			assertEquals(lowDollarToEuroRate, rates.getExchangeRate(USD, EUR, jun15th));
		}
	}

	public static class Getting_A_Rate_For_A_Date_Which_Is_Between_Two_Rates_But_Closer_To_The_Later_Rate {
		private static final BigDecimal lowDollarToEuroRate = decimal("0.8901");
		private static final BigDecimal highDollarToEuroRate = decimal("0.9031");
		
		private CurrencyExchangeRateMap rates;

		@Before
		public void setRates() {
			this.rates = new CurrencyExchangeRateMap();
			this.rates.addExchangeRate(USD, EUR, jun14th, lowDollarToEuroRate);
			this.rates.addExchangeRate(USD, EUR, jun17th, highDollarToEuroRate);
		}

		@Test
		public void shouldReturnTheEarlierRate() throws Exception {
			assertEquals(highDollarToEuroRate, rates.getExchangeRate(USD, EUR, jun16th));
		}
	}
	
	public static CurrencyExchangeRateMap singleRateMap(Currency source,
			Currency target, DateTime date, BigDecimal rate) {
		CurrencyExchangeRateMap map = new CurrencyExchangeRateMap();
		map.addExchangeRate(source, target, date, rate);
		return map;
	}
}

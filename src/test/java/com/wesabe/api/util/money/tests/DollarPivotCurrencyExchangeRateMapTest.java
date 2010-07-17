package com.wesabe.api.util.money.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import java.util.Currency;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.money.DollarPivotCurrencyExchangeRateMap;

@RunWith(Enclosed.class)
@SuppressWarnings("deprecation")
public class DollarPivotCurrencyExchangeRateMapTest {
	public static class Adding_Exchange_Rates_To_USD {
		@Test
		public void itAddsTheTargetCurrencyToSupportedCurrencies() throws Exception {
			DollarPivotCurrencyExchangeRateMap exchangeRates = new DollarPivotCurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, EUR, jun14th, decimal("1.76"));
			
			assertTrue(exchangeRates.getCurrencies().contains(EUR));
		}
	}
	
	public static class Adding_Exchange_Rates_To_Other_Currencies {
		@Test
		public void itThrowsAnException() throws Exception {
			DollarPivotCurrencyExchangeRateMap exchangeRates = new DollarPivotCurrencyExchangeRateMap();
			
			boolean exceptionThrown = false;
			try {
				exchangeRates.addExchangeRate(EUR, NOK, jun14th, decimal("1.76"));
			} catch (IllegalArgumentException e) {
				exceptionThrown = true;
			}
			
			assertTrue("should have thrown an exception, but didn't", exceptionThrown);
		}
	}
	
	public static class Calculating_Exchange_Rates {
		@Test
		public void itUsesUSDAsAPivotValue() throws Exception {
			DollarPivotCurrencyExchangeRateMap exchangeRates = new DollarPivotCurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, EUR, jun14th, decimal("0.7656"));
			exchangeRates.addExchangeRate(USD, NOK, jun14th, decimal("6.7258"));

			assertEquals(decimal("8.785004"), exchangeRates.getExchangeRate(EUR, NOK, jun14th));
			assertEquals(decimal("0.1138303"), exchangeRates.getExchangeRate(NOK, EUR, jun14th));
		}
		
		@Test
		public void itDoesStraightConversionsIfPossible() throws Exception {
			DollarPivotCurrencyExchangeRateMap exchangeRates = new DollarPivotCurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, GBP, jun14th, decimal("0.6846"));

			assertEquals(decimal("1.460707"), exchangeRates.getExchangeRate(GBP, USD, jun14th));
			assertEquals(decimal("0.6846000"), exchangeRates.getExchangeRate(USD, GBP, jun14th));
		}
		
		@Test
		public void itHandlesVeryDevaluedCurrencies() throws Exception {
			final Currency KRW = Currency.getInstance("KRW");
			DollarPivotCurrencyExchangeRateMap exchangeRates = new DollarPivotCurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, KRW, date(2008, 12, 31), decimal("1262.00"));
			
			assertEquals(decimal("0.0007923930"), exchangeRates.getExchangeRate(KRW, USD, date(2009, 4, 30)));
			assertEquals(decimal("1262.000"), exchangeRates.getExchangeRate(USD, KRW, date(2009, 4, 30)));
		}
	}
}

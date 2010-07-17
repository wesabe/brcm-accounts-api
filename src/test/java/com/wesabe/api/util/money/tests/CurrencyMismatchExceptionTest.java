package com.wesabe.api.util.money.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.money.CurrencyMismatchException;

@RunWith(Enclosed.class)
public class CurrencyMismatchExceptionTest {
	public static class A_Currency_Mismatch_Exception {
		private final CurrencyMismatchException	exception =
			new CurrencyMismatchException("do something", USD, EUR);
		
		@Test
		public void shouldHaveAnErrorMessage() throws Exception {
			assertEquals("Cannot do something USD and EUR amounts.", exception.getMessage());
		}
		
		@Test
		public void shouldHaveAnAction() throws Exception {
			assertEquals("do something", exception.getAction());
		}
		
		@Test
		public void shouldHaveABaseCurrency() throws Exception {
			assertEquals(USD, exception.getBaseCurrency());
		}
		
		@Test
		public void shouldHaveAnOtherCurrency() throws Exception {
			assertEquals(EUR, exception.getOtherCurrency());
		}
	}
}

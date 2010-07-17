package com.wesabe.api.util.money.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.money.MoneyFormat;

@RunWith(Enclosed.class)
public class MoneyFormatTest {
	public static class Formatting_Money_In_The_United_States {
		@Test
		public void itUsesAMinusToIndicateNegativeAmounts() throws Exception {
			assertEquals("-$4.00", MoneyFormat.of(USD, Locale.US).format(decimal("-4.00")));
		}
		
		@Test
		public void itDisplaysEurosUsingTheSymbol() throws Exception {
			assertEquals("-€4.00", MoneyFormat.of(EUR, Locale.US).format(decimal("-4.00")));
		}
		
		@Test
		public void itDisplaysBritishPoundsUsingTheSymbol() throws Exception {
			assertEquals("-£4.00", MoneyFormat.of(GBP, Locale.US).format(decimal("-4.00")));
		}
		
		@Test
		public void itUsesSaneInternationalCurrencySymbols() throws Exception {
			assertEquals("-NKr4.00", MoneyFormat.of(NOK, Locale.US).format(decimal("-4.00")));
		}
	}
	
	public static class Formatting_Money_In_Faroff_Lands {
		@Test
		public void itDoesAsTheRomansDo() throws Exception {
			assertEquals("-€ 4,00", MoneyFormat.of(EUR, Locale.ITALY).format(decimal("-4.00")));
		}
	}
	
	public static class Formatting_Swiss_Francs {
	  @Test
	  public void itDoesNotDoTheirCrazyRounding() throws Exception {  
	    // see http://www-01.ibm.com/software/globalization/topics/locales/numeric_number.jsp
	    assertEquals("-Fr.426.62", MoneyFormat.of(CHF, Locale.US).format(decimal("-426.62")));
	  }
	}
}

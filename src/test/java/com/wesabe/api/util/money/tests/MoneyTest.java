package com.wesabe.api.util.money.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.CurrencyMismatchException;
import com.wesabe.api.util.money.Money;

@RunWith(Enclosed.class)
public class MoneyTest {
	
	public static class Any_Money {
		
		@Test
		public void itAllowsGettingTheValue() {
			Money two = new Money(decimal("2.00"), USD);
			assertEquals(decimal("2.00"), two.getValue());
		}
		
	}

	public static class Zero_Dollars {

		@Test
		public void shouldBeZero() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			assertTrue(zeroDollars.isZero());
		}

		@Test
		public void shouldHaveASignOfZero() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			assertEquals(0, zeroDollars.signum());
		}

		@Test
		public void shouldBeEqualToZeroDollars() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money anotherZeroDollars = new Money(decimal("0.00"), USD);
			assertTrue(zeroDollars.equals(anotherZeroDollars));
		}

		@Test
		public void shouldBeEqualToItself() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			assertTrue(zeroDollars.equals(zeroDollars));
		}

		@Test
		public void shouldNotBeEqualToNull() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			assertFalse(zeroDollars.equals(null));
		}

		@Test
		public void shouldHaveTheSameHashCodeAsZeroDollars() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money anotherZeroDollars = new Money(decimal("0.00"), USD);
			assertEquals(anotherZeroDollars.hashCode(), zeroDollars.hashCode());
		}

		@Test
		public void shouldNotBeEqualToOneDollar() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertFalse(zeroDollars.equals(oneDollar));
		}

		@Test
		public void shouldNotHaveTheSameHashCodeAsOneDollar() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertFalse(oneDollar.hashCode() == zeroDollars.hashCode());
		}

		@Test
		public void shouldNotHaveTheSameHashCodeAsZeroEuros() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money zeroEuros = new Money(decimal("0.00"), EUR);
			assertFalse(zeroEuros.hashCode() == zeroDollars.hashCode());
		}

		@Test
		public void shouldNotBeEqualToZeroEuros() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money zeroEuros = new Money(decimal("0.00"), EUR);
			assertFalse(zeroDollars.equals(zeroEuros));
		}

		@Test
		public void shouldStillBeZeroWhenNegated() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			assertTrue(zeroDollars.equals(zeroDollars.negate()));
		}

		@Test
		public void shouldBeZeroEurosWhenConverted() throws Exception {
			CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money zeroEuros = new Money(decimal("0.00"), EUR);
			assertEquals(zeroEuros, zeroDollars.convert(exchangeRates, EUR, now()));
		}
		
		@Test
		public void itIsLessThanOneDollar() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals(-1, zeroDollars.compareTo(oneDollar));
		}
		
		@Test
		public void itIsMoreThanNegativeOneDollar() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money negativeOneDollar = new Money(decimal("-1.00"), USD);
			assertEquals(1, zeroDollars.compareTo(negativeOneDollar));
		}
		
		@Test
		public void itIsComparableToZeroDollars() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			assertEquals(0, zeroDollars.compareTo(zeroDollars));
		}
		
		@Test
		public void itIsNotComparableToZeroEuros() throws Exception {
			Money zeroDollars = new Money(decimal("0.00"), USD);
			Money zeroEuros = new Money(decimal("0.00"), EUR);
			boolean exceptionThrown = false;
			try {
				zeroDollars.compareTo(zeroEuros);
			} catch (CurrencyMismatchException e) {
				exceptionThrown = true;
			}
			assertTrue(exceptionThrown);
		}
		
		@Test
		public void itIsComparableToOtherMoneyObjects() throws Exception {
			Comparable<Money> comparableMoney = new Money(decimal("0.00"), USD);
			Money negativeOneDollar = new Money(decimal("-1.00"), USD);
			assertEquals(1, comparableMoney.compareTo(negativeOneDollar));
		}
	}

	public static class Negative_One_Dollar {
		@Test
		public void shouldHaveASignOfNegativeOne() throws Exception {
			Money negativeOneDollar = new Money(decimal("-1.00"), USD);
			assertEquals(-1, negativeOneDollar.signum());
		}

		@Test
		public void shoudlHaveAnAbsoluteValueOfOneDollar() throws Exception {
			Money negativeOneDollar = new Money(decimal("-1.00"), USD);
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals(oneDollar, negativeOneDollar.abs());
		}
		
		@Test
		public void shouldRenderInAnArbitraryLocale() throws Exception {
			Money negativeOneDollar = new Money(decimal("-1.00"), USD);
			assertEquals("-1,00 $US", negativeOneDollar.toString(Locale.FRANCE));
		}
		
		@Test
		public void shouldRenderWithoutParenthesesInTheUS() throws Exception {
			Money negativeOneDollar = new Money(decimal("-1.00"), USD);
			assertEquals("-$1.00", negativeOneDollar.toString(Locale.US));
		}
	}

	public static class One_Dollar {
		@Test
		public void shouldNotBeZero() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertFalse(oneDollar.isZero());
		}

		@Test
		public void shouldHaveASignOfOne() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals(1, oneDollar.signum());
		}

		@Test
		public void shouldBeEqualToOneDollar() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money anotherDollar = new Money(decimal("1.00"), USD);
			assertTrue(oneDollar.equals(anotherDollar));
		}

		@Test
		public void shouldNotBeEqualToZeroDollars() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money zeroDollars = new Money(decimal("0.00"), USD);
			assertFalse(oneDollar.equals(zeroDollars));
		}

		@Test
		public void shouldNotBeEqualToOneEuro() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money oneEuro = new Money(decimal("1.00"), EUR);
			assertFalse(oneDollar.equals(oneEuro));
		}

		@Test
		public void shouldNotEqualANonMoneyObject() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertFalse(oneDollar.equals("this is not my beautiful house"));
		}

		@Test
		public void shouldBeNegativeOneDollarWhenNegated() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money minusOneDollar = new Money(decimal("-1.00"), USD);
			assertEquals(minusOneDollar, oneDollar.negate());
		}

		@Test
		public void shoudlHaveAnAbsoluteValueOfOneDollar() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals(oneDollar, oneDollar.abs());
		}
		
		@Test
		public void shouldConvertItselfToAMachineReadableString() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals("1.00", oneDollar.toPlainString());
		}
	}

	public static class One_Dollar_Plus_One_Dollar {

		@Test
		public void shouldBeTwoDollars() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money twoDollars = new Money(decimal("2.00"), USD);
			assertEquals(twoDollars, oneDollar.add(oneDollar));
		}

	}

	public static class Two_Dollars_Minus_One_Dollar {

		@Test
		public void shouldBeOneDollar() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money twoDollars = new Money(decimal("2.00"), USD);
			assertEquals(oneDollar, twoDollars.subtract(oneDollar));
		}

	}

	public static class One_Dollar_Plus_One_Euro {
		@Test
		public void shouldThrowACurrencyMismatchException() throws Exception {
			boolean exceptionThrown = false;
			String exceptionMessage = null;
			
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money oneEuro = new Money(decimal("1.00"), EUR);
			
			try {
				oneDollar.add(oneEuro);
			} catch (CurrencyMismatchException e) {
				exceptionThrown = true;
				exceptionMessage = e.getMessage();
			}
			
			assertTrue(exceptionThrown);
			assertEquals("Cannot add USD and EUR amounts.", exceptionMessage);
		}
	}

	public static class One_Dollar_Minus_One_Euro {
		@Test
		public void shouldThrowACurrencyMismatchException() throws Exception {
			boolean exceptionThrown = false;
			String exceptionMessage = null;
			
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money oneEuro = new Money(decimal("1.00"), EUR);
			
			try {
				oneDollar.subtract(oneEuro);
			} catch (CurrencyMismatchException e) {
				exceptionThrown = true;
				exceptionMessage = e.getMessage();
			}
			
			assertTrue(exceptionThrown);
			assertEquals("Cannot subtract USD and EUR amounts.", exceptionMessage);
		}
	}
	
	public static class One_Dollar_Times_Two {
		@Test
		public void itIsTwoDollars() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money twoDollars = new Money(decimal("2.00"), USD);
			assertEquals(twoDollars, oneDollar.multiply(2));
		}
	}
	
	public static class Two_Dollars_Times_Negative_One {
		@Test
		public void itIsNegativeTwoDollars() throws Exception {
			Money twoDollars = new Money(decimal("2.00"), USD);
			Money negativeTwoDollars = new Money(decimal("-2.00"), USD);
			assertEquals(negativeTwoDollars, twoDollars.multiply(-1));
		}
	}
	
	public static class Ten_Dollars_Times_Zero {
		@Test
		public void itIsZeroDollars() throws Exception {
			Money tenDollars = new Money(decimal("10.00"), USD);
			Money zeroDollars = new Money(decimal("0.00"), USD);
			assertEquals(zeroDollars, tenDollars.multiply(0));
		}
	}

	public static class One_Dollar_As_A_String {
		@SuppressWarnings("deprecation")
		@Test
		public void shouldRenderInTheDefaultLocale() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals(oneDollar.toString(Locale.getDefault()), oneDollar.toString());
		}

		@Test
		public void shouldRenderInAnArbitraryLocale() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals("1,00 $US", oneDollar.toString(Locale.FRANCE));
		}
		
		@Test
		public void shouldRenderAsAMachineReadableString() throws Exception {
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals("1.00", oneDollar.toPlainString());
		}
	}

	public static class One_Dollar_Converted_Into_Dollars {
		@Test
		public void shouldBeOneDollar() throws Exception {
			CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
			Money oneDollar = new Money(decimal("1.00"), USD);
			assertEquals(oneDollar, oneDollar.convert(exchangeRates, USD, now()));
		}
	}

	public static class One_Dollar_Converted_Into_Euros {
		@Test
		public void shouldBeWhateverTheExchangeRateIs() throws Exception {
			CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, EUR, now(), decimal("0.79"));
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money wayLessInEuros = new Money(decimal("0.79"), EUR);
			Money actualValue = oneDollar.convert(exchangeRates, EUR, now());
			assertEquals(wayLessInEuros, actualValue);
		}

		@Test
		public void shouldRoundDownIfLessThanHalf() throws Exception {
			CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, EUR, now(), decimal("0.794"));
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money wayLessInEuros = new Money(decimal("0.79"), EUR);
			Money actualValue = oneDollar.convert(exchangeRates, EUR, now());
			assertEquals(wayLessInEuros, actualValue);
		}

		@Test
		public void shouldRoundUpIfHalf() throws Exception {
			CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, EUR, now(), decimal("0.795"));
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money wayLessInEuros = new Money(decimal("0.80"), EUR);
			Money actualValue = oneDollar.convert(exchangeRates, EUR, now());
			assertEquals(wayLessInEuros, actualValue);
		}

		@Test
		public void shouldRoundUpIfMoreThanHalf() throws Exception {
			CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, EUR, now(), decimal("0.797"));
			Money oneDollar = new Money(decimal("1.00"), USD);
			Money wayLessInEuros = new Money(decimal("0.80"), EUR);
			Money actualValue = oneDollar.convert(exchangeRates, EUR, now());
			assertEquals(wayLessInEuros, actualValue);
		}
	}
}

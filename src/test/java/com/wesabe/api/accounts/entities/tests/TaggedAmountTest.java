package com.wesabe.api.accounts.entities.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.InjectionHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.UnknownCurrencyCodeException;

@RunWith(Enclosed.class)
public class TaggedAmountTest {
	private static final Tag food = new Tag("food");
	
	public static class Converting_An_Amount_Of_Unknown_Currency {
		private Account account = new Account("Checking", USD);
		private Txaction txaction = new Txaction(account, decimal("300.00"), jun15th);
		private TaggedAmount taggedAmount = txaction.addTag(food);
		
		@Before
		public void poisonAccountCurrency() throws Exception {
			inject(Account.class, account, "currencyCode", "!!!");
		}
		
		@Test
		public void itThrowsAnUnknownCurrencyCodeException() throws Exception {
			boolean exceptionThrown = false;
			try {
				taggedAmount.getConvertedAmount(GBP, new CurrencyExchangeRateMap());
			} catch (UnknownCurrencyCodeException e) {
				exceptionThrown = true;
				assertEquals("!!!", e.getCurrencyCode());
			}
			assertTrue(exceptionThrown);
		}
	}
	
	public static class A_Simple_Tag {
		private Account account = new Account("Checking", USD);
		private Txaction txaction = new Txaction(account, decimal("300.00"), jun15th);
		private TaggedAmount taggedAmount = txaction.addTag(food);
		
		@Test
		public void itHasTheSameAmountAsTheTransaction() throws Exception {
			assertEquals(txaction.getAmount(), taggedAmount.getAmount());
		}
		
		@Test
		public void itIsNotASplit() throws Exception {
			assertThat(taggedAmount.isSplit(), is(false));
		}
	}
	
	public static class A_Split_Tag {
		private Account account = new Account("Checking", USD);
		private Txaction txaction = new Txaction(account, decimal("300.00"), jun15th);
		private TaggedAmount taggedAmount = txaction.addTag(food, decimal("40.00"));
		
		@Test
		public void itHasAnId() throws Exception {
			inject(TaggedAmount.class, taggedAmount, "id", 300);
			assertEquals(Integer.valueOf(300), taggedAmount.getId());
		}
		
		@Test
		public void itHasATag() throws Exception {
			assertEquals(new Tag("food"), taggedAmount.getTag());
		}
		
		@Test
		public void itHasAnAmount() throws Exception {
			assertEquals(money("40.00", USD), taggedAmount.getAmount());
		}
		
		@Test
		public void itIsASplit() throws Exception {
			assertThat(taggedAmount.isSplit(), is(true));
		}
		
		@Test
		public void itIsConvertableToOtherCurrencies() throws Exception {
			CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(USD, EUR, jun14th, decimal("0.69"));
			exchangeRates.addExchangeRate(USD, EUR, jun15th, decimal("1.50"));
			exchangeRates.addExchangeRate(USD, EUR, jun16th, decimal("0.90"));
			
			assertEquals(money("60.00", EUR), taggedAmount.getConvertedAmount(EUR, exchangeRates));
		}
	}
}

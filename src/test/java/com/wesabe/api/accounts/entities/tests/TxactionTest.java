package com.wesabe.api.accounts.entities.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.InjectionHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountType;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionStatus;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.UnknownCurrencyCodeException;

@RunWith(Enclosed.class)
public class TxactionTest {
	public static final Tag food = new Tag("food");
	public static final Tag groceries = new Tag("groceries");
	public static final Tag cash = new Tag("cash");
	public static final Tag dining = new Tag("dining");
	public static final Tag lunch = new Tag("lunch");
	public static final Tag transfer = new Tag("transfer");
	
	public static class A_Txaction_In_An_Account_Of_Unknown_Currency {
		private Account account = new Account("Checking", USD);
		private Txaction txaction = new Txaction(account, decimal("300.00"), jun15th);
		
		@Before
		public void poisonAccountCurrency() throws Exception {
			inject(Account.class, account, "currencyCode", "!!!");
		}
		
		@Test
		public void itThrowsAnUnknownCurrencyCodeExceptionWhenGettingTheAmount() throws Exception {
			boolean exceptionThrown = false;
			try {
				txaction.getAmount();
			} catch (UnknownCurrencyCodeException e) {
				exceptionThrown = true;
				assertEquals("!!!", e.getCurrencyCode());
			}
			assertTrue(exceptionThrown);
		}
		
		@Test
		public void itThrowsAnUnknownCurrencyCodeExceptionWhenConvertingTheAmount() throws Exception {
			boolean exceptionThrown = false;
			try {
				txaction.getConvertedAmount(GBP, new CurrencyExchangeRateMap());
			} catch (UnknownCurrencyCodeException e) {
				exceptionThrown = true;
				assertEquals("!!!", e.getCurrencyCode());
			}
			assertTrue(exceptionThrown);
		}
	}
	
	public static class A_Txaction_In_An_Account {
		private final Account euroSavings = new Account("Savings", EUR);
		private final Txaction txaction = new Txaction(euroSavings, decimal("-34.22"), jun15th);
		
		@Test
		public void itHasTheSameCurrencyAsItsAccount() throws Exception {
			assertEquals(money("-34.22", EUR), txaction.getAmount());
		}
		
		@Test
		public void itIsNotATransfer() throws Exception {
			assertFalse(txaction.isTransfer());
		}
		
		@Test
		public void itIsNotDeleted() throws Exception {
			assertFalse(txaction.isDeleted());
		}
		
		@Test
		public void itIsNotDisabled() throws Exception {
			assertFalse(txaction.isDisabled());
		}
		
		@Test
		public void itIsConvertableToOtherCurrencies() throws Exception {
			final CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
			exchangeRates.addExchangeRate(EUR, USD, jun14th, decimal("0.69"));
			exchangeRates.addExchangeRate(EUR, USD, jun15th, decimal("1.50"));
			exchangeRates.addExchangeRate(EUR, USD, jun16th, decimal("0.90"));
			
			assertEquals(money("-51.33", USD), txaction.getConvertedAmount(USD, exchangeRates));
		}
	}
	
	public static class Merchant_And_Unedited_Names {
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Txaction txaction = new Txaction(checking, decimal("-8.20"), now());

		@Before
		public void setup() throws Exception {
			inject(Txaction.class, txaction, "rawName", "BAKESALEBETTY XXXXX0029");
			inject(Txaction.class, txaction, "memo", "OAKLANCA");
		}
		
		@Test
		public void itHasAnUneditedNameEqualToTheRawNameWhenMemoIsBlank() throws Exception {
			inject(Txaction.class, txaction, "memo", " ");
			assertEquals("BAKESALEBETTY XXXXX0029", txaction.getUneditedName());
		}
		
		@Test
		public void itHasAnUneditedNameByJoiningRawNameWithMemo() throws Exception {
			assertEquals("BAKESALEBETTY XXXXX0029 / OAKLANCA", txaction.getUneditedName());
		}
	}
	
	public static class Comparing_A_Txaction_To_Another {
		private Account euroSavings = new Account("Savings", EUR);
		private final Txaction txaction = new Txaction(euroSavings, decimal("-34.22"), jun15th);
		
		@Before
		public void setup() throws Exception {
			txaction.setSequence(1);
			inject(Txaction.class, txaction, "createdAt", jun15th);
		}

		@Test
		public void itComesBeforeAnotherTxactionOnALaterDate() {
			Txaction txactionWithLaterPostedDate = new Txaction(euroSavings, decimal("-34.22"), jun16th);
			assertEquals(-1, txaction.compareTo(txactionWithLaterPostedDate));
			assertEquals(1, txactionWithLaterPostedDate.compareTo(txaction));
		}
		
		@Test
		public void itComesBeforeAnotherTxactionOnTheSameDateWithALaterSequence() {
			Txaction txactionWithLaterSequenceNumber = new Txaction(euroSavings, decimal("-34.22"), jun15th);
			txactionWithLaterSequenceNumber.setSequence(txaction.getSequence()-1);
			assertEquals(-1, txaction.compareTo(txactionWithLaterSequenceNumber));
			assertEquals(1, txactionWithLaterSequenceNumber.compareTo(txaction));
		}
		
		@Test
		public void itIgnoresSequenceWhenOneOrBothTxactionsHaveNullSequences() throws Exception {
			Txaction txactionWithNullSequenceNumber = new Txaction(euroSavings, decimal("-34.22"), jun15th);
			txactionWithNullSequenceNumber.setSequence(null);
			inject(Txaction.class, txactionWithNullSequenceNumber, "createdAt", jun17th);
			assertEquals(-1, txaction.compareTo(txactionWithNullSequenceNumber));
			assertEquals(1, txactionWithNullSequenceNumber.compareTo(txaction));
		}
		
		@Test
		public void itIgnoresCreatedAtDatesWhenOneOrBothTxactionsHaveNullCreatedAtDates() throws Exception {
			Txaction txactionWithNullCreatedAt = new Txaction(euroSavings, decimal("-34.22"), jun15th);
			inject(Txaction.class, txactionWithNullCreatedAt, "createdAt", null);
			assertEquals(0, txaction.compareTo(txactionWithNullCreatedAt));
			assertEquals(0, txactionWithNullCreatedAt.compareTo(txaction));
		}
		
		@Test
		public void itComesBeforeAnotherTxactionOnTheSameDateWithTheSameSequenceWithALaterCreationDate() throws Exception {
			Txaction txactionWithLaterCreationDate = new Txaction(euroSavings, decimal("-34.22"), jun15th);
			txactionWithLaterCreationDate.setSequence(txaction.getSequence());
			inject(Txaction.class, txactionWithLaterCreationDate, "createdAt", jun17th);
			assertEquals(-1, txaction.compareTo(txactionWithLaterCreationDate));
			assertEquals(1, txactionWithLaterCreationDate.compareTo(txaction));
		}
	}
	
	public static class Tagging_A_Transaction {
		final Account account = new Account("checking", USD);
		final Txaction txaction = new Txaction(account, decimal("-34.22"), now());
		
		@Test
		public void itAddsATaggedAmountToTheTransaction() throws Exception {
			final TaggedAmount foodAmount = txaction.addTag(food);
			assertEquals(ImmutableList.of(foodAmount), txaction.getTaggedAmounts());
		}
		
		@Test
		public void itAddsASplitTaggedAmountToTheTransaction() throws Exception {
			final TaggedAmount splitAmount = txaction.addTag(food, decimal("-30.00"));
			assertEquals(ImmutableList.of(splitAmount), txaction.getTaggedAmounts());
		}
	}
	
	public static class With_A_Transfer_Txaction {
		private final Account account = new Account("checking", USD);
		private final Txaction txaction = new Txaction(account, decimal("-34.22"), now());
		
		@Test
		public void itIsATransfer() throws Exception {
			txaction.setTransferTxaction(new Txaction());
			assertTrue(txaction.isTransfer());
		}
	}
	
	public static class With_A_Status_Of_One {
		private final Account account = new Account("checking", USD);
		private final Txaction txaction = new Txaction(account, decimal("-34.22"), now());
		
		@Test
		public void itIsDeleted() throws Exception {
			txaction.setStatus(TxactionStatus.DELETED);
			assertTrue(txaction.isDeleted());
		}
	}
	
	public static class With_A_Status_Of_Five {
		private final Account account = new Account("checking", USD);
		private final Txaction txaction = new Txaction(account, decimal("-34.22"), now());
		
		@Test
		public void itIsDeleted() throws Exception {
			txaction.setStatus(TxactionStatus.DISABLED);
			assertTrue(txaction.isDisabled());
		}
	}
	
	public static class Calculating_Filtered_Amounts_With_No_Tags {
		private final Account account = new Account("checking", USD);
		
		@Test
		public void itDoesntFilterAnyPartOfANegativeAmount() throws Exception {
			final Txaction txaction = new Txaction(account, decimal("-34.22"), now());
			assertEquals(money("-34.22", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(food)));
			assertEquals(money("-34.22", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(groceries, food)));
		}
		
		@Test
		public void itDoesntFilterAnyPartOfAPositiveAmount() throws Exception {
			final Txaction txaction = new Txaction(account, decimal("34.22"), now());
			assertEquals(money("34.22", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(food)));
			assertEquals(money("34.22", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(groceries, food)));
		}
	}
	
	public static class Calculating_Filtered_Amounts_Without_Splits {
		private Account account;
		private Txaction txaction;
		
		@Before
		public void setup() {
			account = new Account("checking", USD);
			txaction = new Txaction(account, decimal("-34.22"), now());
			txaction.addTag(food);
			txaction.addTag(groceries);
		}
		
		@Test
		public void itReturnsZeroIfAnyTaggedAmountIsFiltered() throws Exception {
			assertEquals(money("0.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(groceries)));
			assertEquals(money("0.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(food)));
			assertEquals(money("0.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(food, groceries)));
		}
		
		@Test
		public void itReturnsTheFullAmountIfNoTaggedAmountsAreFiltered() throws Exception {
			assertEquals(money("-34.22", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(cash)));
		}
	}
	
	public static class Calculating_Filtered_Amounts_With_Splits {
		private Account account;
		private Txaction txaction;
		
		@Before
		public void setup() {
			account = new Account("checking", USD);
			txaction = new Txaction(account, decimal("-34.22"), now());
			txaction.addTag(cash, decimal("-20.00"));
			txaction.addTag(groceries, decimal("-14.22"));
		}
		
		@Test
		public void itReturnsTheTransactionAmountMinusTheAmountOfTheFilteredSplits() throws Exception {
			assertEquals(money("-20.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(groceries)));
		}
		
		@Test
		public void itReturnsZeroWhenTheSumOfTheFilteredAmountsEqualsTheTransactionAmount() throws Exception {
			assertEquals(money("0.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(groceries, cash)));
		}
	}
	
	public static class Calculating_Filtered_Amounts_With_Overunity_Splits {
		private Account account = new Account("checking", USD);
		private Txaction txaction;
		
		@Before
		public void setup() {
			txaction = new Txaction(account, decimal("-40.00"), now());
			txaction.addTag(transfer, decimal("-20.00"));
			txaction.addTag(lunch, decimal("-20.00"));
			txaction.addTag(dining, decimal("-20.00"));
			txaction.addTag(food, decimal("-20.00"));
		}
		
		@Test
		public void itReturnsTheTransactionAmountMinusTheAmountOfTheFilteredSplits() throws Exception {
			assertEquals(money("-20.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(transfer)));
		}
		
		@Test
		public void itReturnsZeroIfFilteredSplitsAreMoreThanTransactionAmount() throws Exception {
			assertEquals(money("0.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(lunch, dining, food)));
		}
		
		@Test
		public void itReturnsZeroIfFilteredSplitsEqualTransactionAmount() throws Exception {
			assertEquals(money("0.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(lunch, dining)));
		}
	}
	
	public static class Calculating_Positive_Filtered_Amounts_With_Splits {
		private Account account;
		private Txaction txaction;
		
		@Before
		public void setup() {
			account = new Account("checking", USD);
			txaction = new Txaction(account, decimal("34.22"), now());
			txaction.addTag(cash, decimal("20.00"));
			txaction.addTag(groceries, decimal("14.22"));
		}
		
		@Test
		public void itReturnsTheTransactionAmountMinusTheAmountOfTheFilteredSplits() throws Exception {
			assertEquals(money("20.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(groceries)));
		}
		
		@Test
		public void itReturnsZeroWhenTheSumOfTheFilteredAmountsEqualsTheTransactionAmount() throws Exception {
			assertEquals(money("0.00", USD), txaction.getAmountByFilteringTags(Sets.newHashSet(groceries, cash)));
		}
	}
}

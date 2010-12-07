package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.InjectionHelper.*;
import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wesabe.api.accounts.analytics.TxactionListBuilder;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.AccountType;
import com.wesabe.api.accounts.entities.Merchant;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.entities.TxactionStatus;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class TxactionListBuilderTest {
	public static class A_Builder_Without_Any_Explicit_Filters {
		private List<Txaction> txactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), apr1st);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction deleted = new Txaction(checking, decimal("-20.00"), jun14th);
		private Txaction disabled = new Txaction(checking, decimal("500.00"), jun16th);
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
		
		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			deleted.setStatus(TxactionStatus.DELETED);
			disabled.setStatus(TxactionStatus.DISABLED);
			txactions = ImmutableList.of(starbucks, disabled, wholeFoods, deleted);
		}

		@Test
		public void itFiltersDeletedAndDisabledTransactions() throws Exception {
			assertEquals(Lists.newArrayList(starbucks, wholeFoods), new TxactionListBuilder()
																		.setCurrency(USD)
																		.setCurrencyExchangeRateMap(exchangeRates)
																		.setAccounts(new AccountList(checking))
																		.build(txactions)
																		.getTxactions());
		}
		
		@Test
		public void itDoesNotIncludeTheDeletedOrDisabledTransactionsInTheTotalCount() throws Exception {
			final TxactionList list = new TxactionListBuilder()
											.setCurrency(USD)
											.setCurrencyExchangeRateMap(exchangeRates)
											.setAccounts(new AccountList(checking))
											.build(txactions);

			assertEquals(2, list.getTotalCount());
		}
	}
	
	public static class A_Builder_With_Accounts {
		private List<Txaction> txactions;
		private List<Txaction> checkingTxactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			
			txactions = ImmutableList.of(starbucks, interestEarned, wholeFoods);
			checkingTxactions = Lists.newArrayList(starbucks, wholeFoods);
		}

		@Test
		public void itReturnsTxactionsContainedByTheGivenAccounts() {
			assertEquals(checkingTxactions, new TxactionListBuilder()
												.setAccounts(new AccountList(checking))
												.setCurrency(USD)
												.setCurrencyExchangeRateMap(exchangeRates)
												.build(txactions)
												.getTxactions());
		}
		
		@Test
		public void itDoesNotIncludeOtherAccountsInTheTotalCount() throws Exception {
			final TxactionList list = new TxactionListBuilder()
										.setAccounts(new AccountList(checking))
										.setCurrency(USD)
										.setCurrencyExchangeRateMap(exchangeRates)
										.build(txactions);
			assertEquals(2, list.getTotalCount());
		}
	}
	
	public static class A_Builder_With_Tags {
		private List<Txaction> txactions;
		private List<Txaction> foodTxactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private Tag food = new Tag("food");
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			
			txactions = ImmutableList.of(starbucks, interestEarned, wholeFoods);
			
			starbucks.addTag(new Tag("food"));
			wholeFoods.addTag(new Tag("food"));
			
			foodTxactions = Lists.newArrayList(starbucks, wholeFoods);
		}

		@Test
		public void itReturnsTxactionsContainedByTheGivenAccounts() {
			assertEquals(foodTxactions, new TxactionListBuilder()
												.setTags(ImmutableSet.of(food))
												.setAccounts(new AccountList(checking, savings))
												.setCurrency(USD)
												.setCurrencyExchangeRateMap(exchangeRates)
												.build(txactions)
												.getTxactions());
		}
		
		@Test
		public void itDoesNotIncludeOtherTagsInTheTotalCount() throws Exception {
			final TxactionList list = new TxactionListBuilder()
											.setTags(ImmutableSet.of(food))
											.setAccounts(new AccountList(checking, savings))
											.setCurrency(USD)
											.setCurrencyExchangeRateMap(exchangeRates)
											.build(txactions);
			assertEquals(2, list.getTotalCount());
		}
		
		@Test
		public void itDoesNotCalculateARunningTotalBalance() throws Exception {
			assertNull(new TxactionListBuilder()
								.setTags(ImmutableSet.of(food))
								.setAccounts(new AccountList(checking, savings))
								.setCurrency(USD)
								.setCurrencyExchangeRateMap(exchangeRates)
								.build(txactions)
								.get(0).getBalance());
		}
	}
	
	public static class A_Builder_Without_Balances {
		private List<Txaction> txactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			
			txactions = ImmutableList.of(starbucks, interestEarned, wholeFoods);
		}
		
		@Test
		public void itDoesNotCalculateARunningTotalBalance() throws Exception {
			assertNull(new TxactionListBuilder()
								.setCalculateBalances(false)
								.setAccounts(new AccountList(checking, savings))
								.setCurrency(USD)
								.setCurrencyExchangeRateMap(exchangeRates)
								.build(txactions)
								.get(0).getBalance());
		}
	}
	
	public static class A_Builder_With_Merchant_Names {
		private List<Txaction> txactions;
		private List<Txaction> expectedTxactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private Merchant wholeFoodsMerchant, starbucksMerchant;
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			this.starbucksMerchant = new Merchant("Starbucks");
			this.wholeFoodsMerchant = new Merchant("Whole Foods");
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			starbucks.setMerchant(starbucksMerchant);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setMerchant(wholeFoodsMerchant);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			
			txactions = ImmutableList.of(starbucks, interestEarned, wholeFoods);
			
			expectedTxactions = Lists.newArrayList(wholeFoods);
		}

		@Test
		public void itReturnsTxactionsForTheGivenMerchants() {
			assertEquals(expectedTxactions, new TxactionListBuilder()
												.setAccounts(new AccountList(checking, savings))
												.setMerchantNames(ImmutableSet.of("Whole Foods"))
												.setCurrency(USD)
												.setCurrencyExchangeRateMap(exchangeRates)
												.build(txactions)
												.getTxactions());
		}
		
		@Test
		public void itDoesNotIncludeOtherMerchantsInTheTotalCount() throws Exception {
			final TxactionList list = new TxactionListBuilder()
											.setAccounts(new AccountList(checking, savings))
											.setMerchantNames(ImmutableSet.of("Whole Foods"))
											.setCurrency(USD)
											.setCurrencyExchangeRateMap(exchangeRates)
											.build(txactions);
			assertEquals(1, list.getTotalCount());
		}
		
		@Test
		public void itDoesNotCalculateARunningTotalBalance() throws Exception {
			assertNull(new TxactionListBuilder()
								.setMerchantNames(ImmutableSet.of("Whole Foods"))
								.setAccounts(new AccountList(checking, savings))
								.setCurrency(USD)
								.setCurrencyExchangeRateMap(exchangeRates)
								.build(txactions)
								.get(0).getBalance());
		}
	}
	
	public static class A_Builder_With_Unedited_Txactions {
		private List<Txaction> txactions;
		private List<Txaction> expectedTxactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction taggedWholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction untaggedStarbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private Txaction transferToSavings = new Txaction(checking, decimal("1000"), new DateTime());
		private Txaction transferFromChecking = new Txaction(savings, decimal("1000"), new DateTime());
		private Merchant wholeFoodsMerchant, starbucksMerchant, transferMerchant;
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			this.starbucksMerchant = new Merchant("Starbucks");
			this.wholeFoodsMerchant = new Merchant("Whole Foods");
			this.transferMerchant = new Merchant("Transfer");
			
			untaggedStarbucks.setStatus(TxactionStatus.ACTIVE);
			untaggedStarbucks.setTagged(false);
			untaggedStarbucks.setMerchant(starbucksMerchant);
			taggedWholeFoods.setStatus(TxactionStatus.ACTIVE);
			taggedWholeFoods.setTagged(true);
			taggedWholeFoods.setMerchant(wholeFoodsMerchant);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			interestEarned.setTagged(false);
			transferToSavings.setMerchant(transferMerchant);
			transferToSavings.setTransferTxaction(transferFromChecking);
			transferFromChecking.setTransferTxaction(transferToSavings);
			
			txactions = ImmutableList.of(untaggedStarbucks, interestEarned, taggedWholeFoods, transferToSavings, transferFromChecking);
		}

		@Test
		public void itIncludesAllTxactionsIfUneditedIsFalse() {
			final TxactionList list = new TxactionListBuilder()
											.setAccounts(new AccountList(checking, savings))
											.setUnedited(false)
											.setCurrency(USD)
											.setCurrencyExchangeRateMap(exchangeRates)
											.build(txactions);
			assertEquals(5, list.getTotalCount());
		}

		@Test
		public void itIncludesNonTransferTxactionsWithNoMerchantOrNoTagsIfUneditedIsTrue() {
			expectedTxactions = Lists.newArrayList(transferFromChecking, interestEarned, untaggedStarbucks);
			final TxactionList list = new TxactionListBuilder()
											.setAccounts(new AccountList(checking, savings))
											.setUnedited(true)
											.setCurrency(USD)
											.setCurrencyExchangeRateMap(exchangeRates)
											.build(txactions);
			assertEquals(expectedTxactions, list.getTxactions());
		}
	}
	
	public static class A_Builder_With_An_Offset {
		private List<Txaction> txactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			
			txactions = ImmutableList.of(interestEarned, starbucks, wholeFoods);
		}

		@Test
		public void itReturnsTxactionsExcludingTheOffsetOnes() {
			assertEquals(Lists.newArrayList(starbucks, wholeFoods), new TxactionListBuilder()
												.setOffset(1)
												.setAccounts(new AccountList(checking, savings))
												.setCurrency(USD)
												.setCurrencyExchangeRateMap(exchangeRates)
												.build(txactions)
												.getTxactions());
		}
		
		@Test
		public void itIncludesTheOffsetTransactionsInTheTotalCount() throws Exception {
			final TxactionList list = new TxactionListBuilder()
										.setOffset(1)
										.setAccounts(new AccountList(checking, savings))
										.setCurrency(USD)
										.setCurrencyExchangeRateMap(exchangeRates)
										.build(txactions);
			assertEquals(3, list.getTotalCount());
		}
	}
	
	public static class A_Builder_With_A_Limit {
		private List<Txaction> txactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			
			txactions = ImmutableList.of(interestEarned, starbucks, wholeFoods);
		}

		@Test
		public void itReturnsTxactionsExcludingTheOffsetOnes() {
			assertEquals(Lists.newArrayList(interestEarned, starbucks), new TxactionListBuilder()
												.setLimit(2)
												.setAccounts(new AccountList(checking, savings))
												.setCurrency(USD)
												.setCurrencyExchangeRateMap(exchangeRates)
												.build(txactions)
												.getTxactions());
		}
		
		@Test
		public void itIncludesTheLimitedTransactionsInTheTotalCount() throws Exception {
			final TxactionList list = new TxactionListBuilder()
											.setOffset(1)
											.setAccounts(new AccountList(checking, savings))
											.setCurrency(USD)
											.setCurrencyExchangeRateMap(exchangeRates)
											.build(txactions);
			assertEquals(3, list.getTotalCount());
		}
	}
	
	public static class A_Builder_With_An_Amount {
		private List<Txaction> txactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			
			txactions = ImmutableList.of(interestEarned, starbucks, wholeFoods);
		}

		@Test
		public void itReturnsTxactionsWithTheCorrectAmount() {
			final TxactionList list = new TxactionListBuilder()
											.setAmount(decimal("-3.00"))
											.setCurrency(USD)
											.setCurrencyExchangeRateMap(exchangeRates)
											.build(txactions);
			assertEquals(ImmutableList.of(starbucks), list.getTxactions());
		}
	}
	public static class A_Builder_With_A_Query {
		private List<Txaction> txactions;
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		private Txaction wholeFoods = new Txaction(checking, decimal("-48.19"), jun14th);
		private Txaction starbucks = new Txaction(checking, decimal("-3.00"), jun15th);
		private Txaction interestEarned = new Txaction(savings, decimal("23.01"), new DateTime());
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void setup() throws Exception {
			checking.setCurrency(USD);
			savings.setCurrency(USD);
			
			inject(Account.class, checking, "accountBalances", Sets.newHashSet(new AccountBalance(checking, decimal("100.00"), new DateTime())));
			inject(Account.class, savings, "accountBalances", Sets.newHashSet(new AccountBalance(savings, decimal("100.00"), new DateTime())));
			
			starbucks.setStatus(TxactionStatus.ACTIVE);
			wholeFoods.setStatus(TxactionStatus.ACTIVE);
			interestEarned.setStatus(TxactionStatus.ACTIVE);
			
			txactions = ImmutableList.of(interestEarned, starbucks, wholeFoods);
		}
				
		private TxactionList buildTxactionList(String query) {
			final TxactionList list = new TxactionListBuilder()
											.setQuery(query)
											.setCurrency(USD)
											.setCurrencyExchangeRateMap(exchangeRates)
											.build(txactions);
			return list;
		}
		
		@Test
		public void itReturnsTxactionsWithFilteredNamesContainingTheQuery() throws Exception {
			inject(Txaction.class, starbucks, "filteredName", "Starbucks San Francis");
			
			final TxactionList list = buildTxactionList("Starbucks");
			assertEquals(ImmutableList.of(starbucks), list.getTxactions());
		}

		@Test
		public void itReturnsTxactionsWithMerchantNamesContainingTheQuery() throws Exception {
			starbucks.setMerchant(new Merchant("Starbucks"));
			
			final TxactionList list = buildTxactionList("Starbucks");
			assertEquals(ImmutableList.of(starbucks), list.getTxactions());
		}
		
		@Test
		public void itReturnsTxactionsWithTagNamesContainingTheQuery() throws Exception {
			inject(Txaction.class, starbucks, "taggedAmounts", ImmutableList.of(new TaggedAmount(starbucks, new Tag("snack"), null)));
			
			final TxactionList list = buildTxactionList("Snack");
			assertEquals(ImmutableList.of(starbucks), list.getTxactions());
		}
		
		@Test
		public void itReturnsTxactionsWithNotesContainingTheQuery() throws Exception {
			inject(Txaction.class, starbucks, "note", "MUFFINS OH JOY");
			
			final TxactionList list = buildTxactionList("muffins");
			assertEquals(ImmutableList.of(starbucks), list.getTxactions());
		}
	}
}

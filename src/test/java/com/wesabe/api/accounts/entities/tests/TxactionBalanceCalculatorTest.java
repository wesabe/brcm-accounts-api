package com.wesabe.api.accounts.entities.tests;

import static com.wesabe.api.tests.util.NumberHelper.*;
import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.InjectionHelper.*;

import java.util.Currency;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.api.accounts.entities.AccountType;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionBalanceCalculator;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class TxactionBalanceCalculatorTest {
	public static class Calculating_Balances_For_A_Single_Account {
		private Account account = Account.ofType(AccountType.CHECKING);
		private AccountBalance accountBalance = new AccountBalance(account, decimal("100.00"), now().minusHours(6));
		private Txaction txaction = new Txaction(account, decimal("-10.00"), now().minusDays(1));
		
		private Set<Account> accounts;
		private List<Txaction> txactions;
		private Set<AccountBalance> accountBalances;
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
		
		@Before
		public void setup() {
			exchangeRates.addExchangeRate(EUR, USD, new DateTime(), decimal("0.79"));
			
			account.setCurrency(USD);
			
			accountBalances = Sets.newHashSet(accountBalance);
			accounts = Sets.newHashSet(account);
			txactions = Lists.newArrayList(txaction);
		}
		
		public TxactionBalanceCalculator calculator(Currency targetCurrency) throws Exception {
			inject(Account.class, account, "accountBalances", accountBalances);
			inject(Account.class, account, "txactions", Sets.newHashSet(txactions));
			return new TxactionBalanceCalculator(accounts, txactions, targetCurrency, exchangeRates);
		}
		
		private TxactionBalanceCalculator calculator() throws Exception {
			return calculator(USD);
		}

		@Test
		public void itUsesTheAccountBalanceAsTheTxactionBalance() throws Exception {
			assertEquals(accountBalance.getBalance(), calculator().getBalance(txaction));
		}
		
		@Test
		public void itIncludesTxactionsAfterTheMostRecentAccountBalance() throws Exception {
			Txaction firstTxaction = new Txaction(account, decimal("-5.00"), now());
			txactions.add(firstTxaction);
			
			TxactionBalanceCalculator calculator = calculator();
			assertEquals(money("95.00", USD), calculator.getBalance(firstTxaction));
			assertEquals(money("100.00", USD), calculator.getBalance(txaction));
		}
		
		@Test
		public void itUsesNowAsTheBalanceDateIfThereAreNoAccountBalances() throws Exception {
			accountBalances = Sets.newHashSet();
			inject(Account.class, account, "balance", decimal("100.00"));
			
			assertEquals(money("126.58", EUR), calculator(EUR).getBalance(txaction));
		}
		
		@Test
		public void itIgnoresAllButTheFirstAccountBalance() throws Exception {
			for (int i = 0; i < 10; i++) {
				txactions.add(new Txaction(account, decimal("-5.00"), now().minusDays(20+i)));
			}
			// create a balance in the middle of those txactions
			accountBalances.add(new AccountBalance(account, decimal("5000"), now().minusDays(25)));
			
			TxactionBalanceCalculator calculator = calculator();
			assertEquals(money("100.00", USD), calculator.getBalance(txactions.get(0)));
			assertEquals(money("110.00", USD), calculator.getBalance(txactions.get(1)));
			assertEquals(money("115.00", USD), calculator.getBalance(txactions.get(2)));
			assertEquals(money("120.00", USD), calculator.getBalance(txactions.get(3)));
			assertEquals(money("125.00", USD), calculator.getBalance(txactions.get(4)));
			assertEquals(money("130.00", USD), calculator.getBalance(txactions.get(5)));
			assertEquals(money("135.00", USD), calculator.getBalance(txactions.get(6)));
			assertEquals(money("140.00", USD), calculator.getBalance(txactions.get(7)));
			assertEquals(money("145.00", USD), calculator.getBalance(txactions.get(8)));
			assertEquals(money("150.00", USD), calculator.getBalance(txactions.get(9)));
			assertEquals(money("155.00", USD), calculator.getBalance(txactions.get(10)));
		}
	}

	public static class Calculating_Balances_For_Multiple_Accounts {
		private Account checking = Account.ofType(AccountType.CHECKING);
		private Account credit = Account.ofType(AccountType.CREDIT_CARD);
		private Account cash = Account.ofType(AccountType.CASH);
		private Account savings = Account.ofType(AccountType.SAVINGS);
		
		private Set<AccountBalance> checkingBalances = Sets.newHashSet(
				new AccountBalance(checking, decimal("100.00"), now().minusHours(6)),
				new AccountBalance(checking, decimal("1000.00"), now().minusMonths(4)));
		private Set<AccountBalance> creditBalances = Sets.newHashSet(
				new AccountBalance(credit, decimal("-50.00"), now().minusHours(5)),
				new AccountBalance(credit, decimal("0.00"), now().minusDays(20)));
		private Set<AccountBalance> savingsBalances = Sets.newHashSet(
				new AccountBalance(savings, decimal("1000.00"), now().minusDays(2)),
				new AccountBalance(savings, decimal("300.00"), now().minusMonths(3)));
		
		private Set<Account> accounts;
		private List<Txaction> txactions, checkingTxactions, creditTxactions, cashTxactions, savingsTxactions;
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
		
		private Txaction newerThanFirstBalance;
		private Txaction firstTxactionPastFirstBalance;
		private Txaction secondTxactionPastFirstBalance;
		private Txaction thirdTxactionPastFirstBalance;
		private Txaction cashTxaction;
		private Txaction fifthTxactionPastFirstBalance;
		private Txaction sixthTxactionPastFirstBalance;
		private Txaction firstTxactionAfterCreditIsReset;
		private Txaction lastTxaction;
		
		@Before
		public void setup() {
			exchangeRates.addExchangeRate(EUR, USD, now(), decimal("0.8"));
			
			checking.setCurrency(USD);
			credit.setCurrency(USD);
			cash.setCurrency(USD);
			savings.setCurrency(EUR);
			
			checkingTxactions = Lists.newArrayList(
					newerThanFirstBalance = new Txaction(checking, decimal("-10.00"), now()),
					firstTxactionPastFirstBalance = new Txaction(checking, decimal("-10.00"), now().minusDays(1)));
			creditTxactions = Lists.newArrayList(
					secondTxactionPastFirstBalance = new Txaction(credit, decimal("-20.00"), now().minusDays(2)),
					thirdTxactionPastFirstBalance = new Txaction(credit, decimal("-20.00"), now().minusDays(3)),
					firstTxactionAfterCreditIsReset = new Txaction(credit, decimal("400.00"), now().minusDays(21)));
			cashTxactions = Lists.newArrayList(
					cashTxaction = new Txaction(cash, decimal("-2.00"), now().minusDays(4)));
			savingsTxactions = Lists.newArrayList(
					fifthTxactionPastFirstBalance = new Txaction(savings, decimal("100.00"), now().minusDays(8)),
					sixthTxactionPastFirstBalance = new Txaction(savings, decimal("100.00"), now().minusDays(15)),
					lastTxaction = new Txaction(savings, decimal("100.00"), now().minusDays(200)));
			
			accounts = Sets.newHashSet(checking, credit, cash, savings);
		}
		
		public TxactionBalanceCalculator calculator() throws Exception {
			txactions = Lists.newArrayList();
			txactions.addAll(checkingTxactions);
			txactions.addAll(creditTxactions);
			txactions.addAll(savingsTxactions);
			txactions.addAll(cashTxactions);
			
			inject(Account.class, checking, "accountBalances", checkingBalances);
			inject(Account.class, checking, "txactions", Sets.newHashSet(checkingTxactions));
			inject(Account.class, credit, "accountBalances", creditBalances);
			inject(Account.class, credit, "txactions", Sets.newHashSet(creditTxactions));
			inject(Account.class, cash, "accountBalances", Sets.newHashSet());
			inject(Account.class, cash, "txactions", Sets.newHashSet(cashTxactions));
			inject(Account.class, savings, "accountBalances", savingsBalances);
			inject(Account.class, savings, "txactions", Sets.newHashSet(savingsTxactions));
			
			return new TxactionBalanceCalculator(accounts, txactions, USD, exchangeRates);
		}
		
		@Test
		public void itHasABalanceForEachTxaction() throws Exception {
			TxactionBalanceCalculator calculator = calculator();
			
			for (Txaction txaction : txactions) {
				assertNotNull(calculator.getBalance(txaction));
			}
		}
		
		@Test
		public void itHasTheCorrectBalancesForAllTxactions() throws Exception {
			TxactionBalanceCalculator calculator = calculator();
			
			// initial balances:
			//   checking =   90 USD
			//   credit   =  -50 USD
			//   cash     =  n/a
			//   savings  =  800 USD (1000 EUR * 0.8 USD / EUR)
			//   total    =  840 USD
			
			assertEquals(money("840.00", USD), calculator.getBalance(newerThanFirstBalance));
			// adjust checking balance: 90 - (-10) => 100 (total 850)
			
			assertEquals(money("850.00", USD), calculator.getBalance(firstTxactionPastFirstBalance));
			// adjust checking balance: 100 - (-10) => 110 (total 860)
			
			assertEquals(money("860.00", USD), calculator.getBalance(secondTxactionPastFirstBalance));
			// adjust credit balance: -50 - (-20) => -30 (total 880)
			
			assertEquals(money("880.00", USD), calculator.getBalance(thirdTxactionPastFirstBalance));
			// adjust credit balance: -30 - (-20) => -10 (total 900)
			
			assertEquals(money("900.00", USD), calculator.getBalance(cashTxaction));
			// REVIEW <brian@wesabe.com> 2009-04-08: Should the balance for cash transactions be null?
			// no adjustment of balance (total 900)
			
			assertEquals(money("900.00", USD), calculator.getBalance(fifthTxactionPastFirstBalance));
			// adjust savings balance: 1000 EUR - (100 EUR) => 900 EUR => 720 USD (total 820)
			
			assertEquals(money("820.00", USD), calculator.getBalance(sixthTxactionPastFirstBalance));
			// adjust savings balance: 900 EUR - (100 EUR) => 800 EUR => 640 USD (total 740)
			
			assertEquals(money("740.00", USD), calculator.getBalance(firstTxactionAfterCreditIsReset));
			// adjust credit balance: 0 - (400) => -400 (total 340)
			
			assertEquals(money("340.00", USD), calculator.getBalance(lastTxaction));
			// adjust savings balance: 800 EUR - (100 EUR) => 700 EUR => 560 USD (total 260)
		}
	}
}

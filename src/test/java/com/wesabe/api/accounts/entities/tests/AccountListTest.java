package com.wesabe.api.accounts.entities.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.InjectionHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountGroup;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.AccountStatus;
import com.wesabe.api.accounts.entities.AccountType;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class AccountListTest {
	public static class Calculating_A_Total_Balance {
		private Account checking = new Account("Checking", USD);
		private Account savings = new Account("Savings", USD);
		private Account credit = new Account("Credit", USD);
		private Account euroSavings = new Account("Euro Savings", EUR);
		private Account my401k = new Account("401k", USD);
		private List<Account> accounts = ImmutableList.of(checking, savings, credit, euroSavings, my401k);
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
		
		@Before
		public void injectBalances() throws Exception {
			inject(Account.class, checking, "balance", decimal("300.12"));
			inject(Account.class, savings, "balance", decimal("4391.87"));
			inject(Account.class, credit, "balance", decimal("-332.24"));
			inject(Account.class, euroSavings, "balance", decimal("254.19"));
			inject(Account.class, my401k, "balance", decimal("10927.85"));
			exchangeRates.addExchangeRate(EUR, USD, new DateTime(), decimal("0.79"));
		}
		
		@Test
		public void itSumsAccountBalancesIntoASingleTotal() throws Exception {
			assertEquals(
				money("15488.41", USD),
				new AccountList(accounts).getTotal(USD, exchangeRates)
			);
		}
		
		@Test
		public void itIgnoresArchivedAndDeletedAccounts() throws Exception {
			my401k.setStatus(AccountStatus.ARCHIVED);
			
			assertEquals(
				money("4560.56", USD),
				new AccountList(accounts).getTotal(USD, exchangeRates)
			);
		}
	}
	
	public static class Grouping_Accounts {
		private Account checking = new Account("Checking", USD);
		private Account savings = new Account("Savings", USD);
		private Account cash = new Account("Cash", USD);
		private Account manual = new Account("Manual", EUR);
		private Account oldChecking = new Account("Old Checking", USD);
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();

		@Before
		public void injectBalances() throws Exception {
			inject(Account.class, checking, "balance", decimal("300.12"));
			checking.setAccountType(AccountType.CHECKING);
			
			inject(Account.class, savings, "balance", decimal("4391.87"));
			savings.setAccountType(AccountType.SAVINGS);
			
			cash.setAccountType(AccountType.CASH);
			
			inject(Account.class, manual, "balance", decimal("254.19"));
			manual.setAccountType(AccountType.MANUAL);
			
			inject(Account.class, oldChecking, "balance", decimal("0.00"));
			oldChecking.setAccountType(AccountType.CHECKING);
			oldChecking.setStatus(AccountStatus.ARCHIVED);
			
			exchangeRates.addExchangeRate(EUR, USD, new DateTime(), decimal("0.79"));
		}
		
		@Test
		public void itSplitsAccountsIntoGroupsByType() {
			assertEquals(
					Lists.newArrayList(
							new AccountGroup("Checking", "checking", new AccountList(checking)),
							new AccountGroup("Savings", "savings", new AccountList(savings))),
					new AccountList(checking, savings).getAccountGroups());
		}
		
		@Test
		public void itConsidersCashAndManualAccountsToBelongToTheSameGroup() {
			assertEquals(
					Lists.newArrayList(
							new AccountGroup("Cash", "cash", new AccountList(cash, manual))),
					new AccountList(cash, manual).getAccountGroups());
		}
		
		@Test
		public void itGroupsArchivedAccountsTogether() throws Exception {
			assertEquals(
					Lists.newArrayList(
							new AccountGroup("Checking", "checking", new AccountList(checking)),
							new AccountGroup("Savings", "savings", new AccountList(savings)),
							new AccountGroup("Archived", "archived", new AccountList(oldChecking))
					),
					new AccountList(checking, savings, oldChecking).getAccountGroups()
			);
		}
	}
}

package com.wesabe.api.accounts.entities.tests;

import static org.junit.Assert.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.InjectionHelper.*;
import static com.wesabe.api.tests.util.CurrencyHelper.*;

import java.util.ArrayList;

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
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.entities.TxactionListItem;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class TxactionListTest {
	public static class An_Empty_Txaction_List {
		private TxactionList txactionList = new TxactionList();
		
		@Test
		public void itIsEmpty() {
			assert(txactionList.isEmpty());
		}
		
		@Test
		public void itIsStillEmptyAfterCalculatingRunningTotalBalances() {
			txactionList.calculateRunningTotalBalances(new ArrayList<Account>(), USD, new CurrencyExchangeRateMap());
		}
	}
	
	public static class A_Populated_Txaction_List_No_Pending_Txactions {
		private TxactionList txactionList;
		private CurrencyExchangeRateMap exchangeRates = new CurrencyExchangeRateMap();
		private Account checkingUSD = Account.ofType(AccountType.CHECKING);
		private Account cashUSD = Account.ofType(AccountType.CASH);
		private Account creditEUR = Account.ofType(AccountType.CREDIT_CARD);
		private AccountBalance checkingUSDBalance = new AccountBalance(checkingUSD, decimal("11000.00"), now());
		private AccountBalance creditEURBalance = new AccountBalance(creditEUR, decimal("-923.00"), now());
		private Txaction wholeFoodsUSD = new Txaction(checkingUSD, decimal("-45.00"), now().minusDays(1));
		private AccountBalance checkingUSDBalance2 = new AccountBalance(checkingUSD, decimal("9000.00"), now().minusDays(1).minusHours(1));
		private Txaction starbucksEUR = new Txaction(creditEUR, decimal("-2.89"), now().minusDays(3));
		private Txaction checkingAtmWithdrawalUSD = new Txaction(checkingUSD, decimal("-80.00"), now().minusDays(18));
		private Txaction starbucksUSD = new Txaction(checkingUSD, decimal("-2.95"), now().minusDays(20));
		private Txaction cashAtmWithdrawalUSD = new Txaction(cashUSD, decimal("80.00"), now().minusDays(20));
		private Txaction mixtGreensUSD = new Txaction(cashUSD, decimal("-10.00"), now().minusDays(22));
		
		@Before
		public void setup() throws Exception {
			exchangeRates.addExchangeRate(EUR, USD, new DateTime(), decimal("0.79"));
			
			checkingUSD.setCurrency(USD);
			cashUSD.setCurrency(USD);
			creditEUR.setCurrency(EUR);
			inject(Account.class, checkingUSD, "accountBalances", Sets.newHashSet(checkingUSDBalance, checkingUSDBalance2));
			inject(Account.class, creditEUR, "accountBalances", Sets.newHashSet(creditEURBalance));
			
			txactionList = new TxactionList(wholeFoodsUSD, starbucksEUR, checkingAtmWithdrawalUSD, starbucksUSD, cashAtmWithdrawalUSD, mixtGreensUSD);
		}

		@Test
		public void itIsNotEmpty() {
			assertFalse(txactionList.isEmpty());
		}
		
		@Test
		public void itCanCalculateRunningTotalBalances() {
			txactionList.calculateRunningTotalBalances(Lists.newArrayList(checkingUSD, cashUSD, creditEUR), USD, exchangeRates);
			// latest account balances determine the initial amount
			assertEquals(new TxactionListItem(wholeFoodsUSD, 			money("10270.83", USD)), 	txactionList.get(0));
			// 2nd account balance is ignored here
			assertEquals(new TxactionListItem(starbucksEUR,				money("10315.83", USD)), 	txactionList.get(1));
			assertEquals(new TxactionListItem(checkingAtmWithdrawalUSD, money("10318.11", USD)), 	txactionList.get(2));
			assertEquals(new TxactionListItem(starbucksUSD, 			money("10398.11", USD)), 	txactionList.get(3));
			assertEquals(new TxactionListItem(cashAtmWithdrawalUSD, 	money("10401.06", USD)), 	txactionList.get(4));
			assertEquals(new TxactionListItem(mixtGreensUSD, 			money("10401.06", USD)), 	txactionList.get(5));
		}
	}
}

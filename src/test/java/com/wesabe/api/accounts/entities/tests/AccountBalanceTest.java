package com.wesabe.api.accounts.entities.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;

import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.api.util.money.CurrencyMismatchException;
import com.wesabe.api.util.money.Money;

@RunWith(Enclosed.class)
public class AccountBalanceTest {
	public static class An_Account_Balance {
		@Test
		public void itHasABalance() {
			AccountBalance accountBalance = new AccountBalance(new Account("Checking", USD), decimal("10.00"), new DateTime());
			assertEquals(money("10.00", USD), accountBalance.getBalance());
		}
		
		@Test
		public void itHasAnAccount() {
			Account account = new Account("Checking", USD);
			AccountBalance accountBalance = new AccountBalance(account, decimal("10.00"), new DateTime());
			assertEquals(account, accountBalance.getAccount());
		}
		
		@Test
		public void itCanBeConstructedWithAnAccountAndAmountAndDate() {
			new AccountBalance(new Account("Checking", USD), decimal("10.00"), new DateTime());
		}
		
		@Test
		public void itCanBeConstructedWithAnAccountAndAMoneyWithMatchingCurrencies() {
			Account account = new Account("Checking", USD);
			Money balance = money("10.00", USD);
			new AccountBalance(account, balance, new DateTime());
		}
		
		@Test(expected=CurrencyMismatchException.class)
		public void itCannotBeConstructedWithAnAccountWhoseCurrencyMismatchesTheMoneyBalance() {
			Account account = new Account("Checking", USD);
			Money mismatchedBalance = money("10.00", GBP);
			new AccountBalance(account, mismatchedBalance, new DateTime());
		}
	}
}

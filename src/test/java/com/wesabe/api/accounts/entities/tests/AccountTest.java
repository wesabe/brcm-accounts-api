package com.wesabe.api.accounts.entities.tests;

import static org.hamcrest.CoreMatchers.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.InjectionHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.Sets;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.api.accounts.entities.AccountStatus;
import com.wesabe.api.accounts.entities.AccountType;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.util.guid.GUID;

@RunWith(Enclosed.class)
public class AccountTest {
	public static class An_Account {
		@Test
		public void itHasAMutableAccountKey() throws Exception {
			Account account = new Account();
			assertNull(account.getAccountKey());
			account.setAccountKey("woo");
			assertEquals("woo", account.getAccountKey());
		}
		
		@Test
		public void itHasAMutableName() throws Exception {
			Account account = new Account();
			assertNull(account.getName());
			account.setName("woo");
			assertEquals("woo", account.getName());
		}
		
		@Test
		public void itHasAMutableStatus() throws Exception {
			Account account = new Account();
			assertEquals(AccountStatus.ACTIVE, account.getStatus());
			account.setStatus(AccountStatus.DELETED);
			assertEquals(AccountStatus.DELETED, account.getStatus());
		}
		
		@Test
		public void itHasAMutableRelativeId() throws Exception {
			Account account = new Account();
			assertNull(account.getRelativeId());
			account.setRelativeId(300);
			assertEquals(Integer.valueOf(300), account.getRelativeId());
		}
		
		@Test
		public void itHasAMutableCurrency() throws Exception {
			Account account = new Account();
			assertEquals(USD, account.getCurrency());
			account.setCurrency(EUR);
			assertEquals(EUR, account.getCurrency());
		}
		
		@Test
		public void itIsNotEqualToNull() throws Exception {
			assertFalse(new Account().equals(null));
		}
		
		@Test
		public void itIsNotEqualToANonAccount() throws Exception {
			assertFalse(new Account().equals("woo"));
		}
		
		@Test
		public void itIsNotEqualToAnotherAccountWithDifferentFields() throws Exception {
			final Account firstAccount = new Account();
			final Account otherAccount = new Account();
			
			inject(Account.class, firstAccount, "id", Integer.valueOf(300));
			inject(Account.class, firstAccount, "guid", "0123456789");
			
			inject(Account.class, otherAccount, "id", Integer.valueOf(200));
			inject(Account.class, otherAccount, "guid", "9876543210");
			
			assertFalse(firstAccount.equals(otherAccount));
			assertFalse(firstAccount.hashCode() == otherAccount.hashCode());
		}
		
		@Test
		public void itIsEqualToAnotherAccountWithTheSameFields() throws Exception {
			final Account firstAccount = new Account();
			final Account otherAccount = new Account();
			
			inject(Account.class, firstAccount, "id", Integer.valueOf(300));
			inject(Account.class, firstAccount, "guid", "0123456789");
			
			inject(Account.class, otherAccount, "id", Integer.valueOf(300));
			inject(Account.class, otherAccount, "guid", "0123456789");
			
			assertTrue(firstAccount.equals(otherAccount));
			assertTrue(firstAccount.hashCode() == otherAccount.hashCode());
		}
	}
	
	public static class A_New_Account {
		private final Account account = new Account();
		
		@Test
		public void itIsActive() throws Exception {
			assertEquals(AccountStatus.ACTIVE, account.getStatus());
		}
		
		@Test
		public void itHasAnUnknownAccountType() {
			assertEquals(AccountType.UNKNOWN, account.getAccountType());
		}
		
		@Test
		public void itHasARandomlyGeneratedGUID() throws Exception {
			assertNotNull(account.getGuid());
			assertEquals(64, account.getGuid().length());
		}
		
		@Test
		public void itIsInUSD() throws Exception {
			assertEquals(USD, account.getCurrency());
		}
	}
	
	public static class A_Loaded_Account {
		private final Account account = new Account("Checking", USD);
		
		@Before
		public void setup() throws Exception {
			inject(Account.class, account, "id", Integer.valueOf(300));
			inject(Account.class, account, "guid", "0123456789");
			inject(Account.class, account, "accountTypeId", AccountType.CHECKING.getValue());
			inject(Account.class, account, "position", 12);
		}
		
		@Test
		public void itHasAnId() throws Exception {
			assertEquals(Integer.valueOf(300), account.getId());
		}
		
		@Test
		public void itHasAGUID() throws Exception {
			assertEquals(new GUID("0123456789"), account.getGuid());
		}
		
		@Test
		public void itMightHaveABalance() throws Exception {
			inject(Account.class, account, "balance", decimal("-300.12"));
			assertEquals(money("-300.12", USD), account.getBalance());
		}
		
		@Test
		public void itMightNotHaveABalance() throws Exception {
			inject(Account.class, account, "balance", null);
			assertNull(account.getBalance());
		}
		
		@Test
		public void itHasAnAccountType() throws Exception {
			assertEquals(AccountType.CHECKING, account.getAccountType());
		}
		
		@Test
		public void itHasAPosition() throws Exception {
			assertEquals(Integer.valueOf(12), account.getPosition());
		}
	}
	
	public static class An_Account_With_Uploads {
		private final Account account = new Account("Checking", USD);
		
		@Before
		public void setup() throws Exception {
			AccountBalance balance = new AccountBalance(account, decimal("100.00"), jun14th);
			inject(Account.class, account, "accountBalances", Sets.newHashSet(balance));
			inject(Account.class, account, "accountTypeId", AccountType.CHECKING.getValue());
			inject(AccountBalance.class, balance, "createdAt", jun15th);
		}
		
		@Test
		public void itHasABalanceDate() {
			assertEquals(jun14th, account.getBalanceDate());
		}
		
		@Test
		public void itHasALastActivityDateEqualToWhenTheLastBalanceWasCreated() {
			assertThat(account.getLastActivityDate(), is(jun15th));
		}
	}
	
	public static class A_Cash_Account {
		private final static Account account = Account.ofType(AccountType.CASH);
		
		@Test
		public void itHasAccountTypeCash() {
			assertEquals(AccountType.CASH, account.getAccountType());
		}
		
		@Test
		public void itHasNoBalanceDate() throws Exception {
			assertNull(account.getBalanceDate());
		}
		
		@Test
		public void itHasNoLastActivityDate() throws Exception {
			assertNull(account.getLastActivityDate());
		}
	}
	
	public static class A_Cash_Account_With_A_Cached_Balance {
		private final static Account account = Account.ofType(AccountType.CASH);

		@Before
		public void setup() throws Exception {
			inject(Account.class, account, "balance", decimal("10.00"));
		}
		
		@Test
		public void itDoesNotHaveABalance() {
			assertFalse(account.hasBalance());
		}
		
		@Test
		public void itHasANullBalance() {
			assertNull(account.getBalance());
		}
		
		@Test
		public void itHasNoLastActivityDate() throws Exception {
			assertNull(account.getLastActivityDate());
		}
	}
	
	public static class A_Manual_Account {
		private final static Account account = Account.ofType(AccountType.MANUAL);
		
		@Test
		public void itHasAccountTypeManual() {
			assertEquals(AccountType.MANUAL, account.getAccountType());
		}
	}
	
	public static class A_Manual_Account_Without_A_Cached_Balance {
		private final static Account account = Account.ofType(AccountType.MANUAL);

		@Before
		public void setup() throws Exception {
			Set<AccountBalance> accountBalances = Sets.newHashSet();
			
			accountBalances.add(new AccountBalance(account, decimal("10.00"), new DateTime()));
			
			inject(Account.class, account, "balance", null);
			inject(Account.class, account, "accountBalances", accountBalances);
		}
		
		@Test
		public void itHasABalance() {
			assert(account.hasBalance());
		}
		
		@Test
		public void itHasABalanceEqualToTheLastAccountBalance() {
			assertEquals(money("10.00", USD), account.getBalance());
		}
	}
	
	public static class A_Manual_Account_With_A_Stale_Balance {
		private final static Account account = Account.ofType(AccountType.MANUAL);
		
		@Before
		public void setup() throws Exception {
			Set<AccountBalance> accountBalances = Sets.newHashSet(
					new AccountBalance(account, decimal("10.00"), apr1st));
			
			inject(Account.class, account, "accountBalances", accountBalances);
			
			Set<Txaction> txactions = Sets.newHashSet(
					new Txaction(account, decimal("-1.50"), jun14th));
			
			inject(Account.class, account, "txactions", txactions);
		}
		
		@Test
		public void itComputesTheBalanceByAddingSubsequentTransactionAmountsToTheStaleBalance() {
			assertEquals(money("8.50", USD), account.getBalance());
		}
	}
}

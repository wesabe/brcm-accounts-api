package com.wesabe.api.accounts.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Locale;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.wesabe.api.accounts.dao.AccountBalanceDAO;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.presenters.AccountBalancePresenter;
import com.wesabe.api.accounts.resources.AccountBalanceResource;
import com.wesabe.api.util.auth.WesabeUser;

@RunWith(Enclosed.class)
public class AccountBalanceResourceTest {
	private static class Setup {
		protected AccountBalanceResource accountBalanceResource;
		protected WesabeUser user;
		protected AccountDAO accountDAO;
		protected AccountBalanceDAO accountBalanceDAO;
		protected AccountBalancePresenter accountBalancePresenter;
		protected Account account;
		protected AccountBalance accountBalance;
		
		@Before
		public void setup() {
			accountDAO = mock(AccountDAO.class);
			accountBalanceDAO = mock(AccountBalanceDAO.class);
			accountBalancePresenter = mock(AccountBalancePresenter.class);
			
			accountBalanceResource = new AccountBalanceResource(accountBalanceDAO, accountDAO, accountBalancePresenter);
			user = mock(WesabeUser.class);
			
			account = mock(Account.class);
			accountBalance = mock(AccountBalance.class);
			
			expectAccount(account);
		}
		
		protected void expectAccount(Account account) {
			when(accountDAO.findAccount(Mockito.anyString(), eq(1))).thenReturn(account);
		}
		
		protected void expectAccountBalance(AccountBalance accountBalance) {
			when(accountBalanceDAO.findAccountBalance(Mockito.anyString(), eq(1))).thenReturn(accountBalance);
		}
	}
	
	public static class Requesting_An_Account_Balance_With_Matching_Ids extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(accountBalance.getAccount()).thenReturn(account);
			expectAccountBalance(accountBalance);
		}
		
		@Test
		public void itPresentsTheAccount() {
			accountBalanceResource.show(user, Locale.ENGLISH, new IntegerParam("1"), new IntegerParam("1"));
			verify(accountBalancePresenter).present(accountBalance, Locale.ENGLISH);
		}
	}
	
	public static class Requesting_An_Account_Balance_With_Mismatched_Ids extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(accountBalance.getAccount()).thenReturn(new Account());
			expectAccountBalance(accountBalance);
		}
		
		@Test
		public void itReturnsNotFound() {
			try {
				accountBalanceResource.show(user, Locale.ENGLISH, new IntegerParam("1"), new IntegerParam("1"));
				fail("expected a Not Found exception");
			} catch (WebApplicationException ex) {
				assertThat(ex.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
	}
	
	public static class Requesting_An_Account_Balance_By_An_Invalid_Account_Id extends Setup {
		@Before
		public void setup() {
			super.setup();
			expectAccount(null);
		}
		
		@Test
		public void itReturnsNotFound() {
			try {
				accountBalanceResource.show(user, Locale.ENGLISH, new IntegerParam("1"), new IntegerParam("1"));
			} catch (WebApplicationException ex) {
				assertThat(ex.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
	}
	
	public static class Requesting_An_Account_Balance_By_A_Valid_Account_Id_But_An_Invalid_Account_Balance_Id extends Setup {
		@Before
		public void setup() {
			super.setup();
			expectAccountBalance(null);
		}
		
		@Test
		public void itReturnsNotFound() {
			try {
				accountBalanceResource.show(user, Locale.ENGLISH, new IntegerParam("1"), new IntegerParam("1"));
			} catch (WebApplicationException ex) {
				assertThat(ex.getResponse().getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			}
		}
	}
}

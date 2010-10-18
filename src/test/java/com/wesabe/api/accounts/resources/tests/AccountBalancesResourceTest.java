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

import com.google.common.collect.Sets;
import com.wesabe.api.accounts.dao.AccountBalanceDAO;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.api.accounts.entities.AccountType;
import com.wesabe.api.accounts.params.DecimalParam;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.presenters.AccountBalancePresenter;
import com.wesabe.api.accounts.resources.AccountBalancesResource;
import com.wesabe.api.util.auth.WesabeUser;

@RunWith(Enclosed.class)
public class AccountBalancesResourceTest {
	private static class Setup {
		protected AccountBalancesResource accountBalancesResource;
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
			
			accountBalancesResource = new AccountBalancesResource(accountBalanceDAO, accountDAO, accountBalancePresenter);
			user = mock(WesabeUser.class);
			
			account = mock(Account.class);
			when(account.getAccountType()).thenReturn(AccountType.CHECKING);
			when(account.hasBalance()).thenReturn(true);
			accountBalance = mock(AccountBalance.class);
			
			expectAccount(account);
		}
		
		protected void expectAccount(Account account) {
			when(accountDAO.findAccount(Mockito.anyString(), eq(1))).thenReturn(account);
		}
	}
	
	public static class Requesting_All_Account_Balances extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.getAccountBalances()).thenReturn(Sets.newHashSet(accountBalance));
		}
		
		@Test
		public void itPresentsAllAccountBalances() {
			accountBalancesResource.list(user, Locale.ENGLISH, new IntegerParam("1"));
			verify(accountBalancePresenter).present(accountBalance, Locale.ENGLISH);
		}
	}
	
	public static class Requesting_All_Account_Balances_For_An_Account_Without_A_Balance extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.hasBalance()).thenReturn(false);
			when(account.getAccountBalances()).thenReturn(Sets.newHashSet(accountBalance));
		}
		
		@Test
		public void itDoesNotPresentAnyAccountBalances() {
			accountBalancesResource.list(user, Locale.ENGLISH, new IntegerParam("1"));
			verify(accountBalancePresenter, never()).present(Mockito.any(AccountBalance.class), eq(Locale.ENGLISH));
		}
	}
	
	public static class Creating_A_New_Account_Balance extends Setup {
		@Before
		public void setup() {
			super.setup();
		}
		
		@Test
		public void itCreatesANewAccountBalance() {
			accountBalancesResource.create(user, Locale.ENGLISH, new IntegerParam("1"), new DecimalParam("20"));
			verify(accountBalanceDAO).create(Mockito.any(AccountBalance.class));
		}
	}
	
	public static class Creating_A_New_Account_Balance_Without_An_Amount extends Setup {
		@Before
		public void setup() {
			super.setup();
		}
		
		@Test
		public void itReturnsBadRequest() {
			try {
				accountBalancesResource.create(user, Locale.ENGLISH, new IntegerParam("1"), null);
				fail("expected a Bad Request exception");
			} catch (WebApplicationException ex) {
				assertThat(ex.getResponse().getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
			}
		}
	}
	
	public static class Creating_A_New_Account_Balance_With_An_Account_Without_Balances extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.hasBalance()).thenReturn(false);
		}
		
		@Test
		public void itReturnsBadRequest() {
			try {
				accountBalancesResource.create(user, Locale.ENGLISH, new IntegerParam("1"), new DecimalParam("20"));
				fail("expected a Bad Request exception");
			} catch (WebApplicationException ex) {
				assertThat(ex.getResponse().getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
			}
		}
	}
}

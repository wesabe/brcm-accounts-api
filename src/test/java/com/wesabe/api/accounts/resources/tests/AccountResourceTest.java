package com.wesabe.api.accounts.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Locale;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.presenters.AccountPresenter;
import com.wesabe.api.accounts.presenters.InvestmentAccountPresenter;
import com.wesabe.api.accounts.resources.AccountResource;
import com.wesabe.api.util.auth.WesabeUser;

@RunWith(Enclosed.class)
public class AccountResourceTest {
	private static class Setup {
		protected AccountResource accountResource;
		protected WesabeUser user;
		protected AccountDAO accountDAO;
		protected AccountPresenter accountPresenter;
		protected InvestmentAccountPresenter investmentAccountPresenter;
		
		@Before
		public void setup() {
			accountDAO = mock(AccountDAO.class);
			accountPresenter = mock(AccountPresenter.class);
			investmentAccountPresenter = mock(InvestmentAccountPresenter.class);
			
			accountResource = new AccountResource(accountDAO, accountPresenter, investmentAccountPresenter);
			user = mock(WesabeUser.class);
		}
	}

	public static class Requesting_A_Non_Existent_Account_Id extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(accountDAO.findAccount(Mockito.anyString(), eq(99))).thenReturn(null);
		}
		
		@Test
		public void itThrowsA404() {
			try {
				accountResource.show(user, Locale.ENGLISH, new IntegerParam("99"));
				fail("Expected 404 to be thrown, but nothing was thrown");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(404);
			}
		}
	}
	
	public static class Requesting_A_Non_Investment_Account extends Setup {
		protected Account account;
		
		@Before
		public void setup() {
			super.setup();
			account = mock(Account.class);
			when(accountDAO.findAccount(Mockito.anyString(), eq(1))).thenReturn(account);
		}
		
		@Test
		public void itPresentsTheAccount() {
			accountResource.show(user, Locale.ENGLISH, new IntegerParam("1"));
			verify(accountPresenter).present(account, Locale.ENGLISH);
		}
	}
	
	public static class Requesting_An_Investment_Account extends Setup {
		protected InvestmentAccount account;
		
		@Before
		public void setup() {
			super.setup();
			account = mock(InvestmentAccount.class);
			when(accountDAO.findAccount(Mockito.anyString(), eq(1))).thenReturn(account);
		}
		
		@Test
		public void itPresentsTheAccount() {
			accountResource.show(user, Locale.ENGLISH, new IntegerParam("1"));
			verify(investmentAccountPresenter).present(account, Locale.ENGLISH);
		}
	}
}

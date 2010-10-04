package com.wesabe.api.accounts.resources.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Currency;
import java.util.Locale;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountStatus;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.params.BooleanParam;
import com.wesabe.api.accounts.params.CurrencyParam;
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
		protected Account account;
		protected InvestmentAccount investmentAccount;
		
		@Before
		public void setup() {
			accountDAO = mock(AccountDAO.class);
			accountPresenter = mock(AccountPresenter.class);
			investmentAccountPresenter = mock(InvestmentAccountPresenter.class);
			
			accountResource = new AccountResource(accountDAO, accountPresenter, investmentAccountPresenter);
			user = mock(WesabeUser.class);
			
			account = mock(Account.class);
			investmentAccount = mock(InvestmentAccount.class);
			
			expectAccount(account);
		}
		
		protected void expectAccount(Account account) {
			when(accountDAO.findAccount(Mockito.anyString(), eq(1))).thenReturn(account);
		}
	}
	
	public static class Requesting_A_Non_Existent_Account_Id extends Setup {
		@Before
		public void setup() {
			super.setup();
			expectAccount(null);
		}
		
		@Test
		public void itThrowsA404() {
			try {
				accountResource.show(user, Locale.ENGLISH, new IntegerParam("1"));
				fail("Expected 404 to be thrown, but nothing was thrown");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(404);
			}
		}
	}
	
	public static class Requesting_A_Non_Investment_Account extends Setup {
		@Before
		public void setup() {
			super.setup();
		}
		
		@Test
		public void itPresentsTheAccount() {
			accountResource.show(user, Locale.ENGLISH, new IntegerParam("1"));
			verify(accountPresenter).present(account, Locale.ENGLISH);
		}
	}
	
	public static class Requesting_An_Investment_Account extends Setup {
		@Before
		public void setup() {
			super.setup();
			expectAccount(investmentAccount);
		}
		
		@Test
		public void itPresentsTheAccount() {
			accountResource.show(user, Locale.ENGLISH, new IntegerParam("1"));
			verify(investmentAccountPresenter).present(account, Locale.ENGLISH);
		}
	}
	
	public static class Updating_An_Account_Name extends Setup {
		@Before
		public void setup() {
			super.setup();
		}
		
		@Test
		public void itUpdatesTheAccount() {
			accountResource.update(user, Locale.ENGLISH, new IntegerParam("1"), "New Name", null, null);
			verify(account).setName("New Name");
			verify(accountDAO).update(account);
		}
	}
	
	public static class Updating_An_Account_Currency extends Setup {
		@Before
		public void setup() {
			super.setup();
		}
		
		@Test
		public void itUpdatesTheCurrency() {
			accountResource.update(user, Locale.ENGLISH, new IntegerParam("1"), null, new CurrencyParam("USD"), null);
			verify(account).setCurrency(Currency.getInstance("USD"));
			verify(accountDAO).update(account);
		}
	}
	
	public static class Archiving_An_Active_Account extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.isArchived()).thenReturn(false);
			when(account.isActive()).thenReturn(true);
		}
		
		@Test
		public void itArchivesTheAccount() {
			accountResource.update(user, Locale.ENGLISH, new IntegerParam("1"), null, null, new BooleanParam("true"));
			verify(account).setStatus(AccountStatus.ARCHIVED);
			verify(accountDAO).update(account);
		}
	}
	
	public static class Archiving_An_Archived_Account extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.isArchived()).thenReturn(true);
			when(account.isActive()).thenReturn(false);
		}
		
		@Test
		public void itDoesNothing() {
			accountResource.update(user, Locale.ENGLISH, new IntegerParam("1"), null, null, new BooleanParam("true"));
			verify(account, never()).setStatus(AccountStatus.ARCHIVED);
			verify(accountDAO, never()).update(account);
		}
	}
	
	public static class Unarchiving_An_Archived_Account extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.isArchived()).thenReturn(true);
			when(account.isActive()).thenReturn(false);
		}
		
		@Test
		public void itActivatesTheAccount() {
			accountResource.update(user, Locale.ENGLISH, new IntegerParam("1"), null, null, new BooleanParam("false"));
			verify(account).setStatus(AccountStatus.ACTIVE);
			verify(accountDAO).update(account);
		}
	}
	
	public static class Unarchiving_An_Active_Account extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.isArchived()).thenReturn(false);
			when(account.isActive()).thenReturn(true);
		}
		
		@Test
		public void itDoesNothing() {
			accountResource.update(user, Locale.ENGLISH, new IntegerParam("1"), null, null, new BooleanParam("false"));
			verify(account, never()).setStatus(AccountStatus.ARCHIVED);
			verify(accountDAO, never()).update(account);
		}
	}
	
	public static class Unarchiving_A_Non_Active_Non_Archived_Account extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.isArchived()).thenReturn(false);
			when(account.isActive()).thenReturn(false);
		}
		
		@Test
		public void itRespondsWithBadRequest() {
			try {
				accountResource.update(user, Locale.ENGLISH, new IntegerParam("1"), null, null, new BooleanParam("false"));
				fail("Expected Bad Request exception, got none");
			} catch (WebApplicationException ex) {
				assertThat(ex.getResponse().getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
			}
		}
	}
	
	public static class Archiving_A_Non_Active_Non_Archived_Account extends Setup {
		@Before
		public void setup() {
			super.setup();
			when(account.isArchived()).thenReturn(false);
			when(account.isActive()).thenReturn(false);
		}
		
		@Test
		public void itRespondsWithBadRequest() {
			try {
				accountResource.update(user, Locale.ENGLISH, new IntegerParam("1"), null, null, new BooleanParam("true"));
				fail("Expected Bad Request exception, got none");
			} catch (WebApplicationException ex) {
				assertThat(ex.getResponse().getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
			}
		}
	}
}
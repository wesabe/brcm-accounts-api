package com.wesabe.api.accounts.presenters.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountType;
import com.wesabe.api.accounts.presenters.AccountBriefPresenter;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class AccountBriefPresenterTest {
	public static class Presenting_An_Account {
		private Account account;
		private AccountBriefPresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.account = mock(Account.class);
			when(account.getRelativeId()).thenReturn(2);
			when(account.getAccountType()).thenReturn(AccountType.CHECKING);
			
			this.presenter = new AccountBriefPresenter();
		}
		
		@Test
		public void itIsNamedAccount() throws Exception {
			final XmlsonObject representation = presenter.present(account);
			
			assertThat(representation.getName(), is("account"));
		}
		
		@Test
		public void itHasAnId() throws Exception {
			final XmlsonObject representation = presenter.present(account);
			
			assertThat(representation.getInteger("id"), is(2));
		}
		
		@Test
		public void itHasAnUri() throws Exception {
			final XmlsonObject representation = presenter.present(account);
			
			assertThat(representation.getString("uri"), is("/accounts/2"));
		}
		
		@Test
		public void itHasAType() throws Exception {
			final XmlsonObject representation = presenter.present(account);
			
			assertThat(representation.getString("type"), is("Checking"));
		}
	}
}

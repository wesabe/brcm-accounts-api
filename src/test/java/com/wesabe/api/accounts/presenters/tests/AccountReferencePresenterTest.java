package com.wesabe.api.accounts.presenters.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.presenters.AccountReferencePresenter;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class AccountReferencePresenterTest {
	public static class Presenting_An_Account_Reference {
		private Account account;
		private AccountReferencePresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.account = mock(Account.class);
			when(account.getRelativeId()).thenReturn(4);
			
			this.presenter = new AccountReferencePresenter();
		}
		
		@Test
		public void itIsNamedAccount() throws Exception {
			final XmlsonObject representation = presenter.present(account);
			
			assertThat(representation.getName(), is("account"));
		}
		
		@Test
		public void itHasTheAccountsUri() throws Exception {
			final XmlsonObject representation = presenter.present(account);
			
			assertThat(representation.getString("uri"), is("/accounts/4"));
		}
	}
}

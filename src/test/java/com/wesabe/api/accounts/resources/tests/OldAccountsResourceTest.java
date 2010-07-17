package com.wesabe.api.accounts.resources.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Currency;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.inject.internal.Lists;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.AccountStatus;
import com.wesabe.api.accounts.params.BooleanParam;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.resources.OldAccountsResource;
import com.wesabe.api.tests.util.MockResourceContext;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class OldAccountsResourceTest {
	private static abstract class Context {
		protected MockResourceContext context;
		protected OldAccountsResource resource;
		protected CurrencyParam currency;
		protected List<Account> accounts;
		protected BooleanParam includeArchived;
		protected XmlsonObject xmlson;
		
		@SuppressWarnings("unchecked")
		public void setup() throws Exception {
			this.context = new MockResourceContext();
			
			this.accounts = Lists.newArrayList();
			
			when(
				context.getAccountDAO().findAllAccountsByAccountKey(
					Mockito.anyString(),
					Mockito.any(Set.class)
				)
			).thenReturn(accounts);
			
			this.xmlson = mock(XmlsonObject.class);
			
			when(
				context.getAccountListPresenter().present(
					Mockito.any(AccountList.class),
					Mockito.any(Currency.class),
					Mockito.any(Locale.class)
				)
			).thenReturn(xmlson);
			
			this.currency = new CurrencyParam("USD");
			
			this.resource = context.getInstance(OldAccountsResource.class);
		}
		
	}
	
	public static class Displaying_Active_Accounts extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.includeArchived = new BooleanParam("false");
		}
		
		@Test
		public void itFindsAllActiveAccountsForTheUser() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, includeArchived);
			
			verify(context.getAccountDAO()).findAllAccountsByAccountKey(context.getUser().getAccountKey(), EnumSet.of(AccountStatus.ACTIVE));
		}
		
		@Test
		public void itPresentsTheAccountList() throws Exception {
			final XmlsonObject representation = resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, includeArchived);
			
			assertThat(representation, is(sameInstance(xmlson)));
			
			verify(context.getAccountListPresenter()).present(new AccountList(accounts), USD, Locale.CANADA_FRENCH);
		}
	}
	
	public static class Displaying_Active_And_Archived_Accounts extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.includeArchived = new BooleanParam("true");
		}
		
		@Test
		public void itFindsAllActiveAndArchivedAccountsForTheUser() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, includeArchived);
			
			verify(context.getAccountDAO()).findAllAccountsByAccountKey(context.getUser().getAccountKey(), EnumSet.of(AccountStatus.ACTIVE, AccountStatus.ARCHIVED));
		}
	}
}

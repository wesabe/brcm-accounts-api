package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountGroup;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.presenters.AccountGroupPresenter;
import com.wesabe.api.accounts.presenters.AccountReferencePresenter;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class AccountGroupPresenterTest {
	public static class Presenting_An_Account_Group {
		private AccountGroup group;
		private AccountList accountList;
		private Account account;
		private CurrencyExchangeRateMap exchangeRateMap;
		private MoneyPresenter moneyPresenter;
		private AccountReferencePresenter accountReferencePresenter;
		private AccountGroupPresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.account = mock(Account.class);
			when(account.getRelativeId()).thenReturn(20);
			
			this.accountList = mock(AccountList.class);
			when(accountList.iterator()).thenReturn(ImmutableList.of(account).iterator());
			
			this.group = mock(AccountGroup.class);
			when(group.getName()).thenReturn("Checking");
			when(group.getId()).thenReturn("checking");
			when(group.getAccounts()).thenReturn(accountList);
			
			this.exchangeRateMap = mock(CurrencyExchangeRateMap.class);
			
			this.moneyPresenter = new MoneyPresenter();
			this.accountReferencePresenter = new AccountReferencePresenter();
			
			this.presenter = new AccountGroupPresenter(exchangeRateMap, moneyPresenter, accountReferencePresenter);
		}
		
		@Test
		public void itHasTheGroupsName() throws Exception {
			final XmlsonObject representation = presenter.present(group, USD, Locale.KOREA);
			
			assertThat(representation.getString("name"), is("Checking"));
		}
		
		@Test
		public void itHasTheGroupsUri() throws Exception {
			final XmlsonObject representation = presenter.present(group, USD, Locale.KOREA);
			
			assertThat(representation.getString("uri"), is("/account-groups/checking"));
		}
		
		@Test
		public void itHasTheGroupsAccountsAsReferences() throws Exception {
			final XmlsonObject representation = presenter.present(group, USD, Locale.KOREA);
			
			final XmlsonArray accounts = (XmlsonArray) representation.get("accounts");
			assertThat(accounts.getMembers().size(), is(1));
			
			final XmlsonObject account = (XmlsonObject) accounts.getMembers().get(0);
			assertThat(account.getString("uri"), is("/accounts/20"));
		}
		
		@Test
		public void itDoesNotListATotalIfNoAccountsHaveBalances() throws Exception {
			when(account.hasBalance()).thenReturn(false);
			
			presenter.present(group, USD, Locale.KOREA);
			
			verify(accountList, never()).getTotal(Mockito.any(Currency.class), Mockito.any(CurrencyExchangeRateMap.class));
		}
		
		@Test
		public void itDoesListATotalIfAnAccountHasABalance() throws Exception {
			when(account.hasBalance()).thenReturn(true);
			when(accountList.getTotal(USD, exchangeRateMap)).thenReturn(money("345.12", USD));
			
			final XmlsonObject representation = presenter.present(group, USD, Locale.KOREA);
			
			final XmlsonObject total = (XmlsonObject) representation.get("total");
			assertThat(total.getString("display"), is("US$345.12"));
			assertThat(total.getString("value"), is("345.12"));
		}
	}
}

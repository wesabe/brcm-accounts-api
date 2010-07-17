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
import com.wesabe.api.accounts.presenters.AccountListPresenter;
import com.wesabe.api.accounts.presenters.AccountPresenter;
import com.wesabe.api.accounts.presenters.InvestmentAccountPresenter;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class AccountListPresenterTest {
	public static class Presenting_An_Account_List {
		private CurrencyExchangeRateMap exchangeRateMap;
		private MoneyPresenter moneyPresenter;
		private AccountPresenter accountPresenter;
		private InvestmentAccountPresenter investmentAccountPresenter;
		private AccountGroupPresenter accountGroupPresenter;
		private Account account;
		private AccountGroup group;
		private AccountList accounts;
		private AccountListPresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.exchangeRateMap = mock(CurrencyExchangeRateMap.class);
			
			this.moneyPresenter = mock(MoneyPresenter.class);
			when(
				moneyPresenter.present(
					Mockito.anyString(),
					Mockito.any(Money.class),
					Mockito.any(Locale.class)
				)
			).thenReturn(new XmlsonObject("total"));
			
			this.accountPresenter = mock(AccountPresenter.class);
			when(
				accountPresenter.present(
					Mockito.any(Account.class),
					Mockito.any(Locale.class)
				)
			).thenReturn(new XmlsonObject("account"));
			
			this.accountGroupPresenter = mock(AccountGroupPresenter.class);
			when(
				accountGroupPresenter.present(
					Mockito.any(AccountGroup.class),
					Mockito.any(Currency.class),
					Mockito.any(Locale.class)
				)
			).thenReturn(new XmlsonObject("group"));
		
			this.account = mock(Account.class);
			
			this.group = mock(AccountGroup.class);
			
			this.accounts = mock(AccountList.class);
			when(accounts.iterator()).thenReturn(ImmutableList.of(account).iterator());
			when(accounts.getAccountGroups()).thenReturn(ImmutableList.of(group));
			when(accounts.getTotal(Mockito.any(Currency.class), Mockito.any(CurrencyExchangeRateMap.class))).thenReturn(money("3460.81", USD));
			
			this.presenter = new AccountListPresenter(exchangeRateMap, moneyPresenter, accountPresenter, investmentAccountPresenter, accountGroupPresenter);
		}
		
		@Test
		public void itIsNamedAccountList() throws Exception {
			final XmlsonObject representation = presenter.present(accounts, USD, Locale.PRC);
			
			assertThat(representation.getName(), is("account-list"));
		}
		
		@Test
		public void itHasAnArrayOfAccounts() throws Exception {
			final XmlsonObject representation = presenter.present(accounts, USD, Locale.PRC);
			
			final XmlsonArray accounts = (XmlsonArray) representation.get("accounts");
			assertThat(accounts.getMembers().size(), is(1));
			
			final XmlsonObject account = (XmlsonObject) accounts.getMembers().get(0);
			assertThat(account.getName(), is("account"));
			
			verify(accountPresenter).present(this.account, Locale.PRC);
		}
		
		@Test
		public void itHasAnArrayOfGroups() throws Exception {
			final XmlsonObject representation = presenter.present(accounts, USD, Locale.PRC);
			
			final XmlsonArray groups = (XmlsonArray) representation.get("account-groups");
			assertThat(groups.getMembers().size(), is(1));
			
			final XmlsonObject group = (XmlsonObject) groups.getMembers().get(0);
			assertThat(group.getName(), is("group"));
			
			verify(accountGroupPresenter).present(this.group, USD, Locale.PRC);
		}
		
		@Test
		public void itHasATotal() throws Exception {
			final XmlsonObject representation = presenter.present(accounts, USD, Locale.PRC);
			
			final XmlsonObject total = (XmlsonObject) representation.get("total");
			assertThat(total.getName(), is("total"));
			
			verify(accounts).getTotal(USD, exchangeRateMap);
			verify(moneyPresenter).present("total", money("3460.81", USD), Locale.PRC);
		}
	}
}

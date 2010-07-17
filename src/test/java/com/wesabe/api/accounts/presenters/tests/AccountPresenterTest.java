package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountStatus;
import com.wesabe.api.accounts.entities.AccountType;
import com.wesabe.api.accounts.entities.FinancialInst;
import com.wesabe.api.accounts.presenters.AccountPresenter;
import com.wesabe.api.accounts.presenters.FinancialInstPresenter;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.xmlson.XmlsonElement;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class AccountPresenterTest {
	public static class Presenting_An_Account {
		private AccountPresenter presenter;
		private FinancialInstPresenter financialInstPresenter;
		private XmlsonObject financialInstRepresentation;
		private Account account;
		private FinancialInst financialInst;
		
		@Before
		public void setup() throws Exception {
			this.account = mock(Account.class);			
			when(account.getName()).thenReturn("Checking");
			when(account.getPosition()).thenReturn(0);
			when(account.getRelativeId()).thenReturn(5);
			when(account.getAccountType()).thenReturn(AccountType.CHECKING);
			when(account.getCurrency()).thenReturn(USD);
			when(account.getStatus()).thenReturn(AccountStatus.ACTIVE);
			
			this.financialInst = mock(FinancialInst.class);
			when(account.getFinancialInst()).thenReturn(financialInst);
			
			this.financialInstRepresentation = new XmlsonObject("financial-institution");			
			this.financialInstPresenter = mock(FinancialInstPresenter.class);
			when(financialInstPresenter.present(Mockito.any(FinancialInst.class))).thenReturn(financialInstRepresentation);
			
			this.presenter = new AccountPresenter(new MoneyPresenter(), financialInstPresenter);			
		}
		
		@Test
		public void itIsNamedAccount() throws Exception {
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.getName(), is("account"));
		}
		
		@Test
		public void itHasTheAccountsName() throws Exception {
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.getString("name"), is("Checking"));
		}
		
		@Test
		public void itHasTheAccountsPosition() throws Exception {
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.getInteger("position"), is(0));
		}
		
		@Test
		public void itHasTheAccountsUri() throws Exception {
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.getString("uri"), is("/accounts/5"));
		}
		
		@Test
		public void itHasTheAccountsType() throws Exception {
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.getString("type"), is("Checking"));
		}
		
		@Test
		public void itHasTheAccountsCurrency() throws Exception {
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.getString("currency"), is("USD"));
		}
		
		@Test
		public void itHasTheAccountsStatus() throws Exception {
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.getString("status"), is("active"));
		}
		
		@Test
		public void itHasTheAccountsBalanceIfAny() throws Exception {
			when(account.hasBalance()).thenReturn(true);
			when(account.getBalance()).thenReturn(money("344.00", USD));
			
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			final XmlsonObject balance = (XmlsonObject) representation.get("balance");
			assertThat(balance.getString("display"), is("344,00 $"));
			assertThat(balance.getString("value"), is("344.00"));
		}
		
		@Test
		public void itHasTheLastBalanceAtDateIfThereIsABalance() throws Exception {
			when(account.hasBalance()).thenReturn(true);
			when(account.getBalance()).thenReturn(money("344.00", USD));
			when(account.getLastActivityDate()).thenReturn(new DateTime(2009, 5, 22, 13, 25, 0, 0, DateTimeZone.UTC));
			
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.getString("last-balance-at"), is("20090522T132500Z"));
		}
		
		@Test
		public void itHasTheFinancialInst() throws Exception {
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);
			
			assertThat(representation.get("financial-institution"), is((XmlsonElement) financialInstRepresentation));
		}
		
		@Test
		public void itDoesNotHaveTheFinancialInstIfTheAccountHasNone() throws Exception {
			when(account.getFinancialInst()).thenReturn(null);
			
			final XmlsonObject representation = presenter.present(account, Locale.GERMANY);

			assertThat(representation.get("financial-institution"), is(nullValue()));
		}		
	}
}

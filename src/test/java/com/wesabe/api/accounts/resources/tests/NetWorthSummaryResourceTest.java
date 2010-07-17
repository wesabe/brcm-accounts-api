package com.wesabe.api.accounts.resources.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static org.fest.assertions.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.wesabe.api.accounts.analytics.IntervalType;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.params.IntervalTypeParam;
import com.wesabe.api.accounts.params.UriParam;
import com.wesabe.api.accounts.resources.NetWorthSummaryResource;
import com.wesabe.api.tests.util.MockResourceContext;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class NetWorthSummaryResourceTest {
	public static class Building_A_Summary {
		private MockResourceContext context;
		private NetWorthSummaryResource resource;
		private Account account;
		private AccountList accounts;
		private Txaction txaction, filteredTxaction;
		private ImmutableMap<Interval, Money> summaries;
		private List<Txaction> txactions;
		private XmlsonObject representation;
		
		@SuppressWarnings("unchecked")
		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();
			
			this.account = mock(Account.class);
			when(account.getRelativeId()).thenReturn(1);
			this.accounts = new AccountList(account);
			when(context.getAccountDAO().findVisibleAccounts(Mockito.anyString())).thenReturn(accounts);
			
			this.txaction = mock(Txaction.class);
			this.filteredTxaction = mock(Txaction.class);
			this.txactions = ImmutableList.of(txaction, filteredTxaction);
			when(context.getTxactionDAO().findTxactionsInDateRange(Mockito.anyCollection(), Mockito.any(Interval.class))).thenReturn(txactions);
			
			this.summaries = mock(ImmutableMap.class);
			when(context.getNetWorthSummarizer().summarize(Mockito.anyCollection(), Mockito.anyCollection(), Mockito.any(Interval.class), Mockito.any(IntervalType.class), Mockito.any(Currency.class), Mockito.any(Set.class))).thenReturn(summaries);
			
			this.representation = mock(XmlsonObject.class);
			when(context.getNetWorthSummaryPresenter().present(Mockito.any(ImmutableMap.class), Mockito.any(Locale.class))).thenReturn(representation);
			
			this.resource = context.getInstance(NetWorthSummaryResource.class);
		}
		
		@Test
		public void itFindsTheUsersVisibleAccounts() throws Exception {
			handleGet();

			verify(context.getAccountDAO()).findVisibleAccounts(context.getUser().getAccountKey());
		}
		
		@Test
		public void itFindsAllTransactionsInTheAccounts() throws Exception {
			handleGet();

			verify(context.getTxactionDAO()).findTxactionsInDateRange(accounts, new Interval(date(2009, 7, 27), date(2009, 9, 7)));
		}
		
		@Test
		public void itSummarizesTheTxactions() throws Exception {
			handleGet();
			
			verify(context.getNetWorthSummarizer()).summarize(accounts, txactions, new Interval(date(2009, 7, 27), date(2009, 9, 7)), IntervalType.WEEKLY, USD, ImmutableSet.<Tag>of());
		}
		
		@Test
		public void itPresentsTheSummary() throws Exception {
			assertThat(handleGet()).isEqualTo(representation);
			
			verify(context.getNetWorthSummaryPresenter()).present(summaries, Locale.CHINA);
		}

		private XmlsonObject handleGet() {
			return resource.show(context.getUser(), Locale.CHINA, new IntervalTypeParam("weekly"), new CurrencyParam("USD"), new ISODateParam("20090801"), new ISODateParam("20090901"), ImmutableSet.<UriParam>of(), ImmutableSet.<Tag>of());
		}
	}
	
	public static class Given_An_Invalid_Interval {
		private MockResourceContext context;
		private NetWorthSummaryResource resource;
		
		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();
			this.resource = context.getInstance(NetWorthSummaryResource.class);
		}
		
		@Test
		public void itThrowsA404() throws Exception {
			try {
				resource.show(context.getUser(), Locale.CHINA, new IntervalTypeParam("weekly"), new CurrencyParam("USD"), new ISODateParam("20090801"), new ISODateParam("20090501"), ImmutableSet.<UriParam>of(), ImmutableSet.<Tag>of());
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus(), is(404));
			}
		}
	}
}

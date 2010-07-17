package com.wesabe.api.accounts.resources.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Currency;
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
import com.wesabe.api.accounts.analytics.MonetarySummaryWithSplits;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.params.BooleanParam;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.params.IntervalTypeParam;
import com.wesabe.api.accounts.params.UriParam;
import com.wesabe.api.accounts.resources.OldIntervalSummaryResource;
import com.wesabe.api.tests.util.MockResourceContext;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class OldIntervalSummaryResourceTest {
	public static class Building_An_Interval_Summary {
		private CurrencyParam currency;
		private IntervalTypeParam intervalType;
		private ISODateParam startDate, endDate;
		private BooleanParam uneditedOnly;
		private Set<UriParam> accountUris;
		private Set<String> tagUris;
		private Set<String> merchantNames;
		private Set<Tag> filteredTags;
		private MockResourceContext context;
		private Account account;
		private AccountList accounts;
		private Txaction txaction, filteredTxaction;
		private TxactionList txactionList;
		private ImmutableMap<Interval, MonetarySummaryWithSplits> results;
		private XmlsonObject representation;
		private OldIntervalSummaryResource resource;

		@SuppressWarnings("unchecked")
		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();

			this.currency = new CurrencyParam("GBP");
			
			this.intervalType = new IntervalTypeParam("weekly");
			
			this.startDate = new ISODateParam("20070801");
			this.endDate = new ISODateParam("20070901");
			
			this.filteredTags = mock(Set.class);
			
			this.account = mock(Account.class);
			when(account.getRelativeId()).thenReturn(1);
			this.accounts = new AccountList(account);
			when(context.getAccountDAO().findVisibleAccounts(Mockito.anyString())).thenReturn(accounts);

			this.txaction = mock(Txaction.class);
			this.filteredTxaction = mock(Txaction.class);
			when(context.getTxactionDAO().findTxactionsInDateRange(Mockito.anyCollection(), Mockito.any(Interval.class))).thenReturn(ImmutableList.of(txaction, filteredTxaction));
			
			this.txactionList = mock(TxactionList.class);
			when(context.getTxactionListBuilder().build(anyList())).thenReturn(txactionList);
			
			when(txactionList.getTxactions()).thenReturn(ImmutableList.of(txaction));
			
			this.results = mock(ImmutableMap.class);
			when(context.getIntervalSummarizer().summarize(Mockito.any(Iterable.class), Mockito.any(Interval.class), Mockito.any(IntervalType.class), Mockito.any(Currency.class), Mockito.any(Set.class))).thenReturn(results);

			this.representation = mock(XmlsonObject.class);
			when(context.getIntervalSummaryPresenter().present(Mockito.any(ImmutableMap.class), Mockito.any(Locale.class))).thenReturn(representation);
			
			this.uneditedOnly = new BooleanParam("false");
			
			this.accountUris = ImmutableSet.of(new UriParam("/accounts/1"));
			
			this.tagUris = ImmutableSet.of("/tags/food");
			
			this.merchantNames = ImmutableSet.of("Starbucks");

			this.resource = context.getInstance(OldIntervalSummaryResource.class);
		}
		
		private XmlsonObject handleGet() {
			return resource.show(context.getUser(), Locale.TAIWAN, intervalType, currency, startDate, endDate, uneditedOnly, accountUris, tagUris, merchantNames, filteredTags);
		}

		@Test
		public void itFindsTheUsersVisibleAccounts() throws Exception {
			handleGet();

			verify(context.getAccountDAO()).findVisibleAccounts(context.getUser().getAccountKey());
		}

		@Test
		public void itFindsAllTransactionsInTheAccounts() throws Exception {
			handleGet();

			verify(context.getTxactionDAO()).findTxactionsInDateRange(accounts, new Interval(date(2007, 7, 30), date(2007, 9, 3)));
		}
		
		@Test
		public void itFiltersTheTxactions() throws Exception {
			handleGet();
			
			verify(context.getTxactionListBuilder()).setCalculateBalances(false);
			verify(context.getTxactionListBuilder()).setUnedited(false);
			verify(context.getTxactionListBuilder()).setTags(ImmutableSet.of(new Tag("food")));
			verify(context.getTxactionListBuilder()).setAccounts(accounts);
			verify(context.getTxactionListBuilder()).setMerchantNames(merchantNames);
			verify(context.getTxactionListBuilder()).build(ImmutableList.of(txaction, filteredTxaction));
		}

		@Test
		public void itSummarizesTheTransactions() throws Exception {
			handleGet();
			
			verify(context.getIntervalSummarizer()).summarize(ImmutableList.of(txaction), new Interval(date(2007, 7, 30), date(2007, 9, 3)), IntervalType.WEEKLY, GBP, filteredTags);
		}

		@Test
		public void itPresentsTheSummaries() throws Exception {
			assertThat(handleGet(), is(representation));

			verify(context.getIntervalSummaryPresenter()).present(results, Locale.TAIWAN);
		}
	}
	
	public static class Given_An_Invalid_Interval {
		private MockResourceContext context;
		private OldIntervalSummaryResource resource;
		
		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();
			this.resource = context.getInstance(OldIntervalSummaryResource.class);
		}
		
		@Test
		public void itThrowsA404() throws Exception {
			try {
				resource.show(context.getUser(), Locale.CHINA, new IntervalTypeParam("weekly"), new CurrencyParam("USD"), new ISODateParam("20090801"), new ISODateParam("20090501"), new BooleanParam("false"), null, null, null, null);
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus(), is(404));
			}
		}
	}
}

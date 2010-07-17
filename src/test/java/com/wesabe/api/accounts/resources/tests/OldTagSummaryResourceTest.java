package com.wesabe.api.accounts.resources.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Currency;
import java.util.Locale;

import javax.ws.rs.WebApplicationException;

import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.resources.OldTagSummaryResource;
import com.wesabe.api.tests.util.MockResourceContext;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class OldTagSummaryResourceTest {
	public static class Building_A_Tag_Summary {
		private CurrencyParam currency;
		private ISODateParam startDate, endDate;
		private MockResourceContext context;
		private AccountList accounts;
		private Txaction txaction;
		private ImmutableMap<Tag, MonetarySummary> results;
		private XmlsonObject representation;
		private OldTagSummaryResource resource;
		
		@SuppressWarnings("unchecked")
		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();
			
			this.currency = new CurrencyParam("GBP");
			
			this.startDate = new ISODateParam("20070801");
			this.endDate = new ISODateParam("20070901");
			
			this.accounts = mock(AccountList.class);
			when(context.getAccountDAO().findVisibleAccounts(Mockito.anyString())).thenReturn(accounts);
			
			this.txaction = mock(Txaction.class);
			when(context.getTxactionDAO().findTxactionsInDateRange(Mockito.anyCollection(), Mockito.any(Interval.class))).thenReturn(ImmutableList.of(txaction));
			
			this.results = mock(ImmutableMap.class);
			when(context.getTagSummarizer().summarize(Mockito.any(Iterable.class), Mockito.any(Currency.class))).thenReturn(results);
			
			this.representation = mock(XmlsonObject.class);
			when(context.getTagSummaryPresenter().present(Mockito.any(ImmutableMap.class), Mockito.any(Locale.class))).thenReturn(representation);
			
			this.resource = context.getInstance(OldTagSummaryResource.class);
		}
		
		@Test
		public void itFindsTheUsersVisibleAccounts() throws Exception {
			resource.show(context.getUser(), Locale.TAIWAN, currency, startDate, endDate);
			
			verify(context.getAccountDAO()).findVisibleAccounts(context.getUser().getAccountKey());
		}
		
		@Test
		public void itFindsAllTransactionsInTheAccounts() throws Exception {
			resource.show(context.getUser(), Locale.TAIWAN, currency, startDate, endDate);
			
			verify(context.getTxactionDAO()).findTxactionsInDateRange(accounts, new Interval(startDate.getValue(), endDate.getValue()));
		}
		
		@Test
		public void itSummarizesTheTransactions() throws Exception {
			resource.show(context.getUser(), Locale.TAIWAN, currency, startDate, endDate);
			
			verify(context.getTagSummarizer()).summarize(ImmutableList.of(txaction), GBP);
		}
		
		@Test
		public void itPresentsTheSummaries() throws Exception {
			assertThat(resource.show(context.getUser(), Locale.TAIWAN, currency, startDate, endDate), is(representation));
			
			verify(context.getTagSummaryPresenter()).present(results, Locale.TAIWAN);
		}
	}
	
	public static class Given_An_Invalid_Interval {
		private MockResourceContext context;
		private OldTagSummaryResource resource;
		
		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();
			this.resource = context.getInstance(OldTagSummaryResource.class);
		}
		
		@Test
		public void itThrowsA404() throws Exception {
			try {
				resource.show(context.getUser(), Locale.CHINA, new CurrencyParam("USD"), new ISODateParam("20090801"), new ISODateParam("20090501"));
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus(), is(404));
			}
		}
	}
}

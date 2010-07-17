package com.wesabe.api.accounts.resources.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static org.fest.assertions.Assertions.*;
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
import com.google.common.collect.ImmutableSet;
import com.wesabe.api.accounts.analytics.TagHierarchy;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder.HierarchyType;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder.TagImportanceScheme;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.params.BooleanParam;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.resources.TagHierarchyResource;
import com.wesabe.api.tests.util.MockResourceContext;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class TagHierarchyResourceTest {
	public static class Building_A_Tag_Summary {
		private CurrencyParam currency;
		private ISODateParam startDate, endDate;
		private IntegerParam maxTags;
		private BooleanParam rankByAmount, spending;
		private MockResourceContext context;
		private AccountList accounts;
		private Txaction txaction;
		private TagHierarchy results;
		private XmlsonObject representation;
		private TagHierarchyResource resource;

		@SuppressWarnings("unchecked")
		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();

			this.currency = new CurrencyParam("GBP");

			this.startDate = new ISODateParam("20070801");
			this.endDate = new ISODateParam("20070901");
			
			this.maxTags = new IntegerParam("5");
			this.rankByAmount = new BooleanParam("true");
			this.spending = new BooleanParam("true");

			this.accounts = mock(AccountList.class);
			when(context.getAccountDAO().findVisibleAccounts(Mockito.anyString())).thenReturn(accounts);

			this.txaction = mock(Txaction.class);
			when(context.getTxactionDAO().findTxactionsInDateRange(Mockito.anyCollection(), Mockito.any(Interval.class))).thenReturn(ImmutableList.of(txaction));

			this.results = mock(TagHierarchy.class);
			when(context.getTagHierarchyBuilder().build(Mockito.any(Iterable.class), Mockito.any(Currency.class), Mockito.any(TagImportanceScheme.class), Mockito.any(HierarchyType.class), Mockito.any(Set.class), Mockito.anyInt())).thenReturn(results);

			this.representation = mock(XmlsonObject.class);
			when(context.getTagHierarchyPresenter().present(Mockito.any(TagHierarchy.class), Mockito.any(Locale.class))).thenReturn(representation);

			this.resource = context.getInstance(TagHierarchyResource.class);
		}

		@Test
		public void itFindsTheUsersVisibleAccounts() throws Exception {
			resource.show(
				context.getUser(),
				Locale.CHINA,
				currency,
				startDate,
				endDate,
				maxTags,
				rankByAmount,
				spending
			);

			verify(context.getAccountDAO()).findVisibleAccounts(context.getUser().getAccountKey());
		}

		@Test
		public void itFindsAllTransactionsInTheAccounts() throws Exception {
			resource.show(
				context.getUser(),
				Locale.CHINA,
				currency,
				startDate,
				endDate,
				maxTags,
				rankByAmount,
				spending
			);

			verify(context.getTxactionDAO()).findTxactionsInDateRange(accounts, new Interval(startDate.getValue(), endDate.getValue()));
		}

		@Test
		public void itBuildsATagHierarchy() throws Exception {
			resource.show(
				context.getUser(),
				Locale.CHINA,
				currency,
				startDate,
				endDate,
				maxTags,
				rankByAmount,
				spending
			);

			verify(context.getTagHierarchyBuilder()).build(
				ImmutableList.of(txaction),
				GBP,
				TagHierarchyBuilder.TagImportanceScheme.RANK_BY_AMOUNT,
				TagHierarchyBuilder.HierarchyType.SPENDING,
				ImmutableSet.<Tag>of(),
				5
			);
		}
		
		@Test
		public void itOptionallyBuildsATagHierarchyRankedByCount() throws Exception {
			resource.show(
				context.getUser(),
				Locale.CHINA,
				currency,
				startDate,
				endDate,
				maxTags,
				new BooleanParam("false"),
				spending
			);

			verify(context.getTagHierarchyBuilder()).build(
				ImmutableList.of(txaction),
				GBP,
				TagHierarchyBuilder.TagImportanceScheme.RANK_BY_COUNT,
				TagHierarchyBuilder.HierarchyType.SPENDING,
				ImmutableSet.<Tag>of(),
				5
			);
		}
		
		@Test
		public void itOptionalBuildsATagHierarchyForEarnings() throws Exception {
			resource.show(
				context.getUser(),
				Locale.CHINA,
				currency,
				startDate,
				endDate,
				maxTags,
				rankByAmount,
				new BooleanParam("false")
			);

			verify(context.getTagHierarchyBuilder()).build(
				ImmutableList.of(txaction),
				GBP,
				TagHierarchyBuilder.TagImportanceScheme.RANK_BY_AMOUNT,
				TagHierarchyBuilder.HierarchyType.EARNINGS,
				ImmutableSet.<Tag>of(),
				5
			);
		}

		@Test
		public void itPresentsTheSummaries() throws Exception {
			assertThat(resource.show(
				context.getUser(),
				Locale.TAIWAN,
				currency,
				startDate,
				endDate,
				maxTags,
				rankByAmount,
				spending
			)).isEqualTo(representation);

			verify(context.getTagHierarchyPresenter()).present(results, Locale.TAIWAN);
		}
	}
	
	public static class Given_An_Invalid_Interval {
		private MockResourceContext context;
		private TagHierarchyResource resource;
		
		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();
			this.resource = context.getInstance(TagHierarchyResource.class);
		}
		
		@Test
		public void itThrowsA404() throws Exception {
			try {
				resource.show(
					context.getUser(),
					Locale.CHINA,
					new CurrencyParam("USD"),
					new ISODateParam("20090801"),
					new ISODateParam("20090501"),
					new IntegerParam("5"),
					new BooleanParam("true"),
					new BooleanParam("true")
				);
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus()).isEqualTo(404);
			}
		}
	}
}

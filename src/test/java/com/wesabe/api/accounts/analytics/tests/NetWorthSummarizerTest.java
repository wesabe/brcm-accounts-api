package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;
import com.wesabe.api.accounts.analytics.IntervalType;
import com.wesabe.api.accounts.analytics.NetWorthSummarizer;
import com.wesabe.api.accounts.analytics.TxactionListBuilder;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.entities.TxactionListItem;
import com.wesabe.api.util.money.Money;

@RunWith(Enclosed.class)
public class NetWorthSummarizerTest {
	public static class Summarizing_A_Set_Of_Transactions {
		private TxactionListBuilder listBuilder;
		private Provider<TxactionListBuilder> listBuilderProvider;
		private NetWorthSummarizer summarizer;
		private List<Account> accounts;
		private List<Txaction> txactions;
		private Interval dateRange;
		private TxactionList txactionList;
		private TxactionListItem item1, item2;
		private Txaction tx1, tx2;
		
		@SuppressWarnings("unchecked")
		@Before
		public void setup() throws Exception {
			this.listBuilder = mock(TxactionListBuilder.class);
			this.listBuilderProvider = new Provider<TxactionListBuilder>() {
				@Override
				public TxactionListBuilder get() {
					return listBuilder;
				}
			};
			
			this.accounts = mock(List.class);
			
			this.dateRange = new Interval(date(2008, 10, 1), date(2008, 11, 1));
			
			this.tx1 = mock(Txaction.class);
			when(tx1.getDatePosted()).thenReturn(date(2008, 10, 7));
			this.item1 = mock(TxactionListItem.class);
			when(item1.getBalance()).thenReturn(money("200.00", USD));
			when(item1.getTxaction()).thenReturn(tx1);
			
			this.tx2 = mock(Txaction.class);
			when(tx2.getDatePosted()).thenReturn(date(2008, 10, 16));
			this.item2 = mock(TxactionListItem.class);
			when(item2.getBalance()).thenReturn(money("300.00", USD));
			when(item2.getTxaction()).thenReturn(tx2);
			
			this.txactionList = new TxactionList(item1, item2);
			when(listBuilder.build(Mockito.anyCollection())).thenReturn(txactionList);
			
			this.summarizer = new NetWorthSummarizer(listBuilderProvider);
		}
		
		@Test
		public void itBuildsTheTransactionList() throws Exception {
			summarizer.summarize(accounts, txactions, dateRange, IntervalType.WEEKLY, USD, ImmutableSet.<Tag>of());
			
			final InOrder inOrder = inOrder(listBuilder);
			inOrder.verify(listBuilder).setAccounts(accounts);
			inOrder.verify(listBuilder).setCurrency(USD);
			inOrder.verify(listBuilder).setCalculateBalances(true);
			inOrder.verify(listBuilder).build(txactions);
		}
		
		@Test
		public void itReturnsAMapOfIntervalsToBalances() throws Exception {
			final ImmutableMap<Interval, Money> summaries = summarizer.summarize(accounts, txactions, dateRange, IntervalType.WEEKLY, USD, ImmutableSet.<Tag>of());
			
			assertThat(summaries).hasSize(5);
			
			assertThat(summaries.get(new Interval(date(2008,  9, 29), date(2008, 10,  6)))).isEqualTo(money(  "0.00", USD));
			assertThat(summaries.get(new Interval(date(2008, 10,  6), date(2008, 10, 13)))).isEqualTo(money("200.00", USD));
			assertThat(summaries.get(new Interval(date(2008, 10, 13), date(2008, 10, 20)))).isEqualTo(money("300.00", USD));
			assertThat(summaries.get(new Interval(date(2008, 10, 20), date(2008, 10, 27)))).isEqualTo(money("300.00", USD));
			assertThat(summaries.get(new Interval(date(2008, 10, 27), date(2008, 11,  3)))).isEqualTo(money("300.00", USD));
		}
	}
}

package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.fest.assertions.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.wesabe.api.accounts.analytics.IntervalSummarizer;
import com.wesabe.api.accounts.analytics.IntervalType;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.analytics.MonetarySummaryWithSplits;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionStatus;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class IntervalSummarizerTest {
	public static class Summarizing_A_Single_Day_By_Day {
		private Interval interval = new Interval(date(2009, 1, 18), date(2009, 1, 19));
		private ImmutableMap<Interval, MonetarySummaryWithSplits> summaries;
		private Account checking = new Account("Checking", USD);
		private Txaction paidRent = new Txaction(checking, decimal("-1500.00"), date(2009, 1, 18));
		private Txaction boughtLunch = new Txaction(checking, decimal("12.34"), new DateTime(2009, 1, 18, 23, 0, 0, 0));
		
		@Before
		public void setup() {
			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());
			
			this.summaries = summarizer.summarize(ImmutableList.of(paidRent, boughtLunch), interval, IntervalType.DAILY, USD, null);
		}
		
		@Test
		public void itBuildsASingleSummary() throws Exception {
			assertThat(summaries).hasSize(1);
		}
		
		@Test
		public void itSummarizesSpendingTransactions() throws Exception {
			final MonetarySummary summary = summaries.get(interval);
			
			assertThat(summary.getSpending().getAmount()).isEqualTo(money("1500.00", USD));
			assertThat(summary.getSpending().getCount()).isEqualTo(1);
		}
		
		@Test
		public void itSummarizesEarningsTransactions() throws Exception {
			final MonetarySummary summary = summaries.get(interval);
			
			assertThat(summary.getEarnings().getAmount()).isEqualTo(money("12.34", USD));
			assertThat(summary.getEarnings().getCount()).isEqualTo(1);
		}

		@Test
		public void itSummarizesNetTransactions() throws Exception {
			final MonetarySummary summary = summaries.get(interval);

			
			assertThat(summary.getNet().getAmount()).isEqualTo(money("-1487.66", USD));
			assertThat(summary.getNet().getCount()).isEqualTo(2);
		}

		@Test
		public void itSummarizesTheEntireInterval() throws Exception {
			final List<Interval> intervals = ImmutableList.copyOf(summaries.keySet());
			
			
			final DateTime start = intervals.get(0).getStart();
			final DateTime end = intervals.get(intervals.size() - 1).getEnd();

			assertThat(start).isEqualTo(interval.getStart());
			assertThat(end).isEqualTo(interval.getEnd());
		}
	}
	
	public static class Summarizing_Three_Days_By_Day {
		private Interval interval = new Interval(date(2009, 1, 18), date(2009, 1, 21));
		private ImmutableMap<Interval, MonetarySummaryWithSplits> summaries;
		private Collection<Txaction> txactions = ImmutableList.of();

		@Before
		public void setup() {
			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());

			this.summaries = summarizer.summarize(txactions, interval, IntervalType.DAILY, USD, null);
		}

		@Test
		public void itBuildsThreeSummaries() throws Exception {
			assertThat(summaries).hasSize(3);
		}

		@Test
		public void itSummarizesTheEntireInterval() throws Exception {
			final List<Interval> intervals = ImmutableList.copyOf(summaries.keySet());
			
			final DateTime start = intervals.get(0).getStart();
			final DateTime end = intervals.get(intervals.size() - 1).getEnd();

			assertThat(start).isEqualTo(interval.getStart());
			assertThat(end).isEqualTo(interval.getEnd());
		}
	}

	public static class Summarizing_Three_Months_By_Week {
		private Interval interval = new Interval(date(2009, 1, 2), date(2009, 4, 1));
		private ImmutableMap<Interval,MonetarySummaryWithSplits> summaries;
		private Collection<Txaction> txactions = ImmutableList.of();

		@Before
		public void setup() {
			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());

			this.summaries = summarizer.summarize(txactions, interval, IntervalType.WEEKLY, USD, null);
		}

		@Test
		public void itBuildsFourteenSummaries() throws Exception {
			assertThat(summaries).hasSize(14);
		}

		@Test
		public void itSummarizesTheEntireIntervalIncludingBeginningAndEndingPadding() throws Exception {
			final List<Interval> intervals = ImmutableList.copyOf(summaries.keySet());
			
			final DateTime start = intervals.get(0).getStart();
			final DateTime end = intervals.get(intervals.size() - 1).getEnd();

			assertThat(start).isEqualTo(date(2008, 12, 29));
			assertThat(end).isEqualTo(date(2009, 4, 6));
		}
	}

	public static class Summarizing_Deleted_Txactions {
		private Interval interval = new Interval(date(2009, 1, 2), date(2009, 4, 1));
		private ImmutableMap<Interval,MonetarySummaryWithSplits> summaries;

		@Before
		public void setup() {
			final Account account = new Account("Checking", USD);

			final Txaction txaction = new Txaction(account, decimal("-30.00"), date(2009, 2, 13));
			txaction.setStatus(TxactionStatus.DELETED);

			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());

			this.summaries = summarizer.summarize(ImmutableList.of(txaction), interval, IntervalType.MONTHLY, USD, null);
		}

		@Test
		public void itDoesNotIncludeDeletedTxactions() throws Exception {
			final List<MonetarySummaryWithSplits> values = ImmutableList.copyOf(summaries.values());
			
			assertThat(values.get(1).getNet().getCount()).isZero();
		}
	}

	public static class Summarizing_Disabled_Txactions {
		private Interval interval = new Interval(date(2009, 1, 2), date(2009, 4, 1));
		private ImmutableMap<Interval,MonetarySummaryWithSplits> summaries;

		@Before
		public void setup() {
			final Account account = new Account("Checking", USD);

			final Txaction txaction = new Txaction(account, decimal("-30.00"), date(2009, 2, 13));
			txaction.setStatus(TxactionStatus.DISABLED);

			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());

			this.summaries = summarizer.summarize(ImmutableList.of(txaction), interval, IntervalType.MONTHLY, USD, null);
		}

		@Test
		public void itDoesNotIncludeDisabledTxactions() throws Exception {
			final List<MonetarySummaryWithSplits> values = ImmutableList.copyOf(summaries.values());
			
			assertThat(values.get(1).getNet().getCount()).isZero();
		}
	}

	public static class Summarizing_Transfer_Txactions {
		private Interval interval = new Interval(date(2009, 1, 2), date(2009, 4, 1));
		private ImmutableMap<Interval,MonetarySummaryWithSplits> summaries;

		@Before
		public void setup() {
			final Account account = new Account("Checking", USD);
			final Txaction txaction = new Txaction(account, decimal("-30.00"), date(2009, 2, 13));
			txaction.setTransferTxaction(new Txaction());

			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());

			this.summaries = summarizer.summarize(ImmutableList.of(txaction), interval, IntervalType.MONTHLY, USD, null);
		}

		@Test
		public void itDoesNotIncludeTransferTxactions() throws Exception {
			final List<MonetarySummaryWithSplits> values = ImmutableList.copyOf(summaries.values());
			
			assertThat(values.get(1).getNet().getCount()).isZero();
		}
	}

	public static class Summarizing_Txactions_With_The_Same_Amounts {
		private Interval interval = new Interval(date(2009, 1, 2), date(2009, 4, 1));
		private ImmutableMap<Interval,MonetarySummaryWithSplits> summaries;

		@Before
		public void setup() {
			final Account account = new Account("Checking", USD);
			final Txaction txaction1 = new Txaction(account, decimal("-30.00"), date(2009, 2, 13));
			final Txaction txaction2 = new Txaction(account, decimal("-30.00"), date(2009, 2, 14));

			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());

			this.summaries = summarizer.summarize(ImmutableList.of(txaction1, txaction2), interval, IntervalType.MONTHLY, USD, null);
		}

		@Test
		public void itSummarizesAllOfTheTxactions() throws Exception {
			final List<MonetarySummaryWithSplits> values = ImmutableList.copyOf(summaries.values());
			
			assertThat(values.get(1).getNet().getCount()).isEqualTo(2);
			assertThat(values.get(1).getSpending().getAmount()).isEqualTo(money("60.00", USD));
		}
	}

	public static class Summarizing_Tagged_Txactions {
		private Interval interval = new Interval(date(2009, 1, 2), date(2009, 4, 1));
		private ImmutableMap<Interval,MonetarySummaryWithSplits> summaries;

		@Before
		public void setup() {
			final Account account = new Account("Checking", USD);
			final Txaction txaction = new Txaction(account, decimal("-30.00"), date(2009, 2, 13));
			txaction.addTag(new Tag("food"));

			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());
			this.summaries = summarizer.summarize(ImmutableList.of(txaction), interval, IntervalType.MONTHLY, USD, null);
		}

		@Test
		public void itSummarizesAllOfTheTxactions() throws Exception {
			final List<MonetarySummaryWithSplits> values = ImmutableList.copyOf(summaries.values());
			
			assertThat(values.get(1).getNet().getCount()).isEqualTo(1);
			assertThat(values.get(1).getSpending().getAmount()).isEqualTo(money("30.00", USD));
		}
	}

	public static class Summarizing_Filtered_Txactions {
		private Interval interval = new Interval(date(2009, 1, 2), date(2009, 4, 1));
		private Tag workExpense = new Tag("work-expense");
		private Tag loan = new Tag("loan");
		private Set<Tag> filteredTags = ImmutableSet.of(workExpense, loan);
		private ImmutableMap<Interval,MonetarySummaryWithSplits> summaries;

		@Before
		public void setup() {
			final Account account = new Account("Checking", USD);
			final Txaction fullyFiltered = new Txaction(account, decimal("-30.00"), date(2009, 3, 13));
			fullyFiltered.addTag(workExpense);

			final Txaction partiallyFiltered = new Txaction(account, decimal("-30.00"), date(2009, 2, 13));
			partiallyFiltered.addTag(loan, decimal("-20.00"));

			final IntervalSummarizer summarizer = new IntervalSummarizer(new CurrencyExchangeRateMap());

			this.summaries = summarizer.summarize(ImmutableList.of(fullyFiltered, partiallyFiltered), interval, IntervalType.MONTHLY, USD, filteredTags);
		}

		@Test
		public void itDoesNotIncludeFullyFilteredTxactions() throws Exception {
			final List<MonetarySummaryWithSplits> values = ImmutableList.copyOf(summaries.values());
			
			assertThat(values.get(2).getNet().getCount()).isZero();
		}

		@Test
		public void itIncludesOnlyTheUnfilteredPortionOfPartiallyFilteredTxactions() throws Exception {
			final List<MonetarySummaryWithSplits> values = ImmutableList.copyOf(summaries.values());
			
			assertThat(values.get(1).getNet().getCount()).isEqualTo(1);
			assertThat(values.get(1).getNet().getAmount()).isEqualTo(money("-10.00", USD));
		}
	}
}

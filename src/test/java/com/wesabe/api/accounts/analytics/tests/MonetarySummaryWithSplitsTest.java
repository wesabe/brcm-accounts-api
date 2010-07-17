package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.analytics.MonetarySummaryWithSplits;
import com.wesabe.api.accounts.analytics.SumOfMoney;
import com.wesabe.api.accounts.entities.Tag;

@RunWith(Enclosed.class)
public class MonetarySummaryWithSplitsTest {
	public static class A_Monetary_Summary {
		private final SumOfMoney splitSpending = new SumOfMoney(money("10.00", USD), 2);
		private final SumOfMoney splitEarnings = new SumOfMoney(money("20.00", USD), 1);
		private final MonetarySummary splitSummary = new MonetarySummary(splitSpending, splitEarnings);
		private final Tag tag = new Tag("food");
		private final SumOfMoney spending = new SumOfMoney(money("100.00", USD), 2);
		private final SumOfMoney earnings = new SumOfMoney(money("200.00", USD), 1);
		private final MonetarySummaryWithSplits summary = new MonetarySummaryWithSplits(spending, earnings, ImmutableMap.of(tag, splitSummary));
		
		@Test
		public void itHasASpendingSummary() throws Exception {
			assertThat(summary.getSpending()).isEqualTo(spending);
		}
		
		@Test
		public void itHasAnEarningsSummary() throws Exception {
			assertThat(summary.getEarnings()).isEqualTo(earnings);
		}
		
		@Test
		public void itHasANetSummary() throws Exception {
			assertThat(summary.getNet().getAmount()).isEqualTo(money("100.00", USD));
			assertThat(summary.getNet().getCount()).isEqualTo(3);
		}
		
		@Test
		public void itHasAMapOfTagsToSplitSummaries() throws Exception {
			final MonetarySummary s = summary.getSplitSummaries().get(tag);
			
			assertThat(s).isNotNull();
			assertThat(s.getEarnings()).isEqualTo(splitEarnings);
			assertThat(s.getSpending()).isEqualTo(splitSpending);
			assertThat(s.getNet().getAmount()).isEqualTo(money("10.00", USD));
			assertThat(s.getNet().getCount()).isEqualTo(3);
		}
	}
}

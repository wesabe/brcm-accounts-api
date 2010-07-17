package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.analytics.SumOfMoney;
import com.wesabe.api.util.money.Money;

@RunWith(Enclosed.class)
public class MonetarySummaryTest {
	public static class A_Monetary_Summary {
		private final SumOfMoney spending = new SumOfMoney(money("10.00", USD), 2);
		private final SumOfMoney earnings = new SumOfMoney(money("20.00", USD), 1);
		private final MonetarySummary summary = new MonetarySummary(spending, earnings);
		
		@Test
		public void itHasASpendingSummary() throws Exception {
			assertEquals(spending, summary.getSpending());
		}
		
		@Test
		public void itHasAnEarningsSummary() throws Exception {
			assertEquals(earnings, summary.getEarnings());
		}
		
		@Test
		public void itHasANetSummary() throws Exception {
			assertEquals(money("10.00", USD), summary.getNet().getAmount());
			assertEquals(3, summary.getNet().getCount());
		}
	}
	
	public static class Summarizing_Money {
		private final Collection<Money> amounts = ImmutableList.of(
			money("10.01", USD),
			money("100.00", USD),
			money("-32.66", USD),
			money("-50.00", USD),
			money("-50.00", USD)
		);
		private final MonetarySummary summary = MonetarySummary.summarize(amounts, USD);
		
		@Test
		public void itSummarizesAnEarningsCount() throws Exception {
			assertEquals(2, summary.getEarnings().getCount());
		}
		
		@Test
		public void itSummarizesAnEarningsAmount() throws Exception {
			assertEquals(money("110.01", USD), summary.getEarnings().getAmount());
		}
		
		@Test
		public void itSummarizesAnSpendingCount() throws Exception {
			assertEquals(3, summary.getSpending().getCount());
		}
		
		@Test
		public void itSummarizesASpendingAmount() throws Exception {
			assertEquals(money("132.66", USD), summary.getSpending().getAmount());
		}
	}
}

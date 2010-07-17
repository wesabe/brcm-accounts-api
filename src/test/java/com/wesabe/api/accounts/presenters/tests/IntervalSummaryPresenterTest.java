package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.fest.assertions.Assertions.*;

import java.util.Locale;

import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.analytics.MonetarySummaryWithSplits;
import com.wesabe.api.accounts.analytics.SumOfMoney;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.presenters.IntervalSummaryPresenter;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.accounts.presenters.SumOfMoneyPresenter;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class IntervalSummaryPresenterTest {
	public static class The_Representation_Of_An_Interval_Summary {
		private Tag food;
		private Interval interval;
		private MonetarySummary foodSummary;
		private MonetarySummaryWithSplits summary;
		private IntervalSummaryPresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.food = new Tag("food");
			
			this.foodSummary = new MonetarySummary(new SumOfMoney(money("45.00", USD), 6), new SumOfMoney(Money.zero(USD), 0));
			
			this.presenter = new IntervalSummaryPresenter(new SumOfMoneyPresenter((new MoneyPresenter())));
			
			this.interval = new Interval(date(2006, 7, 1), date(2006, 8, 1));
			
			this.summary = new MonetarySummaryWithSplits(
				new SumOfMoney(money("3409.32", USD), 17),
				new SumOfMoney(money("2091.11", USD), 5),
				ImmutableMap.of(food, foodSummary)
			);
		}
		
		@Test
		public void itIsNamedIntervalSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(interval, summary), Locale.CANADA_FRENCH);
			
			assertThat(representation.getName()).isEqualTo("interval-summary");
		}
		
		@Test
		public void itHasAnArrayOfSummaries() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(interval, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			
			assertThat(summaries.getMembers()).hasSize(1);
		}
		
		@Test
		public void itHasTheSpendingDataForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(interval, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			
			assertThat(summary.getName()).isEqualTo("summary");
			
			final XmlsonObject spending = (XmlsonObject) summary.get("spending");
			
			assertThat(spending.getInteger("count")).isEqualTo(17);
			assertThat(spending.getString("value")).isEqualTo("3409.32");
			assertThat(spending.getString("display")).isEqualTo("3 409,32 $ US");
		}
		
		@Test
		public void itHasTheEarningsDataForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(interval, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			
			assertThat(summary.getName()).isEqualTo("summary");
			
			final XmlsonObject earnings = (XmlsonObject) summary.get("earnings");
			
			assertThat(earnings.getInteger("count")).isEqualTo(5);
			assertThat(earnings.getString("value")).isEqualTo("2091.11");
			assertThat(earnings.getString("display")).isEqualTo("2 091,11 $ US");
		}
		
		@Test
		public void itHasTheNetDataForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(interval, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			
			assertThat(summary.getName()).isEqualTo("summary");
			
			final XmlsonObject net = (XmlsonObject) summary.get("net");
			
			assertThat(net.getInteger("count")).isEqualTo(22);
			assertThat(net.getString("value")).isEqualTo("-1318.21");
			assertThat(net.getString("display")).isEqualTo("(1 318,21 $ US)");
		}
		
		@Test
		public void itHasTheIntervalForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(interval, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			final XmlsonObject interval = (XmlsonObject) summary.get("interval");
			
			assertThat(interval.getString("start")).isEqualTo("20060701");
			assertThat(interval.getString("end")).isEqualTo("20060801");
		}
		
		@Test
		public void itHasTheSplitSummariesForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(interval, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			
			final XmlsonArray splits = (XmlsonArray) summary.get("splits");
			
			
			
			final XmlsonObject foodSummary = (XmlsonObject) splits.getMembers().get(0);
			
			final XmlsonObject tag = (XmlsonObject) foodSummary.get("tag");
			assertThat(tag.getString("name")).isEqualTo("food");
			
			final XmlsonObject spending = (XmlsonObject) foodSummary.get("spending");
			
			assertThat(spending.getInteger("count")).isEqualTo(6);
			assertThat(spending.getString("value")).isEqualTo("45.00");
			assertThat(spending.getString("display")).isEqualTo("45,00 $ US");
			
			final XmlsonObject earnings = (XmlsonObject) foodSummary.get("earnings");
			
			assertThat(earnings.getInteger("count")).isEqualTo(0);
			assertThat(earnings.getString("value")).isEqualTo("0.00");
			assertThat(earnings.getString("display")).isEqualTo("0,00 $ US");
			
			final XmlsonObject net = (XmlsonObject) foodSummary.get("net");
			
			assertThat(net.getInteger("count")).isEqualTo(6);
			assertThat(net.getString("value")).isEqualTo("-45.00");
			assertThat(net.getString("display")).isEqualTo("(45,00 $ US)");
		}
	}
}

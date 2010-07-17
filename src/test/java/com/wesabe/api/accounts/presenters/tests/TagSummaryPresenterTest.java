package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.analytics.SumOfMoney;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.accounts.presenters.SumOfMoneyPresenter;
import com.wesabe.api.accounts.presenters.TagSummaryPresenter;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class TagSummaryPresenterTest {
	public static class The_Representation_Of_A_Tag_Summary {
		private MonetarySummary summary;
		private TagSummaryPresenter presenter;
		private Tag tag;
		
		@Before
		public void setup() throws Exception {
			this.presenter = new TagSummaryPresenter(new SumOfMoneyPresenter((new MoneyPresenter())));
			
			this.tag = new Tag("food");
			
			this.summary = new MonetarySummary(
				new SumOfMoney(money("3409.32", USD), 17),
				new SumOfMoney(money("2091.11", USD), 5)
			);
		}
		
		@Test
		public void itItNamedTagSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(tag, summary), Locale.CANADA_FRENCH);
			
			assertThat(representation.getName(), is("tag-summary"));
		}
		
		@Test
		public void itHasAnArrayOfSummaries() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(tag, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			
			assertThat(summaries.getMembers().size(), is(1));
		}
		
		@Test
		public void itHasTheSpendingDataForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(tag, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			
			assertThat(summary.getName(), is("summary"));
			
			final XmlsonObject spending = (XmlsonObject) summary.get("spending");
			
			assertThat(spending.getInteger("count"), is(17));
			assertThat(spending.getString("value"), is("3409.32"));
			assertThat(spending.getString("display"), is("3 409,32 $ US"));
		}
		
		@Test
		public void itHasTheEarningsDataForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(tag, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			
			assertThat(summary.getName(), is("summary"));
			
			final XmlsonObject earnings = (XmlsonObject) summary.get("earnings");
			
			assertThat(earnings.getInteger("count"), is(5));
			assertThat(earnings.getString("value"), is("2091.11"));
			assertThat(earnings.getString("display"), is("2 091,11 $ US"));
		}
		
		@Test
		public void itHasTheNetDataForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(tag, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			
			assertThat(summary.getName(), is("summary"));
			
			final XmlsonObject net = (XmlsonObject) summary.get("net");
			
			assertThat(net.getInteger("count"), is(22));
			assertThat(net.getString("value"), is("-1318.21"));
			assertThat(net.getString("display"), is("(1 318,21 $ US)"));
		}
		
		@Test
		public void itHasTheTagForEachSummary() throws Exception {
			final XmlsonObject representation = presenter.present(ImmutableMap.of(tag, summary), Locale.CANADA_FRENCH);
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			final XmlsonObject tag = (XmlsonObject) summary.get("tag");
			
			assertThat(tag.getString("name"), is("food"));
		}
	}
}

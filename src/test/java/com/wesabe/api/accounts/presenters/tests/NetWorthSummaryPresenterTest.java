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
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.accounts.presenters.NetWorthSummaryPresenter;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class NetWorthSummaryPresenterTest {
	public static class The_Representation_Of_A_Net_Worth_Summary {
		private MoneyPresenter moneyPresenter;
		private NetWorthSummaryPresenter presenter;
		private ImmutableMap<Interval, Money> data;
		
		@Before
		public void setup() throws Exception {
			this.moneyPresenter = new MoneyPresenter();
			this.presenter = new NetWorthSummaryPresenter(moneyPresenter);
			
			this.data = ImmutableMap.of(
				new Interval(date(2007, 1, 1), date(2007, 2, 1)),
				money("400.00", USD)
			);
		}
		
		@Test
		public void itItNamedNetWorthSummary() throws Exception {
			final XmlsonObject representation = presenter.present(data, Locale.CANADA_FRENCH);
			
			assertThat(representation.getName()).isEqualTo("net-worth-summary");
		}
		
		@Test
		public void itItHasSummaries() throws Exception {
			final XmlsonObject representation = presenter.present(data, Locale.CANADA_FRENCH);
			
			final XmlsonArray summaries = (XmlsonArray) representation.get("summaries");
			assertThat(summaries.getMembers()).hasSize(1);
			
			final XmlsonObject summary = (XmlsonObject) summaries.getMembers().get(0);
			assertThat(summary.getName()).isEqualTo("summary");
			assertThat(((XmlsonObject) summary.get("interval")).getString("start")).isEqualTo("20070101");
			assertThat(((XmlsonObject) summary.get("interval")).getString("end")).isEqualTo("20070201");
			assertThat(((XmlsonObject) summary.get("balance")).getString("display")).isEqualTo("400,00 $ US");
			assertThat(((XmlsonObject) summary.get("balance")).getString("value")).isEqualTo("400.00");
		}
	}
}

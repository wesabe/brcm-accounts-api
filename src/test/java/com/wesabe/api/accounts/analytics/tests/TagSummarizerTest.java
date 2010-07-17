package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.fest.assertions.Assertions.*;

import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.analytics.TagSummarizer;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionStatus;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class TagSummarizerTest {
	public static class Summarizing_A_Set_Of_Transactions {
		private ImmutableMap<Tag, MonetarySummary> summaries;
		private Tag rent = new Tag("rent");
		private Tag lunch = new Tag("lunch");
		private Tag food = new Tag("food");
		private Tag restaurants = new Tag("restaurants");
		private Tag friends = new Tag("friends");
		
		@Before
		public void setup() {
			final Account checking = new Account("Checking", USD);
			final Txaction paidRent = new Txaction(checking, decimal("-1500.00"), date(2009, 1, 18));
			final Txaction boughtLunch = new Txaction(checking, decimal("-12.34"), new DateTime(2009, 1, 18, 23, 0, 0, 0));
			final Txaction deletedLunch = new Txaction(checking, decimal("-12.33"), new DateTime(2009, 1, 18, 23, 0, 0, 0));
			final Collection<Txaction> txactions = ImmutableList.of(paidRent, boughtLunch, deletedLunch);
			
			paidRent.addTag(rent);
			boughtLunch.addTag(lunch);
			boughtLunch.addTag(food);
			boughtLunch.addTag(restaurants);
			boughtLunch.addTag(friends, decimal("-6.45"));
			
			deletedLunch.addTag(lunch);
			deletedLunch.addTag(food);
			deletedLunch.addTag(restaurants);
			deletedLunch.addTag(friends, decimal("-6.45"));
			deletedLunch.setStatus(TxactionStatus.DELETED);
			
			final TagSummarizer summarizer = new TagSummarizer(new CurrencyExchangeRateMap());
			
			this.summaries = summarizer.summarize(txactions, USD);
		}
		
		@Test
		public void itGeneratesASummaryForEachTag() throws Exception {
			assertThat(summaries.keySet()).containsOnly(rent, lunch, food, restaurants, friends);
		}
		
		@Test
		public void itSummarizesAmountsOfTags() throws Exception {
			assertThat(summaries.get(rent).getSpending().getAmount()).isEqualTo(money("1500.00", USD));
			assertThat(summaries.get(rent).getEarnings().getAmount()).isEqualTo(money("0.00", USD));
			
			assertThat(summaries.get(lunch).getSpending().getAmount()).isEqualTo(money("12.34", USD));
			assertThat(summaries.get(lunch).getEarnings().getAmount()).isEqualTo(money("0.00", USD));
			
			assertThat(summaries.get(food).getSpending().getAmount()).isEqualTo(money("12.34", USD));
			assertThat(summaries.get(food).getEarnings().getAmount()).isEqualTo(money("0.00", USD));
		}
		
		@Test
		public void itSummariesSplitAmounts() throws Exception {
			assertThat(summaries.get(friends).getSpending().getAmount()).isEqualTo(money("6.45", USD));
			assertThat(summaries.get(friends).getEarnings().getAmount()).isEqualTo(money("0.00", USD));
		}
	}
}

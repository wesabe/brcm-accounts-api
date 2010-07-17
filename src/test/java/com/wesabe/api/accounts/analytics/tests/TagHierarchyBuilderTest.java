package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static com.wesabe.api.tests.util.TagHelper.*;
import static com.wesabe.api.tests.util.TxactionHelper.*;
import static org.fest.assertions.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wesabe.api.accounts.analytics.TagHierarchy;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder;
import com.wesabe.api.accounts.analytics.TagSummarizer;
import com.wesabe.api.accounts.analytics.TagHierarchy.Node;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder.HierarchyType;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder.TagImportanceScheme;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class TagHierarchyBuilderTest {
	private static abstract class Context {
		protected TagHierarchyBuilder builder;
		protected CurrencyExchangeRateMap exchangeRateMap;
		protected TagSummarizer summarizer;
		protected TagHierarchy hierarchy;
		
		
		protected void setup() throws Exception {
			this.exchangeRateMap = new CurrencyExchangeRateMap();
			this.summarizer = new TagSummarizer(exchangeRateMap);
			this.builder = new TagHierarchyBuilder(summarizer, exchangeRateMap);
		}
	}
	
	public static class Marcs_Sample_Data extends Context {
		/*
		 * Whole Foods - food groceries - $400.00
		 * Credit Card Payment - transfer creditcard - $500.00
		 * Andronico's - food groceries - $200.00
		 * Rent Check - rent - $1500.00
		 * Dopo - food restaurant italian - $100.00
		 * Annual Fee - creditcard fee - $50.00
		 * Zuni - food restaurant fancy - $300.00
		 * Peet's - coffee restaurant - $5.00
		 * 
		 * Total amount actually spent: $3055.00
		 * Filters on Spending Summary: transfer
		 */
		private Account checking;
		
		
		@Override
		@Before
		public void setup() throws Exception {
			super.setup();
			
			this.checking = new Account("Checking", USD);
			
			this.hierarchy = builder.build(
				ImmutableList.of(
					spent("400.00").from(checking).at("Whole Foods").on("food", "groceries").build(),
					spent("500.00").from(checking).at("Credit Card Payment").on("transfer", "creditcard").build(),
					spent("200.00").from(checking).at("Andronico's").on("food", "groceries").build(),
					spent("1500.00").from(checking).at("Rent Check").on("rent").build(),
					spent("100.00").from(checking).at("Dopo").on("food", "restaurant", "italian").build(),
					spent("50.00").from(checking).at("Annual Fee").on("creditcard", "fee").build(),
					spent("300.00").from(checking).at("Zuni").on("food", "restaurant", "fancy").build(),
					spent("5.00").from(checking).at("Peet's").on("coffee", "restaurant").build()
				),
				USD,
				TagImportanceScheme.RANK_BY_AMOUNT,
				HierarchyType.SPENDING,
				ImmutableSet.of(tag("transfer")),
				5
			);
		}
		
		@Test
		public void itTotalsTheData() throws Exception {
			assertThat(hierarchy.getSum().getCount()).isEqualTo(7);
			assertThat(hierarchy.getSum().getAmount()).isEqualTo(money("2555.00", USD));
		}
		
		@Test
		public void itUsesFoodCreditCardRestaurantAndRentAsTheTopTags() throws Exception {
			final ImmutableList<Tag> topTags = ImmutableList.copyOf(hierarchy.getChildren().keySet());
			
			assertThat(topTags).containsOnly(
				tag("food"),
				tag("creditcard"),
				tag("restaurant"),
				tag("rent")
			);
		}
		
		@Test
		public void itUsesFoodAsATopTag() throws Exception {
			final Node food = hierarchy.getChildren().get(tag("food"));
			assertThat(food.getSum().getAmount()).isEqualTo(money("1000.00", USD));
			assertThat(food.getSum().getCount()).isEqualTo(4);
		}
		
		@Test
		public void itNestsTheFancyTagUnderFood() throws Exception {
			final Node food = hierarchy.getChildren().get(tag("food"));
			final Node fancy = food.getChildren().get(tag("fancy"));
			assertThat(fancy.getSum().getAmount()).isEqualTo(money("300.00", USD));
			assertThat(fancy.getSum().getCount()).isEqualTo(1);
			assertThat(fancy.getChildren()).isEmpty();
		}
		
		@Test
		public void itNestsTheGroceriesTagtUnderFood() throws Exception {
			final Node food = hierarchy.getChildren().get(tag("food"));
			final Node groceries = food.getChildren().get(tag("groceries"));
			assertThat(groceries.getSum().getAmount()).isEqualTo(money("600.00", USD));
			assertThat(groceries.getSum().getCount()).isEqualTo(2);
			assertThat(groceries.getChildren()).isEmpty();
		}
		
		@Test
		public void itNestsTheItalianTagtUnderFood() throws Exception {
			final Node food = hierarchy.getChildren().get(tag("food"));
			final Node italian = food.getChildren().get(tag("italian"));
			assertThat(italian.getSum().getAmount()).isEqualTo(money("100.00", USD));
			assertThat(italian.getSum().getCount()).isEqualTo(1);
			assertThat(italian.getChildren()).isEmpty();
		}
		
		@Test
		public void itUsesCreditCardAsATopTag() throws Exception {
			final Node creditcard = hierarchy.getChildren().get(tag("creditcard"));
			assertThat(creditcard.getSum().getAmount()).isEqualTo(money("50.00", USD));
			assertThat(creditcard.getSum().getCount()).isEqualTo(1);
		}
		
		@Test
		public void itNestsTheFeeTagUnderCreditCard() throws Exception {
			final Node creditcard = hierarchy.getChildren().get(tag("creditcard"));
			final Node fee = creditcard.getChildren().get(tag("fee"));
			assertThat(fee.getSum().getAmount()).isEqualTo(money("50.00", USD));
			assertThat(fee.getSum().getCount()).isEqualTo(1);
			assertThat(fee.getChildren()).isEmpty();
		}
		
		@Test
		public void itUsesRestaurantAsATopTag() throws Exception {
			final Node restaurant = hierarchy.getChildren().get(tag("restaurant"));
			assertThat(restaurant.getSum().getAmount()).isEqualTo(money("5.00", USD));
			assertThat(restaurant.getSum().getCount()).isEqualTo(1);
		}
		
		@Test
		public void itNestsTheCoffeeTagUnderCreditCard() throws Exception {
			final Node restaurant = hierarchy.getChildren().get(tag("restaurant"));
			final Node coffee = restaurant.getChildren().get(tag("coffee"));
			assertThat(coffee.getSum().getAmount()).isEqualTo(money("5.00", USD));
			assertThat(coffee.getSum().getCount()).isEqualTo(1);
			assertThat(coffee.getChildren()).isEmpty();
		}
		
		@Test
		public void itUsesRentAsATopTag() throws Exception {
			final Node rent = hierarchy.getChildren().get(tag("rent"));
			assertThat(rent.getSum().getAmount()).isEqualTo(money("1500.00", USD));
			assertThat(rent.getSum().getCount()).isEqualTo(1);
			assertThat(rent.getChildren()).isEmpty();
		}
	}
	
	public static class Smallest_Dataset_With_Splits extends Context {
		/*
		 * Whole Foods - food groceries - $400.00
		 * ATM         - cash:100 fee:3.50 - $103.50
		 */
		private Account checking;

		@Override
		@Before
		public void setup() throws Exception {
			super.setup();
			
			this.checking = new Account("Checking", USD);
			this.hierarchy = builder.build(
				ImmutableList.of(
					spent("400.00").from(checking).at("Whole Foods").on("food", "groceries").build(),
					spent("103.50").from(checking).at("ATM").on("cash", decimal("-100.00")).on("fee", decimal("-3.50")).build()
				),
				USD,
				TagImportanceScheme.RANK_BY_AMOUNT,
				HierarchyType.SPENDING,
				ImmutableSet.<Tag>of(),
				5
			);
		}

        @Test
        public void itCountsFeeAsARootTag() throws Exception {
            final Node fee = hierarchy.getChildren().get(tag("fee"));
            assertThat(fee.getSum().getAmount()).isEqualTo(money("3.50", USD));
            assertThat(fee.getSum().getCount()).isEqualTo(1);

            assertThat(fee.getChildren()).isEmpty();
        }

        @Test
        public void itCountsCashAsARootTag() throws Exception {
            final Node cash = hierarchy.getChildren().get(tag("cash"));
            assertThat(cash.getSum().getAmount()).isEqualTo(money("100.00", USD));
            assertThat(cash.getSum().getCount()).isEqualTo(1);

            assertThat(cash.getChildren()).isEmpty();
        }

        @Test
        public void itCountsFoodAsARootTag() throws Exception {
            final Node food = hierarchy.getChildren().get(tag("food"));
            assertThat(food.getSum().getAmount()).isEqualTo(money("400.00", USD));
            assertThat(food.getSum().getCount()).isEqualTo(1);

            assertThat(food.getChildren()).hasSize(1);
        }

        @Test
        public void itCountsGroceriesUnderTheFoodTag() throws Exception {
            final Node food = hierarchy.getChildren().get(tag("food"));
            final Node groceries = food.getChildren().get(tag("groceries"));

            assertThat(groceries.getSum().getAmount()).isEqualTo(money("400.00", USD));
            assertThat(groceries.getSum().getCount()).isEqualTo(1);

            assertThat(groceries.getChildren()).isEmpty();
        }
	}
	
	public static class Too_Many_Slices_To_Display extends Context {
		/*
		 * Whole Foods - food - $400.00
		 * ATM         - cash - $103.50
		 * Rent        - rent - $200.00
		 */
		private Account checking;

		@Override
		@Before
		public void setup() throws Exception {
			super.setup();
			
			this.checking = new Account("Checking", USD);
			this.hierarchy = builder.build(
				ImmutableList.of(
					spent("400.00").from(checking).at("Whole Foods").on("food").build(),
					spent("103.50").from(checking).at("ATM").on("cash").build(),
					spent("200.00").from(checking).at("Rent").on("rent").build()
				),
				USD,
				TagImportanceScheme.RANK_BY_AMOUNT,
				HierarchyType.SPENDING,
				ImmutableSet.<Tag>of(),
				2
			);
		}
		
		@Test
		public void itUsesTheBiggestTwoTagsAsTopTags() throws Exception {
			assertThat(hierarchy.getChildren().keySet()).contains(tag("rent"));
			assertThat(hierarchy.getChildren().keySet()).contains(tag("food"));
		}
		
		@Test
		public void itDoesNotIncludeTheSmallerTag() throws Exception {
			assertThat(hierarchy.getChildren().keySet()).excludes(tag("cash"));
		}
		
		@Test
		public void itIncludesTheOtherTag() throws Exception {
			assertThat(hierarchy.getChildren().keySet()).contains(TagHierarchyBuilder.OTHER);
		}
	}
	
	public static class Untagged_Txactions extends Context {
		/*
		 * Whole Foods - {} - $400.00
		 * ATM         - {} - $103.50
		 * Rent        - {} - $200.00
		 */
		private Account checking;

		@Override
		@Before
		public void setup() throws Exception {
			super.setup();
			
			this.checking = new Account("Checking", USD);
			this.hierarchy = builder.build(
				ImmutableList.of(
					spent("400.00").from(checking).at("Whole Foods").build(),
					spent("103.50").from(checking).at("ATM").build(),
					spent("200.00").from(checking).at("Rent").build()
				),
				USD,
				TagImportanceScheme.RANK_BY_AMOUNT,
				HierarchyType.SPENDING,
				ImmutableSet.<Tag>of(),
				5
			);
		}
		
		@Test
		public void itPlacesUntaggedTransactionsInTheirOwnCategory() throws Exception {
			assertThat(hierarchy.getChildren().keySet()).containsOnly(TagHierarchyBuilder.UNTAGGED);
			
			final Node untagged = hierarchy.getChildren().get(TagHierarchyBuilder.UNTAGGED);
			assertThat(untagged.getSum().getCount()).isEqualTo(3);
			assertThat(untagged.getSum().getAmount()).isEqualTo(money("703.50", USD));
		}
	}
	
	public static class Deleted_Transfer_And_Disabled_Txactions extends Context {
		private Account checking;

		@Override
		@Before
		public void setup() throws Exception {
			super.setup();
			
			this.checking = new Account("Checking", USD);
			this.hierarchy = builder.build(
				ImmutableList.of(
					spent("400.00").from(checking).at("Whole Foods").asDeleted().build(),
					spent("103.50").from(checking).at("ATM").asTransfer().build(),
					spent("200.00").from(checking).at("Rent").asDisabled().build()
				),
				USD,
				TagImportanceScheme.RANK_BY_AMOUNT,
				HierarchyType.SPENDING,
				ImmutableSet.<Tag>of(),
				5
			);
		}
		
		@Test
		public void itIgnoresThemAll() throws Exception {
			assertThat(hierarchy.getChildren()).isEmpty();
		}
	}
	
	public static class Currency_Conversion extends Context {
		private Account checking, savings;

		@Override
		@Before
		public void setup() throws Exception {
			super.setup();
			
			this.exchangeRateMap.addExchangeRate(EUR, USD, now(), decimal("1.499900006016309"));
			this.checking = new Account("Checking", USD);
			this.savings = new Account("Savings", EUR);
			this.hierarchy = builder.build(
				ImmutableList.of(
					spent("400.00").from(checking).at("Whole Foods").on("food").build(),
					spent("103.50").from(checking).at("ATM").on("cash").build(),
					spent("200.00").from(savings).at("Rent").on("rent").build()
				),
				USD,
				TagImportanceScheme.RANK_BY_AMOUNT,
				HierarchyType.SPENDING,
				ImmutableSet.<Tag>of(),
				5
			);
		}
		
		@Test
		public void itConvertsOtherCurrenciesIntoTheTargetCurrency() throws Exception {
			final Node rent = hierarchy.getChildren().get(tag("rent"));
			assertThat(rent.getSum().getAmount()).isEqualTo(money("299.98", USD));
			assertThat(rent.getSum().getCount()).isEqualTo(1);
			assertThat(rent.getChildren()).isEmpty();
		}
	}
	
	public static class Ranking_By_Count extends Context {
		private Account checking;
		
		@Override
		@Before
		public void setup() throws Exception {
			super.setup();
			
			this.checking = new Account("Checking", USD);
			
			this.hierarchy = builder.build(
				ImmutableList.of(
					spent("400.00").from(checking).at("Whole Foods").on("food", "groceries").build(),
					spent("500.00").from(checking).at("Credit Card Payment").on("transfer", "creditcard").build(),
					spent("200.00").from(checking).at("Andronico's").on("food", "groceries").build(),
					spent("1500.00").from(checking).at("Rent Check").on("rent").build(),
					spent("100.00").from(checking).at("Dopo").on("food", "restaurant", "italian").build(),
					spent("50.00").from(checking).at("Annual Fee").on("creditcard", "fee").build(),
					spent("300.00").from(checking).at("Zuni").on("food", "restaurant", "fancy").build(),
					spent("5.00").from(checking).at("Peet's").on("coffee", "restaurant").build()
				),
				USD,
				TagImportanceScheme.RANK_BY_COUNT,
				HierarchyType.SPENDING,
				ImmutableSet.of(tag("transfer")),
				5
			);
		}
		
		@Test
		public void itUsesFoodCreditCardRestaurantAndRentAsTheTopTags() throws Exception {
			final ImmutableList<Tag> topTags = ImmutableList.copyOf(hierarchy.getChildren().keySet());
			
			assertThat(topTags).containsOnly(
				tag("food"),
				tag("creditcard"),
				tag("restaurant"),
				tag("rent")
			);
		}
		
		@Test
		public void itUsesFoodAsATopTag() throws Exception {
			final Node food = hierarchy.getChildren().get(tag("food"));
			assertThat(food.getSum().getAmount()).isEqualTo(money("1000.00", USD));
			assertThat(food.getSum().getCount()).isEqualTo(4);
		}
		
		@Test
		public void itNestsTheFancyTagUnderFood() throws Exception {
			final Node food = hierarchy.getChildren().get(tag("food"));
			final Node fancy = food.getChildren().get(tag("fancy"));
			assertThat(fancy.getSum().getAmount()).isEqualTo(money("300.00", USD));
			assertThat(fancy.getSum().getCount()).isEqualTo(1);
			assertThat(fancy.getChildren()).isEmpty();
		}
		
		@Test
		public void itNestsTheGroceriesTagtUnderFood() throws Exception {
			final Node food = hierarchy.getChildren().get(tag("food"));
			final Node groceries = food.getChildren().get(tag("groceries"));
			assertThat(groceries.getSum().getAmount()).isEqualTo(money("600.00", USD));
			assertThat(groceries.getSum().getCount()).isEqualTo(2);
			assertThat(groceries.getChildren()).isEmpty();
		}
		
		@Test
		public void itNestsTheItalianTagtUnderFood() throws Exception {
			final Node food = hierarchy.getChildren().get(tag("food"));
			final Node italian = food.getChildren().get(tag("italian"));
			assertThat(italian.getSum().getAmount()).isEqualTo(money("100.00", USD));
			assertThat(italian.getSum().getCount()).isEqualTo(1);
			assertThat(italian.getChildren()).isEmpty();
		}
		
		@Test
		public void itUsesCreditCardAsATopTag() throws Exception {
			final Node creditcard = hierarchy.getChildren().get(tag("creditcard"));
			assertThat(creditcard.getSum().getAmount()).isEqualTo(money("50.00", USD));
			assertThat(creditcard.getSum().getCount()).isEqualTo(1);
		}
		
		@Test
		public void itNestsTheFeeTagUnderCreditCard() throws Exception {
			final Node creditcard = hierarchy.getChildren().get(tag("creditcard"));
			final Node fee = creditcard.getChildren().get(tag("fee"));
			assertThat(fee.getSum().getAmount()).isEqualTo(money("50.00", USD));
			assertThat(fee.getSum().getCount()).isEqualTo(1);
			assertThat(fee.getChildren()).isEmpty();
		}
		
		@Test
		public void itUsesRestaurantAsATopTag() throws Exception {
			final Node restaurant = hierarchy.getChildren().get(tag("restaurant"));
			assertThat(restaurant.getSum().getAmount()).isEqualTo(money("5.00", USD));
			assertThat(restaurant.getSum().getCount()).isEqualTo(1);
		}
		
		@Test
		public void itNestsTheCoffeeTagUnderCreditCard() throws Exception {
			final Node restaurant = hierarchy.getChildren().get(tag("restaurant"));
			final Node coffee = restaurant.getChildren().get(tag("coffee"));
			assertThat(coffee.getSum().getAmount()).isEqualTo(money("5.00", USD));
			assertThat(coffee.getSum().getCount()).isEqualTo(1);
			assertThat(coffee.getChildren()).isEmpty();
		}
		
		@Test
		public void itUsesRentAsATopTag() throws Exception {
			final Node rent = hierarchy.getChildren().get(tag("rent"));
			assertThat(rent.getSum().getAmount()).isEqualTo(money("1500.00", USD));
			assertThat(rent.getSum().getCount()).isEqualTo(1);
			assertThat(rent.getChildren()).isEmpty();
		}
	}
}

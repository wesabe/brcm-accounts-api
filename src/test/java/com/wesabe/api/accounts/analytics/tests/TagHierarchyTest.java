package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.fest.assertions.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.analytics.SumOfMoney;
import com.wesabe.api.accounts.analytics.TagHierarchy;
import com.wesabe.api.accounts.analytics.TagHierarchy.Node;
import com.wesabe.api.accounts.entities.Tag;

@RunWith(Enclosed.class)
public class TagHierarchyTest {
	public static class A_Root_Node_With_No_Children {
		private Tag food;
		private SumOfMoney sum;
		private Node root;
		
		@Before
		public void setup() throws Exception {
			this.food = new Tag("food");
			this.sum = new SumOfMoney(money("20.12", USD), 12);
			this.root = new Node(food, sum, ImmutableList.<Node>of());
		}
		
		@Test
		public void itHasATag() throws Exception {
			assertThat(root.getTag()).isEqualTo(food);
		}
		
		@Test
		public void itHasASum() throws Exception {
			assertThat(root.getSum()).isEqualTo(sum);
		}
		
		@Test
		public void itHasNoChildren() throws Exception {
			assertThat(root.getChildren()).isEmpty();
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(root.toString()).isEqualTo("<Node tag=food, sum=20.12USD/12, children={}>");
		}
	}
	
	public static class A_Root_Node_With_Two_Children {
		private Tag food, restaurants, groceries;
		private SumOfMoney foodSpending, restaurantSpending, grocerySpending;
		private Node foodNode, restaurantNode, groceryNode;

		@Before
		public void setup() throws Exception {
			this.food = new Tag("food");
			this.foodSpending = new SumOfMoney(money("100.00", USD), 10);
			this.restaurants = new Tag("restaurants");
			this.restaurantSpending = new SumOfMoney(money("60.00", USD), 6);
			this.groceries = new Tag("groceries");
			this.grocerySpending = new SumOfMoney(money("40.00", USD), 4);

			this.restaurantNode = new Node(restaurants, restaurantSpending, ImmutableList.<Node>of());
			this.groceryNode = new Node(groceries, grocerySpending, ImmutableList.<Node>of());
			this.foodNode = new Node(food, foodSpending, ImmutableList.of(restaurantNode, groceryNode));
		}

		@Test
		public void itHasAChildNodeForRestaurants() throws Exception {
			assertThat(foodNode.getChildren().get(new Tag("restaurants"))).isEqualTo(restaurantNode);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(foodNode.toString()).isEqualTo(
				"<Node tag=food, sum=100.00USD/10, children={\n" +
				"	restaurants=<Node tag=restaurants, sum=60.00USD/6, children={}>\n" +
				"	groceries=<Node tag=groceries, sum=40.00USD/4, children={}>\n" +
				"}>"
			);
		}
	}
	
	public static class A_Tag_Hierarchy {
		private TagHierarchy hierarchy;
		private Tag food;
		private SumOfMoney foodSpending, total;
		private Node foodNode;
		
		@Before
		public void setup() throws Exception {
			this.food = new Tag("food");
			this.foodSpending = new SumOfMoney(money("100.00", USD), 10);
			this.total = new SumOfMoney(money("100.00", USD), 10);
			this.foodNode = new Node(food, foodSpending, ImmutableList.<Node>of());
			this.hierarchy = new TagHierarchy(ImmutableList.of(foodNode), total);
		}
		
		@Test
		public void itHasASetOfChildren() throws Exception {
			assertThat(hierarchy.getChildren().get(food)).isEqualTo(foodNode);
		}
		
		@Test
		public void itHasATotalSum() throws Exception {
			assertThat(hierarchy.getSum()).isEqualTo(total);
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(hierarchy.toString()).isEqualTo("<TagHierarchy\n" +
					"	food=<Node tag=food, sum=100.00USD/10, children={}>\n" +
					">");
		}
	}
}

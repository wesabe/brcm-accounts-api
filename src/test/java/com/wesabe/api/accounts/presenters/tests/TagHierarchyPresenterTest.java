package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.TagHelper.*;
import static org.fest.assertions.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.analytics.SumOfMoney;
import com.wesabe.api.accounts.analytics.TagHierarchy;
import com.wesabe.api.accounts.analytics.TagHierarchy.Node;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.accounts.presenters.SumOfMoneyPresenter;
import com.wesabe.api.accounts.presenters.TagHierarchyPresenter;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonMember;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class TagHierarchyPresenterTest {
	public static class The_Representation_Of_A_Tag_Hierarchy {
		private MoneyPresenter moneyPresenter;
		private SumOfMoneyPresenter sumOfMoneyPresenter;
		private TagHierarchyPresenter presenter;
		private Node food, entertainment, groceries, restaurants;
		private TagHierarchy hierarchy;
		
		@Before
		public void setup() throws Exception {
			this.moneyPresenter = new MoneyPresenter();
			this.sumOfMoneyPresenter = new SumOfMoneyPresenter(moneyPresenter);
			this.presenter = new TagHierarchyPresenter(sumOfMoneyPresenter);
			
			this.restaurants = new Node(tag("restaurants"), new SumOfMoney(money("20.00", USD), 1), ImmutableList.<Node>of());
			this.groceries = new Node(tag("groceries"), new SumOfMoney(money("30.00", USD), 1), ImmutableList.<Node>of());
			this.food = new Node(tag("food"), new SumOfMoney(money("50.00", USD), 2), ImmutableList.of(restaurants, groceries));
			this.entertainment = new Node(tag("entertainment"), new SumOfMoney(money("50.00", USD), 1), ImmutableList.<Node>of());
			
			this.hierarchy = new TagHierarchy(ImmutableList.of(food, entertainment), new SumOfMoney(money("100.00", USD), 3));
		}
		
		@Test
		public void itItIsNamedTagHierarchy() throws Exception {
			final XmlsonObject representation = presenter.present(hierarchy, Locale.CANADA_FRENCH);
			
			assertThat(representation.getName(), is("tag-hierarchy"));
		}
		
		@Test
		public void itHasATopLevelSum() throws Exception {
			final XmlsonObject representation = presenter.present(hierarchy, Locale.CANADA_FRENCH);
			
			final XmlsonObject sum = (XmlsonObject) representation.get("sum");
			
			assertThat(sum.getString("display")).isEqualTo("100,00 $ US");
			assertThat(sum.getString("value")).isEqualTo("100.00");
			assertThat(sum.getInteger("count")).isEqualTo(3);
		}
		
		@Test
		public void itHasTwoTopNodes() throws Exception {
			final XmlsonObject representation = presenter.present(hierarchy, Locale.CANADA_FRENCH);
			
			final XmlsonArray nodes = (XmlsonArray) representation.get("nodes");
			
			assertThat(nodes.getMembers()).hasSize(2);
		}
		
		@Test
		public void itHasATopNodeForFoodWithChildren() throws Exception {
			final XmlsonObject representation = presenter.present(hierarchy, Locale.CANADA_FRENCH);
			
			final XmlsonArray nodes = (XmlsonArray) representation.get("nodes");
			int checkedNodes = 0;
			
			for (XmlsonMember member : nodes.getMembers()) {
				final XmlsonObject node = (XmlsonObject) member;
				if (((XmlsonObject) node.get("tag")).getString("name").equals("food")) {
					final XmlsonObject sum = (XmlsonObject) node.get("sum");
					
					assertThat(sum.getString("display")).isEqualTo("50,00 $ US");
					assertThat(sum.getString("value")).isEqualTo("50.00");
					assertThat(sum.getInteger("count")).isEqualTo(2);
					
					checkedNodes++;
					
					for (XmlsonMember innerMember : ((XmlsonArray) node.get("nodes")).getMembers()) {
						final XmlsonObject innerNode = (XmlsonObject) innerMember;
						if (((XmlsonObject) innerNode.get("tag")).getString("name").equals("groceries")) {
							final XmlsonObject innerSum = (XmlsonObject) innerNode.get("sum");
							
							assertThat(innerSum.getString("display")).isEqualTo("30,00 $ US");
							assertThat(innerSum.getString("value")).isEqualTo("30.00");
							assertThat(innerSum.getInteger("count")).isEqualTo(1);
							
							checkedNodes++;
						}
					}
					
					for (XmlsonMember innerMember : ((XmlsonArray) node.get("nodes")).getMembers()) {
						final XmlsonObject innerNode = (XmlsonObject) innerMember;
						if (((XmlsonObject) innerNode.get("tag")).getString("name").equals("restaurants")) {
							final XmlsonObject innerSum = (XmlsonObject) innerNode.get("sum");
							
							assertThat(innerSum.getString("display")).isEqualTo("20,00 $ US");
							assertThat(innerSum.getString("value")).isEqualTo("20.00");
							assertThat(innerSum.getInteger("count")).isEqualTo(1);
							
							checkedNodes++;
						}
					}
					
					return;
				}
			}
			
			if (checkedNodes != 3) {
				fail("food node not found");
			}
		}
	}
}

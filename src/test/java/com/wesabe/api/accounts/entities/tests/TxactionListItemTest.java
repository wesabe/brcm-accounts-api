package com.wesabe.api.accounts.entities.tests;

import static org.junit.Assert.*;

import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionListItem;
import com.wesabe.api.util.money.Money;

@RunWith(Enclosed.class)
public class TxactionListItemTest {
	public static class A_List_Item {
		private Txaction txaction = new Txaction(new Account("Checking", USD), decimal("-5.00"), new DateTime());
		private Money balance = money("50.00", USD);
		private TxactionListItem item = new TxactionListItem(txaction, balance);
		
		@Test
		public void itEqualsItself() {
			assertEquals(item, item);
		}
		
		@Test
		public void itEqualsAnotherInstanceWithTheSameTxactionAndBalance() {
			assertEquals(item, new TxactionListItem(txaction, balance));
		}
		
		@Test
		public void itHasTheSameHashCodeAsAnotherInstanceWithTheSameTxactionAndBalance() {
			assertEquals(item.hashCode(), new TxactionListItem(txaction, balance).hashCode());
		}
		
		@Test
		public void itHasAStringValueShowingTheTxactionAndBalance() {
			assertEquals(String.format("($50.00, %s)", txaction), item.toString());
		}
	}
}

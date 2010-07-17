package com.wesabe.api.accounts.presenters.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.entities.TxactionListItem;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.accounts.presenters.TxactionListPresenter;
import com.wesabe.api.accounts.presenters.TxactionPresenter;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class TxactionListPresenterTest {
	public static class The_Representation_Of_A_Txaction_List {
		private TxactionListItem balanced, unbalanced;
		private List<TxactionListItem> txactionItems;
		private TxactionList txactions;
		private MoneyPresenter moneyPresenter;
		private TxactionPresenter txactionPresenter;
		private TxactionListPresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.balanced = mock(TxactionListItem.class);
			this.unbalanced = mock(TxactionListItem.class);
			
			this.txactionItems = ImmutableList.of(balanced, unbalanced);
			
			this.txactions = mock(TxactionList.class);
			when(txactions.getTotalCount()).thenReturn(20);
			when(txactions.iterator()).thenReturn(txactionItems.iterator());
			
			this.moneyPresenter = mock(MoneyPresenter.class);
			
			this.txactionPresenter = mock(TxactionPresenter.class);
			
			this.presenter = new TxactionListPresenter(moneyPresenter, txactionPresenter);
		}
		
		@Test
		public void itIsNamedTransactionList() throws Exception {
			final XmlsonObject representation = presenter.present(txactions, Locale.CHINA);
			
			assertThat(representation.getName(), is("transaction-list"));
		}
		
		@Test
		public void itHasTheTotalNumberOfTxactions() throws Exception {
			final XmlsonObject representation = presenter.present(txactions, Locale.CHINA);
			
			final XmlsonObject count = (XmlsonObject) representation.get("count");
			assertThat(count.getInteger("total"), is(20));
		}
	}
}

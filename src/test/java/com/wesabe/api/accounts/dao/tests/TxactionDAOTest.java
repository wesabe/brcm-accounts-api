package com.wesabe.api.accounts.dao.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;
import com.wesabe.api.accounts.dao.TxactionDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Txaction;

@RunWith(Enclosed.class)
public class TxactionDAOTest {
	private static abstract class Context {

		protected Account account;
		protected Txaction txaction;
		protected Query query;
		protected Session session;
		protected TxactionDAO dao;

		public void setup() throws Exception {
			this.account = mock(Account.class);
			
			this.txaction = mock(Txaction.class);
			
			this.query = mock(Query.class);
			when(query.setParameter(Mockito.anyString(), Mockito.anyObject())).thenReturn(query);
			when(query.setParameterList(Mockito.anyString(), Mockito.anyCollection())).thenReturn(query);
			when(query.list()).thenReturn(ImmutableList.of(txaction));
			
			this.session = mock(Session.class);
			when(session.getNamedQuery(Mockito.anyString())).thenReturn(query);
			
			this.dao = new TxactionDAO(new Provider<Session>() {
				@Override
				public Session get() {
					return session;
				}
			});
		}
		
	}
	
	public static class Selecting_All_Txactions_In_A_Set_Of_Accounts extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itReturnsAnEmptyListIfThereAreNoAccounts() throws Exception {
			assertThat(dao.findTxactions(ImmutableList.<Account>of()).isEmpty(), is(true));
		}
		
		@Test
		public void itGetsANamedQuery() throws Exception {
			dao.findTxactions(ImmutableList.of(account));
			
			verify(session).getNamedQuery("com.wesabe.api.accounts.Txaction.findInAccounts");
		}
		
		@Test
		public void itScopesTheQueryToTheSetOfAccounts() throws Exception {
			dao.findTxactions(ImmutableList.of(account));
			
			verify(query).setParameterList("accounts", ImmutableList.of(account));
		}
		
		@Test
		public void itReturnsAListOfTransactionsInTheAccounts() throws Exception {
			assertThat(dao.findTxactions(ImmutableList.of(account)), is((List<Txaction>) ImmutableList.of(txaction)));
		}
	}
	
	public static class Selecting_All_Txactions_In_A_Set_Of_Accounts_With_A_Start_Date extends Context {
		private DateTime startDate;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.startDate = new DateTime();
		}
		
		@Test
		public void itReturnsAnEmptyListIfThereAreNoAccounts() throws Exception {
			assertThat(dao.findTxactionsAfterDate(ImmutableList.<Account>of(), startDate).isEmpty(), is(true));
		}
		
		@Test
		public void itGetsANamedQuery() throws Exception {
			dao.findTxactionsAfterDate(ImmutableList.of(account), startDate);
			
			verify(session).getNamedQuery("com.wesabe.api.accounts.Txaction.findAfterDate");
		}
		
		@Test
		public void itScopesTheQueryToTheSetOfAccounts() throws Exception {
			dao.findTxactionsAfterDate(ImmutableList.of(account), startDate);
			
			verify(query).setParameterList("accounts", ImmutableList.of(account));
		}
		
		@Test
		public void itScopesTheQueryToTheStartDate() throws Exception {
			dao.findTxactionsAfterDate(ImmutableList.of(account), startDate);
			
			verify(query).setParameter("startDate", startDate);
		}
		
		@Test
		public void itReturnsAListOfTransactionsInTheAccounts() throws Exception {
			assertThat(dao.findTxactionsAfterDate(ImmutableList.of(account), startDate), is((List<Txaction>) ImmutableList.of(txaction)));
		}
	}
	
	public static class Selecting_All_Txactions_In_A_Set_Of_Accounts_With_An_End_Date extends Context {
		private DateTime endDate;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.endDate = new DateTime();
		}
		
		@Test
		public void itReturnsAnEmptyListIfThereAreNoAccounts() throws Exception {
			assertThat(dao.findTxactionsBeforeDate(ImmutableList.<Account>of(), endDate).isEmpty(), is(true));
		}
		
		@Test
		public void itGetsANamedQuery() throws Exception {
			dao.findTxactionsBeforeDate(ImmutableList.of(account), endDate);
			
			verify(session).getNamedQuery("com.wesabe.api.accounts.Txaction.findBeforeDate");
		}
		
		@Test
		public void itScopesTheQueryToTheSetOfAccounts() throws Exception {
			dao.findTxactionsBeforeDate(ImmutableList.of(account), endDate);
			
			verify(query).setParameterList("accounts", ImmutableList.of(account));
		}
		
		@Test
		public void itScopesTheQueryToTheEndDate() throws Exception {
			dao.findTxactionsBeforeDate(ImmutableList.of(account), endDate);
			
			verify(query).setParameter("endDate", endDate);
		}
		
		@Test
		public void itReturnsAListOfTransactionsInTheAccounts() throws Exception {
			assertThat(dao.findTxactionsBeforeDate(ImmutableList.of(account), endDate), is((List<Txaction>) ImmutableList.of(txaction)));
		}
	}
	
	public static class Selecting_All_Txactions_In_A_Set_Of_Accounts_Within_An_Interval extends Context {
		private Interval interval;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.interval = new Interval(new DateTime().minusDays(1), new DateTime());
		}
		
		@Test
		public void itReturnsAnEmptyListIfThereAreNoAccounts() throws Exception {
			assertThat(dao.findTxactionsInDateRange(ImmutableList.<Account>of(), interval).isEmpty(), is(true));
		}
		
		@Test
		public void itGetsANamedQuery() throws Exception {
			dao.findTxactionsInDateRange(ImmutableList.of(account), interval);
			
			verify(session).getNamedQuery("com.wesabe.api.accounts.Txaction.findInDateRange");
		}
		
		@Test
		public void itScopesTheQueryToTheSetOfAccounts() throws Exception {
			dao.findTxactionsInDateRange(ImmutableList.of(account), interval);
			
			verify(query).setParameterList("accounts", ImmutableList.of(account));
		}
		
		@Test
		public void itScopesTheQueryToTheStartDate() throws Exception {
			dao.findTxactionsInDateRange(ImmutableList.of(account), interval);
			
			verify(query).setParameter("startDate", interval.getStart());
		}
		
		@Test
		public void itScopesTheQueryToTheEndDate() throws Exception {
			dao.findTxactionsInDateRange(ImmutableList.of(account), interval);
			
			verify(query).setParameter("endDate", interval.getEnd());
		}
		
		@Test
		public void itReturnsAListOfTransactionsInTheAccounts() throws Exception {
			assertThat(dao.findTxactionsInDateRange(ImmutableList.of(account), interval), is((List<Txaction>) ImmutableList.of(txaction)));
		}
	}
}

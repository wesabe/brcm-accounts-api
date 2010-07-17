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
import com.wesabe.api.accounts.dao.InvestmentTxactionDAO;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.entities.InvestmentTxaction;

@RunWith(Enclosed.class)
public class InvestmentTxactionDAOTest {
	private static abstract class Context {

		protected InvestmentAccount account;
		protected InvestmentTxaction txaction;
		protected Query query;
		protected Session session;
		protected InvestmentTxactionDAO dao;

		public void setup() throws Exception {
			this.account = mock(InvestmentAccount.class);
			
			this.txaction = mock(InvestmentTxaction.class);
			
			this.query = mock(Query.class);
			when(query.setParameter(Mockito.anyString(), Mockito.anyObject())).thenReturn(query);
			when(query.setParameterList(Mockito.anyString(), Mockito.anyCollection())).thenReturn(query);
			when(query.list()).thenReturn(ImmutableList.of(txaction));
			
			this.session = mock(Session.class);
			when(session.getNamedQuery(Mockito.anyString())).thenReturn(query);
			
			this.dao = new InvestmentTxactionDAO(new Provider<Session>() {
				@Override
				public Session get() {
					return session;
				}
			});
		}
		
	}
	
	public static class Selecting_All_Txactions_In_A_Set_Of_InvestmentAccounts extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itReturnsAnEmptyListIfThereAreNoInvestmentAccounts() throws Exception {
			assertThat(dao.findInvestmentTxactions(ImmutableList.<InvestmentAccount>of()).isEmpty(), is(true));
		}
		
		@Test
		public void itGetsANamedQuery() throws Exception {
			dao.findInvestmentTxactions(ImmutableList.of(account));
			
			verify(session).getNamedQuery("com.wesabe.api.accounts.InvestmentTxaction.findInAccounts");
		}
		
		@Test
		public void itScopesTheQueryToTheSetOfInvestmentAccounts() throws Exception {
			dao.findInvestmentTxactions(ImmutableList.of(account));
			
			verify(query).setParameterList("accounts", ImmutableList.of(account));
		}
		
		@Test
		public void itReturnsAListOfTransactionsInTheInvestmentAccounts() throws Exception {
			assertThat(dao.findInvestmentTxactions(ImmutableList.of(account)), is((List<InvestmentTxaction>) ImmutableList.of(txaction)));
		}
	}
	
	public static class Selecting_All_Txactions_In_A_Set_Of_InvestmentAccounts_With_A_Start_Date extends Context {
		private DateTime startDate;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.startDate = new DateTime();
		}
		
		@Test
		public void itReturnsAnEmptyListIfThereAreNoInvestmentAccounts() throws Exception {
			assertThat(dao.findInvestmentTxactionsAfterDate(ImmutableList.<InvestmentAccount>of(), startDate).isEmpty(), is(true));
		}
		
		@Test
		public void itGetsANamedQuery() throws Exception {
			dao.findInvestmentTxactionsAfterDate(ImmutableList.of(account), startDate);
			
			verify(session).getNamedQuery("com.wesabe.api.accounts.InvestmentTxaction.findAfterDate");
		}
		
		@Test
		public void itScopesTheQueryToTheSetOfInvestmentAccounts() throws Exception {
			dao.findInvestmentTxactionsAfterDate(ImmutableList.of(account), startDate);
			
			verify(query).setParameterList("accounts", ImmutableList.of(account));
		}
		
		@Test
		public void itScopesTheQueryToTheStartDate() throws Exception {
			dao.findInvestmentTxactionsAfterDate(ImmutableList.of(account), startDate);
			
			verify(query).setParameter("startDate", startDate);
		}
		
		@Test
		public void itReturnsAListOfTransactionsInTheInvestmentAccounts() throws Exception {
			assertThat(dao.findInvestmentTxactionsAfterDate(ImmutableList.of(account), startDate), is((List<InvestmentTxaction>) ImmutableList.of(txaction)));
		}
	}
	
	public static class Selecting_All_Txactions_In_A_Set_Of_InvestmentAccounts_With_An_End_Date extends Context {
		private DateTime endDate;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.endDate = new DateTime();
		}
		
		@Test
		public void itReturnsAnEmptyListIfThereAreNoInvestmentAccounts() throws Exception {
			assertThat(dao.findInvestmentTxactionsBeforeDate(ImmutableList.<InvestmentAccount>of(), endDate).isEmpty(), is(true));
		}
		
		@Test
		public void itGetsANamedQuery() throws Exception {
			dao.findInvestmentTxactionsBeforeDate(ImmutableList.of(account), endDate);
			
			verify(session).getNamedQuery("com.wesabe.api.accounts.InvestmentTxaction.findBeforeDate");
		}
		
		@Test
		public void itScopesTheQueryToTheSetOfInvestmentAccounts() throws Exception {
			dao.findInvestmentTxactionsBeforeDate(ImmutableList.of(account), endDate);
			
			verify(query).setParameterList("accounts", ImmutableList.of(account));
		}
		
		@Test
		public void itScopesTheQueryToTheEndDate() throws Exception {
			dao.findInvestmentTxactionsBeforeDate(ImmutableList.of(account), endDate);
			
			verify(query).setParameter("endDate", endDate);
		}
		
		@Test
		public void itReturnsAListOfTransactionsInTheInvestmentAccounts() throws Exception {
			assertThat(dao.findInvestmentTxactionsBeforeDate(ImmutableList.of(account), endDate), is((List<InvestmentTxaction>) ImmutableList.of(txaction)));
		}
	}
	
	public static class Selecting_All_Txactions_In_A_Set_Of_InvestmentAccounts_Within_An_Interval extends Context {
		private Interval interval;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.interval = new Interval(new DateTime().minusDays(1), new DateTime());
		}
		
		@Test
		public void itReturnsAnEmptyListIfThereAreNoInvestmentAccounts() throws Exception {
			assertThat(dao.findInvestmentTxactionsInDateRange(ImmutableList.<InvestmentAccount>of(), interval).isEmpty(), is(true));
		}
		
		@Test
		public void itGetsANamedQuery() throws Exception {
			dao.findInvestmentTxactionsInDateRange(ImmutableList.of(account), interval);
			
			verify(session).getNamedQuery("com.wesabe.api.accounts.InvestmentTxaction.findInDateRange");
		}
		
		@Test
		public void itScopesTheQueryToTheSetOfInvestmentAccounts() throws Exception {
			dao.findInvestmentTxactionsInDateRange(ImmutableList.of(account), interval);
			
			verify(query).setParameterList("accounts", ImmutableList.of(account));
		}
		
		@Test
		public void itScopesTheQueryToTheStartDate() throws Exception {
			dao.findInvestmentTxactionsInDateRange(ImmutableList.of(account), interval);
			
			verify(query).setParameter("startDate", interval.getStart());
		}
		
		@Test
		public void itScopesTheQueryToTheEndDate() throws Exception {
			dao.findInvestmentTxactionsInDateRange(ImmutableList.of(account), interval);
			
			verify(query).setParameter("endDate", interval.getEnd());
		}
		
		@Test
		public void itReturnsAListOfTransactionsInTheInvestmentAccounts() throws Exception {
			assertThat(dao.findInvestmentTxactionsInDateRange(ImmutableList.of(account), interval), is((List<InvestmentTxaction>) ImmutableList.of(txaction)));
		}
	}
}

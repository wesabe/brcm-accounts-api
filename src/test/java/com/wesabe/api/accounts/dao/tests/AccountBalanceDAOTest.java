package com.wesabe.api.accounts.dao.tests;

import static org.mockito.Mockito.*;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.inject.Provider;
import com.wesabe.api.accounts.dao.AccountBalanceDAO;
import com.wesabe.api.accounts.entities.AccountBalance;

@RunWith(Enclosed.class)
public class AccountBalanceDAOTest {
	public static class Creating_An_AccountBalance {
		private Session hibernateSession;
		private AccountBalance accountBalance;
		private AccountBalanceDAO accountBalanceDAO;
		private DateTime currentDateTime;
		private Transaction hibernateTransaction;

		@Before
		public void setup() {
			this.accountBalance = mock(AccountBalance.class);
			this.hibernateSession = mock(Session.class);
			this.currentDateTime = new DateTime();
			this.accountBalanceDAO = new AccountBalanceDAO(new Provider<Session>() {
				
				@Override
				public Session get() {
					return hibernateSession;
				}
				
			}, new Provider<DateTime>() {
				
				@Override
				public DateTime get() {
					return currentDateTime;
				}
				
			});
			
			this.hibernateTransaction = mock(Transaction.class);
			when(hibernateSession.beginTransaction()).thenReturn(hibernateTransaction);
		}
		
		@Test
		public void itSetsCreatedAt() {
			accountBalanceDAO.create(accountBalance);
			verify(accountBalance).setCreatedAt(currentDateTime);
		}

		@Test
		public void itSetsUpdatedAt() {
			accountBalanceDAO.create(accountBalance);
			verify(accountBalance).setUpdatedAt(currentDateTime);
		}
	}
}

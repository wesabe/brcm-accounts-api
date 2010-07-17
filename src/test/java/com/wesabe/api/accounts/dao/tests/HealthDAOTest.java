package com.wesabe.api.accounts.dao.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.inject.Provider;
import com.wesabe.api.accounts.dao.HealthDAO;

@RunWith(Enclosed.class)
public class HealthDAOTest {
	private static abstract class Context {
		protected Logger logger;
		protected Session session;
		protected SQLQuery query;
		protected HealthDAO dao;

		@Before
		public void setup() throws Exception {
			this.logger = mock(Logger.class);
			
			this.query = mock(SQLQuery.class);
			when(query.setString(Mockito.anyString(), Mockito.anyString())).thenReturn(query);
			when(query.setComment(Mockito.anyString())).thenReturn(query);
			
			this.session = mock(Session.class);
			when(session.createSQLQuery(Mockito.anyString())).thenReturn(query);
			
			this.dao = new HealthDAO(new Provider<Session>() {
				@Override
				public Session get() {
					return session;
				}
			}, logger);
		}
		
	}
	
	public static class Checking_Health_When_Healthy extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			when(query.uniqueResult()).thenReturn("health-check");
		}
		
		@Test
		public void itCreatesANewSQLQuery() throws Exception {
			dao.isHealthy();
			
			verify(session).createSQLQuery("SELECT :value");
		}
		
		@Test
		public void itSetsATestValue() throws Exception {
			dao.isHealthy();
			
			verify(query).setString("value", "health-check");
		}
		
		@Test
		public void itSetsACommentOnTheQuery() throws Exception {
			dao.isHealthy();
			
			verify(query).setComment("health check");
		}
		
		@Test
		public void itGetsAUniqueResultsFromTheQuery() throws Exception {
			dao.isHealthy();
			
			verify(query).uniqueResult();
		}
		
		@Test
		public void itReturnsTrue() throws Exception {
			assertThat(dao.isHealthy(), is(true));
		}
	}
	
	public static class Checking_Health_When_Unhealthy extends Context {
		private HibernateException exception;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.exception = new HibernateException("nooo");
			when(query.uniqueResult()).thenThrow(exception);
		}
		
		@Test
		public void itReturnsFalse() throws Exception {
			assertThat(dao.isHealthy(), is(false));
		}
		
		@Test
		public void itLogsTheError() throws Exception {
			dao.isHealthy();
			
			verify(logger).log(Level.SEVERE, "Error connecting to the database", exception);
		}
	}
	
	public static class Checking_Health_When_Really_Weird extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			new HibernateException("nooo");
			when(query.uniqueResult()).thenReturn("fifty");
		}
		
		@Test
		public void itReturnsFalse() throws Exception {
			assertThat(dao.isHealthy(), is(false));
		}
		
		@Test
		public void itLogsTheError() throws Exception {
			dao.isHealthy();
			
			verify(logger).severe("The database just returned fifty instead of health-check. That's weird");
		}
	}
}

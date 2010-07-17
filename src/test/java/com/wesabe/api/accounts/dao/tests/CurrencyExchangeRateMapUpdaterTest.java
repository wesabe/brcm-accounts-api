package com.wesabe.api.accounts.dao.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.dao.CurrencyExchangeRateMapUpdater;
import com.wesabe.api.accounts.entities.CurrencyExchangeRate;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class CurrencyExchangeRateMapUpdaterTest {
	public static class Initializing {
		private ScheduledExecutorService executorService;
		
		@Test
		public void itSchedulesItselfToRunEvery12HoursWithA12HourDelay() throws Exception {
			this.executorService = mock(ScheduledExecutorService.class);
			
			final CurrencyExchangeRateMapUpdater updater = new CurrencyExchangeRateMapUpdater(null, null, executorService, null);
			
			verify(executorService).scheduleAtFixedRate(updater, 12, 12, TimeUnit.HOURS);
		}
	}
	
	public static class Updating_The_Currency_Exchange_Rate_Map {
		private Query query;
		private Session session;
		private SessionFactory sessionFactory;
		private CurrencyExchangeRateMap exchangeRateMap;
		private ScheduledExecutorService executorService;
		private Logger logger;
		private CurrencyExchangeRate rate;
		private CurrencyExchangeRateMapUpdater updater;
		
		@Before
		public void setup() throws Exception {
			this.rate = new CurrencyExchangeRate(EUR, 1.0, apr1st);
			
			this.logger = mock(Logger.class);
			
			this.query = mock(Query.class);
			when(query.list()).thenReturn(ImmutableList.of(rate));
			
			this.session = mock(Session.class);
			when(session.getNamedQuery(anyString())).thenReturn(query);
			
			this.sessionFactory = mock(SessionFactory.class);
			when(sessionFactory.openSession()).thenReturn(session);
			
			this.exchangeRateMap = mock(CurrencyExchangeRateMap.class);
			
			this.executorService = mock(ScheduledExecutorService.class);
			
			this.updater = new CurrencyExchangeRateMapUpdater(sessionFactory, exchangeRateMap, executorService, logger);
			
			DateTimeUtils.setCurrentMillisFixed(new DateTime(2009, 5, 20, 23, 43, 12, 0).getMillis());
		}
		
		@After
		public void teardown() throws Exception {
			DateTimeUtils.setCurrentMillisSystem();
		}
		
		@Test
		public void itOpensANewSession() throws Exception {
			updater.run();
			
			verify(sessionFactory).openSession();
		}
		
		@Test
		public void itGetsTheExchangeRatesNamedQueryAndExecutesIt() throws Exception {
			updater.run();
			
			InOrder inOrder = inOrder(session, query);
			inOrder.verify(session).getNamedQuery("com.wesabe.api.accounts.entities.CurrencyExchangeRate.findRecent");
			inOrder.verify(query).setParameter("date", new DateTime().minusHours(13));
			inOrder.verify(query).list();
		}
		
		@Test
		public void itAddsTheRatesToTheMap() throws Exception {
			updater.run();
			
			verify(exchangeRateMap).addExchangeRate(USD, EUR, apr1st, new BigDecimal("1"));
		}
		
		@Test
		public void itClosesTheSession() throws Exception {
			updater.run();
			
			verify(session).close();
		}
	}
}

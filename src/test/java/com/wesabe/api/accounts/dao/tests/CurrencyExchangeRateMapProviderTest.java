package com.wesabe.api.accounts.dao.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wesabe.api.accounts.dao.CurrencyExchangeRateMapProvider;
import com.wesabe.api.accounts.entities.CurrencyExchangeRate;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class CurrencyExchangeRateMapProviderTest {
	public static class Providing_A_New_Map {
		private Query query;
		private Session session;
		private SessionFactory factory;
		private CurrencyExchangeRateMapProvider provider;
		private Level originalLevel;
		private Logger logger;
		private CurrencyExchangeRate rate;
		
		@Before
		public void setup() throws Exception {
			this.rate = new CurrencyExchangeRate(EUR, 0.7656, apr1st);
			
			this.logger = Logger.getLogger(CurrencyExchangeRateMapProvider.class.getCanonicalName());
			this.originalLevel = logger.getLevel();
			logger.setLevel(Level.OFF);
			
			this.query = mock(Query.class);
			when(query.list()).thenReturn(ImmutableList.of(rate));
			
			this.session = mock(Session.class);
			when(session.getNamedQuery(anyString())).thenReturn(query);
			
			this.factory = mock(SessionFactory.class);
			when(factory.openSession()).thenReturn(session);
			
			final Injector injector = Guice.createInjector(new AbstractModule() {
				@Override
				protected void configure() {
					bind(SessionFactory.class).toInstance(factory);
				}
			});
			
			this.provider = injector.getInstance(CurrencyExchangeRateMapProvider.class);
		}
		
		@After
		public void teardown() throws Exception {
			logger.setLevel(originalLevel);
		}
		
		@Test
		public void itOpensANewSession() throws Exception {
			provider.get();
			
			verify(factory).openSession();
		}
		
		@Test
		public void itGetsTheExchangeRatesNamedQueryAndExecutesIt() throws Exception {
			provider.get();
			
			InOrder inOrder = inOrder(session, query);
			inOrder.verify(session).getNamedQuery("com.wesabe.api.accounts.entities.CurrencyExchangeRate.findAll");
			inOrder.verify(query).list();
		}
		
		@Test
		public void itClosesTheSession() throws Exception {
			provider.get();
			
			verify(session).close();
		}
		
		@Test
		public void itReturnsAMapWithAllTheRates() throws Exception {
			final CurrencyExchangeRateMap map = provider.get();
			
			assertEquals(decimal("0.7656001"), map.getExchangeRate(USD, EUR, apr1st));
			assertEquals(decimal("1.306165"), map.getExchangeRate(EUR, USD, apr1st));
		}
	}

}

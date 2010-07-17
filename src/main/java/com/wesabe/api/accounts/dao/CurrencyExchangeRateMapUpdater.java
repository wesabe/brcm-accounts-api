package com.wesabe.api.accounts.dao;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.CurrencyExchangeRate;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

/**
 * A class which updates a {@link CurrencyExchangeRateMap} with new rates from
 * the database every 12 hours.
 * 
 * @author coda
 */
public class CurrencyExchangeRateMapUpdater implements Runnable {
	private static final Currency USD = Currency.getInstance("USD");
	private static final int UPDATE_PERIOD = 12; // hours
	private final SessionFactory sessionFactory;
	private final CurrencyExchangeRateMap exchangeRateMap;
	private final Logger logger;

	@Inject
	public CurrencyExchangeRateMapUpdater(SessionFactory sessionFactory, CurrencyExchangeRateMap exchangeRateMap,
			ScheduledExecutorService executorService, Logger logger) {
		this.sessionFactory = sessionFactory;
		this.exchangeRateMap = exchangeRateMap;
		this.logger = logger;
		executorService.scheduleAtFixedRate(this, UPDATE_PERIOD, UPDATE_PERIOD, TimeUnit.HOURS);
	}

	@Override
	public void run() {
		final Session session = sessionFactory.openSession();
		try {
			logger.info("updating currency exchange rates");
			final Query query = session.getNamedQuery("com.wesabe.api.accounts.entities.CurrencyExchangeRate.findRecent");
			query.setParameter("date", new DateTime().minusHours(UPDATE_PERIOD + 1));
			@SuppressWarnings("unchecked")
			final List<CurrencyExchangeRate> rates = query.list();
			
			logger.info("loading currency exchange rates");
			for (CurrencyExchangeRate rate : rates) {
				exchangeRateMap.addExchangeRate(USD, rate.getCurrency(), rate.getDate(), new BigDecimal(rate.getRate()));
			}

			logger.info(rates.size() + " new exchange rates loaded");
		} finally {
			session.close();
		}
	}
}

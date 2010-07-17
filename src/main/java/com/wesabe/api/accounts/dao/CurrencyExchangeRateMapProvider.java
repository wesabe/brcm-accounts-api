package com.wesabe.api.accounts.dao;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.api.accounts.entities.CurrencyExchangeRate;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.DollarPivotCurrencyExchangeRateMap;

/**
 * A {@link Provider} of {@link CurrencyExchangeRateMap}s.
 * 
 * @author coda
 *
 */
@SuppressWarnings("deprecation")
public class CurrencyExchangeRateMapProvider implements Provider<CurrencyExchangeRateMap> {
	private static final Currency USD = Currency.getInstance("USD");
	private final SessionFactory sessionFactory;
	private final Logger logger;
	
	@Inject
	public CurrencyExchangeRateMapProvider(SessionFactory sessionFactory, Logger logger) {
		this.sessionFactory = sessionFactory;
		this.logger = logger;
	}
	
	@Override
	public CurrencyExchangeRateMap get() {
		final Session session = sessionFactory.openSession();
		try {
			logger.info("reading currency exchange rates");
			@SuppressWarnings("unchecked")
			final List<CurrencyExchangeRate> rates = session.getNamedQuery("com.wesabe.api.accounts.entities.CurrencyExchangeRate.findAll").list();
			final DollarPivotCurrencyExchangeRateMap exchangeRateMap = new DollarPivotCurrencyExchangeRateMap();

			logger.info("loading currency exchange rates");
			for (CurrencyExchangeRate rate : rates) {
				exchangeRateMap.addExchangeRate(USD, rate.getCurrency(), rate.getDate(), new BigDecimal(rate.getRate()));
			}

			logger.info(rates.size() + " exchange rates loaded");

			return exchangeRateMap;
		} finally {
			session.close();
		}
	}
}

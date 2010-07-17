package com.wesabe.api.accounts.modules;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.AbstractModule;
import com.wesabe.api.accounts.dao.CurrencyExchangeRateMapProvider;
import com.wesabe.api.accounts.dao.CurrencyExchangeRateMapUpdater;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

public class CurrencyExchangeRateMapModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CurrencyExchangeRateMap.class)
			.toProvider(CurrencyExchangeRateMapProvider.class)
			.asEagerSingleton();
		bind(ScheduledExecutorService.class)
			.toInstance(Executors.newSingleThreadScheduledExecutor());
		bind(CurrencyExchangeRateMapUpdater.class).asEagerSingleton();
	}

}

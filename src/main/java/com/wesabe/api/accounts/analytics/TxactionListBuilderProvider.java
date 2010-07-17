package com.wesabe.api.accounts.analytics;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

public class TxactionListBuilderProvider implements Provider<TxactionListBuilder> {
	private final CurrencyExchangeRateMap exchangeRateMap;
	
	@Inject
	public TxactionListBuilderProvider(CurrencyExchangeRateMap exchangeRateMap) {
		this.exchangeRateMap = exchangeRateMap;
	}

	@Override
	public TxactionListBuilder get() {
		return new TxactionListBuilder().setCurrencyExchangeRateMap(exchangeRateMap);
	}
}

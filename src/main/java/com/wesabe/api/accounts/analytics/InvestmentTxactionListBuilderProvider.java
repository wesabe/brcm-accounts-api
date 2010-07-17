package com.wesabe.api.accounts.analytics;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

public class InvestmentTxactionListBuilderProvider implements Provider<InvestmentTxactionListBuilder> {
	private final CurrencyExchangeRateMap exchangeRateMap;
	
	@Inject
	public InvestmentTxactionListBuilderProvider(CurrencyExchangeRateMap exchangeRateMap) {
		this.exchangeRateMap = exchangeRateMap;
	}

	@Override
	public InvestmentTxactionListBuilder get() {
		return new InvestmentTxactionListBuilder().setCurrencyExchangeRateMap(exchangeRateMap);
	}
}

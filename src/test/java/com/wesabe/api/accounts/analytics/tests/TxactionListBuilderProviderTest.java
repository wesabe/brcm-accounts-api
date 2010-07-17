package com.wesabe.api.accounts.analytics.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.analytics.TxactionListBuilderProvider;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@RunWith(Enclosed.class)
public class TxactionListBuilderProviderTest {
	public static class Building_A_TxactionListBuilder {
		private CurrencyExchangeRateMap exchangeRateMap;
		private TxactionListBuilderProvider factory;
		
		@Before
		public void setup() throws Exception {
			this.exchangeRateMap = mock(CurrencyExchangeRateMap.class);
			
			this.factory = new TxactionListBuilderProvider(exchangeRateMap);
		}
		
		@Test
		public void itBuildsATxactionListBuilder() throws Exception {
			assertThat(factory.get().getCurrencyExchangeRateMap(), is(exchangeRateMap));
		}
	}
}

package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.fest.assertions.Assertions.*;

import org.junit.Test;

import com.wesabe.api.accounts.analytics.SumOfMoney;

public class SumOfMoneyTest {
	private final SumOfMoney sum = new SumOfMoney(money("30.00", USD), 3);

	@Test
	public void itHasAnAmount() throws Exception {
		assertThat(sum.getAmount()).isEqualTo(money("30.00", USD));
	}
	
	@Test
	public void itHasACount() throws Exception {
		assertThat(sum.getCount()).isEqualTo(3);
	}
	
	@Test
	public void itIsHumanReadable() throws Exception {
		assertThat(sum.toString()).isEqualTo("30.00USD/3");
	}
}

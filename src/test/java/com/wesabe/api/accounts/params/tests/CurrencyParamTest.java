package com.wesabe.api.accounts.params.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.params.CurrencyParam;

@RunWith(Enclosed.class)
public class CurrencyParamTest {
	public static class Parsing_A_Valid_Currency_Code {
		private CurrencyParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new CurrencyParam("USD");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(USD));
		}
	}
	
	public static class Parsing_An_Invalid_Currency_Code {
		@Test
		public void itThrowsABadRequestError() throws Exception {
			try {
				new CurrencyParam("USFD");
			} catch (WebApplicationException e) {
				assertThat((String) e.getResponse().getEntity(), is("Invalid parameter: USFD (Unknown currency code: USFD)."));
			}
		}
	}
}

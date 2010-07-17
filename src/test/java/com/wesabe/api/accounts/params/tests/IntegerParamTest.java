package com.wesabe.api.accounts.params.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.params.IntegerParam;

@RunWith(Enclosed.class)
public class IntegerParamTest {
	public static class Parsing_A_Valid_Integer {
		private IntegerParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new IntegerParam("300");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(300));
		}
	}
	
	public static class Parsing_A_Invalid_Integer {
		
		@Test
		public void itThrowsA400BadRequestWebApplicationException() throws Exception {
			try {
				new IntegerParam("X#H");
				fail("should have thrown a WebApplicationException but didn't");
			} catch (WebApplicationException e) {
				assertEquals(400, e.getResponse().getStatus());
				assertEquals("Invalid parameter: X#H (not a valid integer).", e.getResponse().getEntity());
			}
		}
	}
}

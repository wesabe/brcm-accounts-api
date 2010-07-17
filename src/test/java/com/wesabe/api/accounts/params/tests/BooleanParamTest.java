package com.wesabe.api.accounts.params.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.params.BooleanParam;

@RunWith(Enclosed.class)
public class BooleanParamTest {
	public static class Parsing_A_Valid_Boolean {
		private BooleanParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new BooleanParam("true");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(true));
		}
		
		@Test
		public void itIsCaseInsensitive() throws Exception {
			assertThat(new BooleanParam("true").getValue(), is(true));
			assertThat(new BooleanParam("TRUE").getValue(), is(true));
		}
	}
	
	public static class Parsing_A_Invalid_Boolean {
		
		@Test
		public void itThrowsA400BadRequestWebApplicationException() throws Exception {
			try {
				new BooleanParam("X#H");
				fail("should have thrown a WebApplicationException but didn't");
			} catch (WebApplicationException e) {
				assertEquals(400, e.getResponse().getStatus());
				assertEquals("Invalid parameter: X#H (X#H is neither true nor false).", e.getResponse().getEntity());
			}
		}
	}

}

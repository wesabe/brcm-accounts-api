package com.wesabe.api.accounts.params.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.ws.rs.WebApplicationException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.params.ISODateParam;

@RunWith(Enclosed.class)
public class ISODateParamTest {
	public static class Parsing_A_Basic_Calendar_Date {
		private ISODateParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new ISODateParam("20060705");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(new DateTime(2006, 7, 5, 0, 0, 0, 0)));
		}
	}
	
	public static class Parsing_An_Extended_Calendar_Date {
		private ISODateParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new ISODateParam("2006-07-05");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(new DateTime(2006, 7, 5, 0, 0, 0, 0)));
		}
	}
	
	public static class Parsing_A_Basic_Week_Date {
		private ISODateParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new ISODateParam("2006W27");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(new DateTime(2006, 7, 3, 0, 0, 0, 0)));
		}
	}
	
	public static class Parsing_An_Extended_Week_Date {
		private ISODateParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new ISODateParam("2006-W27");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(new DateTime(2006, 7, 3, 0, 0, 0, 0)));
		}
	}
	
	public static class Parsing_A_Basic_Week_Date_With_Weekday {
		private ISODateParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new ISODateParam("2006W273");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(new DateTime(2006, 7, 5, 0, 0, 0, 0)));
		}
	}
	
	public static class Parsing_An_Extended_Week_Date_With_Weekday {
		private ISODateParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new ISODateParam("2006-W27-3");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(new DateTime(2006, 7, 5, 0, 0, 0, 0)));
		}
	}
	
	public static class Parsing_A_Basic_Ordinal_Date {
		private ISODateParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new ISODateParam("2006186");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(new DateTime(2006, 7, 5, 0, 0, 0, 0)));
		}
	}
	
	public static class Parsing_An_Extended_Ordinal_Date {
		private ISODateParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new ISODateParam("2006-186");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(new DateTime(2006, 7, 5, 0, 0, 0, 0)));
		}
	}
	
	public static class Parsing_A_Invalid_Date {
		
		@Test
		public void itThrowsA400BadRequestWebApplicationException() throws Exception {
			try {
				new ISODateParam("X#H");
				fail("should have thrown a WebApplicationException but didn't");
			} catch (WebApplicationException e) {
				assertEquals(400, e.getResponse().getStatus());
				assertEquals("Invalid parameter: X#H (not a valid ISO 8601 date).", e.getResponse().getEntity());
			}
		}
	}
}

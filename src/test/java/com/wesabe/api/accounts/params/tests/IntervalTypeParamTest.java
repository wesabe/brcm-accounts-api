package com.wesabe.api.accounts.params.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.analytics.IntervalType;
import com.wesabe.api.accounts.params.IntervalTypeParam;

@RunWith(Enclosed.class)
public class IntervalTypeParamTest {
	public static class Parsing_A_Valid_Interval_Type {
		private IntervalTypeParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new IntervalTypeParam("weekly");
		}
		
		@Test
		public void itHasAnIntervalType() throws Exception {
			assertThat(param.getValue(), is(IntervalType.WEEKLY));
		}
	}
	
	public static class Parsing_An_Invalid_Interval_Type {
		@Test
		public void itReturnsA404() throws Exception {
			try {
				new IntervalTypeParam("dingo");
			} catch (WebApplicationException e) {
				final Response response = e.getResponse();
				
				assertThat(response.getStatus(), is(404));
			}
		}
	}
}

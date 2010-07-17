package com.wesabe.api.accounts.params.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.uri.UriTemplate;
import com.wesabe.api.accounts.params.UriParam;

@RunWith(Enclosed.class)
public class UriParamTest {
	public static class Parsing_A_Valid_URI {
		private UriParam param;
		
		@Before
		public void setup() throws Exception {
			this.param = new UriParam("/whee/1");
		}
		
		@Test
		public void itHasAValue() throws Exception {
			assertThat(param.getValue(), is(URI.create("/whee/1")));
		}
		
		@Test
		public void itIsMatchableAgainsUriTemplates() throws Exception {
			final UriTemplate template = new UriTemplate("/whee/{id}");
			
			assertThat(param.match(template), is((Map<String, String>) ImmutableMap.of("id", "1")));
			assertThat(param.match(template, "id"), is("1"));
		}
	}
	
	public static class Parsing_A_Invalid_URI {
		
		@Test
		public void itThrowsA400BadRequestWebApplicationException() throws Exception {
			try {
				new UriParam("/%dg");
				fail("should have thrown a WebApplicationException but didn't");
			} catch (WebApplicationException e) {
				assertEquals(400, e.getResponse().getStatus());
				assertEquals("Invalid parameter: /%dg (not a valid URI).", e.getResponse().getEntity());
			}
		}
	}
}

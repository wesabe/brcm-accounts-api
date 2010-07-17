package com.wesabe.api.accounts.resources.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.dao.HealthDAO;
import com.wesabe.api.accounts.resources.HealthResource;

@RunWith(Enclosed.class)
public class HealthResourceTest {
	private static abstract class Context {
		protected HealthDAO healthDAO;
		protected HealthResource resource;
		
		public void setup() throws Exception {
			this.healthDAO = mock(HealthDAO.class);
			this.resource = new HealthResource(healthDAO);
		}
		
	}
	
	public static class Responding_When_Health extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			when(healthDAO.isHealthy()).thenReturn(true);
		}
		
		@Test
		public void itReturns204NoContent() throws Exception {
			final Response response = resource.show();
			assertThat(response.getStatus(), is(204));
		}
	}
	
	public static class Responding_When_Unhealthy extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			when(healthDAO.isHealthy()).thenReturn(false);
		}
		
		@Test
		public void itReturns500InternalServerError() throws Exception {
			final Response response = resource.show();
			assertThat(response.getStatus(), is(500));
		}
	}
}

package com.wesabe.api.accounts.providers.tests;

import static com.wesabe.api.tests.util.ProviderAsserts.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Type;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.wesabe.api.accounts.providers.WesabeUserInjectionProvider;
import com.wesabe.api.util.auth.WesabeUser;

@RunWith(Enclosed.class)
public class WesabeUserInjectionProviderTest {
	public static class Injecting_A_WesabeUser {
		private WesabeUserInjectionProvider provider = new WesabeUserInjectionProvider();
		
		@Test
		public void itIsAProvider() throws Exception {
			assertThat(WesabeUserInjectionProvider.class, is(jerseyProvider()));
		}
		
		@Test
		public void itOnlyInjectsWesabeUsers() throws Exception {
			assertThat(provider.getInjectedType(), is(sameInstance((Type) WesabeUser.class)));
		}
		
		@Test
		public void itInjectsTheUserPrincipalFromTheRequest() throws Exception {
			final WesabeUser user = mock(WesabeUser.class);
			
			final HttpRequestContext request = mock(HttpRequestContext.class);
			when(request.getUserPrincipal()).thenReturn(user);
			
			final HttpContext context = mock(HttpContext.class);
			when(context.getRequest()).thenReturn(request);
			
			assertThat(provider.getValue(context), is(user));
		}
	}
}

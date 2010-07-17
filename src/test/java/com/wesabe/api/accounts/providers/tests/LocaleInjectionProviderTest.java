package com.wesabe.api.accounts.providers.tests;

import static com.wesabe.api.tests.util.ProviderAsserts.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Type;
import java.util.Locale;

import javax.ws.rs.WebApplicationException;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.wesabe.api.accounts.providers.LocaleInjectionProvider;

@RunWith(Enclosed.class)
public class LocaleInjectionProviderTest {
	public static class Injecting_A_Locale {
		private LocaleInjectionProvider provider = new LocaleInjectionProvider();
		
		@Test
		public void itIsAProvider() throws Exception {
			assertThat(LocaleInjectionProvider.class, is(jerseyProvider()));
		}
		
		@Test
		public void itOnlyInjectsWesabeUsers() throws Exception {
			assertThat(provider.getInjectedType(), is(sameInstance((Type) Locale.class)));
		}
		
		@Test
		public void itInjectsTheFirstAcceptableLanguageFromTheRequest() throws Exception {
			final HttpRequestContext request = mock(HttpRequestContext.class);
			when(request.getAcceptableLanguages()).thenReturn(ImmutableList.of(Locale.CANADA, Locale.GERMANY));
			
			final HttpContext context = mock(HttpContext.class);
			when(context.getRequest()).thenReturn(request);
			
			assertThat(provider.getValue(context), is(Locale.CANADA));
		}
		
		@Test
		public void itInjectsTheDefaultLocaleIfNoLanguagesAreGiven() throws Exception {
			final HttpRequestContext request = mock(HttpRequestContext.class);
			when(request.getAcceptableLanguages()).thenReturn(ImmutableList.<Locale>of());
			
			final HttpContext context = mock(HttpContext.class);
			when(context.getRequest()).thenReturn(request);
			
			assertThat(provider.getValue(context), is(Locale.getDefault()));
		}
		
		@Test
		public void itInjectsTheDefaultLocaleIfAllLanguagesAreAcceptable() throws Exception {
			final HttpRequestContext request = mock(HttpRequestContext.class);
			when(request.getAcceptableLanguages()).thenReturn(ImmutableList.of(new Locale("*")));
			
			final HttpContext context = mock(HttpContext.class);
			when(context.getRequest()).thenReturn(request);
			
			assertThat(provider.getValue(context), is(Locale.getDefault()));
		}
		
		@Test
		public void itInjectsTheDefaultLocaleIfTheAcceptLanguagesHeaderIsMalformed() throws Exception {
			final HttpRequestContext request = mock(HttpRequestContext.class);
			when(request.getAcceptableLanguages()).thenThrow(new WebApplicationException(400));
			
			final HttpContext context = mock(HttpContext.class);
			when(context.getRequest()).thenReturn(request);
			
			assertThat(provider.getValue(context), is(Locale.getDefault()));
		}
	}
}

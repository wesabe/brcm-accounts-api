package com.wesabe.api.tests.util;

import javax.ws.rs.ext.Provider;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ProviderAsserts {
	private static class ProviderMatcher extends BaseMatcher<Class<?>> {
		
		@Override
		public boolean matches(Object arg0) {
			if (arg0 instanceof Class<?>) {
				final Class<?> klass = (Class<?>) arg0;
				return klass.getAnnotation(Provider.class) != null;
			}
			return false;
		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("a Jersey provider");
		}
	}
	
	public static ProviderMatcher jerseyProvider() {
		return new ProviderMatcher();
	}
}

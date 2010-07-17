package com.wesabe.api.accounts.providers;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import com.codahale.shore.injection.AbstractInjectionProvider;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.core.HttpContext;

@Provider
public class LocaleInjectionProvider extends AbstractInjectionProvider<Locale> {
	private static final Locale WILDCARD_LOCALE = new Locale("*");

	public LocaleInjectionProvider() {
		super(Locale.class);
	}

	@Override
	public Locale getValue(HttpContext c) {
		List<Locale> locales;

		try {
			locales = c.getRequest().getAcceptableLanguages();
		} catch (WebApplicationException e) {
			locales = ImmutableList.of();
		}

		if (!locales.isEmpty()) {
			final Locale locale = locales.get(0);
			if (!locale.equals(WILDCARD_LOCALE)) {
				return locale;
			}
		}

		return Locale.getDefault();
	}

}

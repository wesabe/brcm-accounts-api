package com.wesabe.api.accounts.providers;

import org.joda.time.DateTime;

import com.codahale.shore.injection.AbstractInjectionProvider;
import com.sun.jersey.api.core.HttpContext;

public class CurrentDateTimeProvider extends AbstractInjectionProvider<DateTime> {

	public CurrentDateTimeProvider() {
		super(DateTime.class);
	}

	@Override
	public DateTime getValue(HttpContext context) {
		return new DateTime();
	}

}

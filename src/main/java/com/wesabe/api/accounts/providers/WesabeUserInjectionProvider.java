package com.wesabe.api.accounts.providers;

import javax.ws.rs.ext.Provider;

import com.codahale.shore.injection.AbstractInjectionProvider;
import com.sun.jersey.api.core.HttpContext;
import com.wesabe.api.util.auth.WesabeUser;

@Provider
public class WesabeUserInjectionProvider extends AbstractInjectionProvider<WesabeUser> {
	public WesabeUserInjectionProvider() {
		super(WesabeUser.class);
	}

	@Override
	public WesabeUser getValue(HttpContext c) {
		return (WesabeUser) c.getRequest().getUserPrincipal();
	}
}

package com.wesabe.api.accounts;

import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.codahale.shore.AbstractConfiguration;
import com.google.inject.Stage;
import com.wesabe.api.accounts.modules.CurrencyExchangeRateMapModule;
import com.wesabe.api.accounts.modules.HibernateJMXModule;
import com.wesabe.api.util.auth.WesabeAuthenticator;

/**
 * brcm-accounts-api's Shore configuration.
 * 
 * @author coda
 */
public class Configuration extends AbstractConfiguration {
	@Override
	protected void configure() {
		addEntityPackage("com.wesabe.api.accounts.entities");
		addResourcePackage("com.wesabe.api.accounts.providers");
		addResourcePackage("com.wesabe.api.accounts.resources");
		addModule(new CurrencyExchangeRateMapModule());
		addModule(new HibernateJMXModule());
		setStage(Stage.PRODUCTION);
	}

	@Override
	public String getExecutableName() {
		return "brcm-accounts-api";
	}
	
	@Override
	public Connector getConnector() {
		return new SocketConnector();
	}
	
	@Override
	protected void configureContext(ServletContextHandler context) {
		final ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
		securityHandler.setRealmName("brcm-accounts-api");
		
		final Authenticator authenticator = new WesabeAuthenticator(securityHandler);
		securityHandler.setAuthenticator(authenticator);
		
		final Constraint requireAuthentication = new Constraint();
		requireAuthentication.setAuthenticate(true);
		requireAuthentication.setRoles(new String[] { "user" });
		
		final ConstraintMapping authenticateAll = new ConstraintMapping();
		authenticateAll.setPathSpec("/*");
		authenticateAll.setConstraint(requireAuthentication);
		
		final Constraint passThrough = new Constraint();
		passThrough.setAuthenticate(false);
		
		final ConstraintMapping healthCheckExemption = new ConstraintMapping();
		healthCheckExemption.setPathSpec("/health/");
		healthCheckExemption.setConstraint(passThrough);
		
		final ConstraintMapping statsExemption = new ConstraintMapping();
		statsExemption.setPathSpec("/stats/*");
		statsExemption.setConstraint(passThrough);
		
		securityHandler.setConstraintMappings(new ConstraintMapping[] { authenticateAll, healthCheckExemption, statsExemption });
		context.setSecurityHandler(securityHandler);
	}
	
	@Override
	protected void configureRequestLog(RequestLog log) {
		final NCSARequestLog requestLog = (NCSARequestLog) log;
		requestLog.setIgnorePaths(new String[] { "/health/", "/stats/*" });
		requestLog.setLogLatency(true);
	}
}

package com.wesabe.api.accounts.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.Configuration;
import com.wesabe.api.util.auth.WesabeAuthenticator;

@RunWith(Enclosed.class)
public class ConfigurationTest {
	private static class TestConfiguration extends Configuration {
		@Override
		public void configure() {
			super.configure();
		}
		
		@Override
		public void configureContext(ServletContextHandler context) {
			super.configureContext(context);
		}
		
		@Override
		protected void configureServer(Server server) {
			super.configureServer(server);
		}
	}
	
	public static class The_BRCM_Configuration {
		private TestConfiguration config;
		
		@Before
		public void setup() throws Exception {
			this.config = new TestConfiguration();
			config.configure();
		}
		
		@Test
		public void itSpecifiesAnEntityPackage() throws Exception {
			assertThat(config.getEntityPackages(), hasItem("com.wesabe.api.accounts.entities"));
		}
		
		@Test
		public void itSpecifiesAJerseyResourcePackage() throws Exception {
			assertThat(config.getResourcePackages(), hasItem("com.wesabe.api.accounts.resources"));
		}
		
		@Test
		public void itSpecifiesAJerseyProviderPackage() throws Exception {
			assertThat(config.getResourcePackages(), hasItem("com.wesabe.api.accounts.providers"));
		}
		
		@Test
		public void itUsesASocketConnector() throws Exception {
			assertThat(config.getConnector(), is(SocketConnector.class));
		}
	}
	
	public static class Configuring_The_Servlet_Context {
		private ServletContextHandler context;
		private TestConfiguration config;
		
		@Before
		public void setup() throws Exception {
			this.context = new ServletContextHandler();
			this.config = new TestConfiguration();
		}
		
		@Test
		public void itConfiguresASecurityHandlerWithAWesabeAuthenticatorAndAnInternalUserRealm() throws Exception {
			config.configureContext(context);
			
			final SecurityHandler securityHandler = context.getSecurityHandler();
			assertThat(securityHandler.getAuthenticator(), is(WesabeAuthenticator.class));
		}
		
		@Test
		public void itRequiresAuthenticationForAllRequests() throws Exception {
			config.configureContext(context);
			
			final ConstraintSecurityHandler securityHandler = (ConstraintSecurityHandler) context.getSecurityHandler();
			final ConstraintMapping authenticateAll = securityHandler.getConstraintMappings()[0];
			assertThat(authenticateAll.getPathSpec(), is("/*"));
			assertThat(authenticateAll.getConstraint().getAuthenticate(), is(true));
			assertThat(authenticateAll.getConstraint().getRoles(), is(new String[] { "user" }));
		}
		
		@Test
		public void itDoesNotRequireAuthenticationForAHealthCheckRequest() throws Exception {
			config.configureContext(context);
			
			final ConstraintSecurityHandler securityHandler = (ConstraintSecurityHandler) context.getSecurityHandler();
			final ConstraintMapping authenticateAll = securityHandler.getConstraintMappings()[1];
			assertThat(authenticateAll.getPathSpec(), is("/health/"));
			assertThat(authenticateAll.getConstraint().getAuthenticate(), is(false));
		}
		
		@Test
		public void itDoesNotRequireAuthenticationForAStatsRequest() throws Exception {
			config.configureContext(context);
			
			final ConstraintSecurityHandler securityHandler = (ConstraintSecurityHandler) context.getSecurityHandler();
			final ConstraintMapping authenticateAll = securityHandler.getConstraintMappings()[2];
			assertThat(authenticateAll.getPathSpec(), is("/stats/*"));
			assertThat(authenticateAll.getConstraint().getAuthenticate(), is(false));
		}
	}
}

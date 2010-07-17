package com.wesabe.api.accounts.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * A data access object for retrieving information about the current process's
 * health regarding database connections.
 * 
 * @author coda
 *
 */
public class HealthDAO {
	private static final String HEALTHY = "health-check";
	private final Provider<Session> provider;
	private final Logger logger;
	
	@Inject
	public HealthDAO(Provider<Session> provider, Logger logger) {
		this.provider = provider;
		this.logger = logger;
	}
	
	/**
	 * Return {@code true} if this process can successfully communicate with
	 * the database, {@code false} otherwise.
	 * 
	 * @return whether or not the database is healthy
	 */
	public boolean isHealthy() {
		try {
			final Session session = provider.get();
			final String result = (String) session.createSQLQuery("SELECT :value")
					.setString("value", HEALTHY)
					.setComment("health check")
					.uniqueResult();
			if (result.equals(HEALTHY)) {
				return true;
			}
			
			logger.severe("The database just returned " + result + " instead of " + HEALTHY + ". That's weird");
			return false;
			
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Error connecting to the database", e);
			return false;
		}
	}
}

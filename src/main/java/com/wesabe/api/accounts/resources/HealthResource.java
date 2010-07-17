package com.wesabe.api.accounts.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.wesabe.api.accounts.dao.HealthDAO;

/**
 * A health check which returns either 204 if the application is ready to accept
 * requests or 500 if something is wrong. Used by haproxy and other pieces of
 * HTTP middleware to check for backend health.
 * 
 * @author coda
 *
 */
@Path("/health/")
@Produces(MediaType.TEXT_PLAIN)
public class HealthResource {
	private final HealthDAO healthDAO;
	
	@Inject
	public HealthResource(HealthDAO healthDAO) {
		this.healthDAO = healthDAO;
	}
	
	@GET
	public Response show() {
		if (healthDAO.isHealthy()) {
			return Response.noContent().build();
		}
		return Response.serverError().build();
	}
}

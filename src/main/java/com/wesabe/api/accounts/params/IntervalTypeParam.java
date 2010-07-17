package com.wesabe.api.accounts.params;

import java.util.Locale;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codahale.shore.params.AbstractParam;
import com.wesabe.api.accounts.analytics.IntervalType;

/**
 * Parses a string into an {@link IntervalType}.
 * 
 * @author coda
 *
 */
public class IntervalTypeParam extends AbstractParam<IntervalType> {
	
	/**
	 * Creates a new {@link IntervalType} for a given string.
	 * 
	 * @param intervalType the interval type as a string
	 * @throws WebApplicationException if {@code interval} isn't a valid IntervalType
	 */
	public IntervalTypeParam(String intervalType) throws WebApplicationException {
		super(intervalType);
	}

	@Override
	protected IntervalType parse(String param) throws Exception {
		return IntervalType.valueOf(param.toUpperCase(Locale.US));
	}
	
	@Override
	protected Response onError(String param, Throwable e) {
		return Response.status(Status.NOT_FOUND).build();
	}
}

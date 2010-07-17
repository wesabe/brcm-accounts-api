package com.wesabe.api.accounts.params;

import javax.ws.rs.WebApplicationException;

import com.codahale.shore.params.AbstractParam;

/**
 * Parses a decimal string into an {@link Integer}.
 * 
 * @author coda
 *
 */
public class IntegerParam extends AbstractParam<Integer> {

	/**
	 * Creates a new {@link IntegerParam} for a given decimal string.
	 * 
	 * @param number a decimal string
	 * @throws WebApplicationException if {@code number} isn't a valid integer
	 */
	public IntegerParam(String number) throws WebApplicationException {
		super(number);
	}

	@Override
	protected Integer parse(String param) throws Exception {
		try {
			return Integer.valueOf(param);
		} catch (Throwable e) {
			throw new IllegalArgumentException("not a valid integer");
		}
	}
	
}

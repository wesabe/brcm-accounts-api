package com.wesabe.api.accounts.params;

import javax.ws.rs.WebApplicationException;

import com.codahale.shore.params.AbstractParam;

/**
 * Parses {@code "true"} or {@code "false"} into a {@link Boolean}, ignoring
 * case.
 * 
 * @author coda
 *
 */
public class BooleanParam extends AbstractParam<Boolean> {
	private static final String TRUE = Boolean.TRUE.toString();
	private static final String FALSE = Boolean.FALSE.toString();
	
	/**
	 * Creates a new {@link BooleanParam} for a given boolean string.
	 * 
	 * @param bool either {@code "true"} or {@code "false"}
	 * @throws WebApplicationException if {@code bool} isn't a valid boolean
	 */
	public BooleanParam(String bool) throws WebApplicationException {
		super(bool);
	}

	@Override
	protected Boolean parse(String bool) throws Exception {
		if (bool.equalsIgnoreCase(TRUE) || bool.equalsIgnoreCase(FALSE)) {
			return Boolean.valueOf(bool);
		}
		
		throw new IllegalArgumentException(bool + " is neither true nor false");
	}

}

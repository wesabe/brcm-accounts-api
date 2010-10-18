package com.wesabe.api.accounts.params;

import java.math.BigDecimal;

import javax.ws.rs.WebApplicationException;

import com.codahale.shore.params.AbstractParam;

public class DecimalParam extends AbstractParam<BigDecimal> {

	/**
	 * Creates a new {@link DecimalParam} for a given decimal string.
	 * 
	 * @param number a decimal string
	 * @throws WebApplicationException if {@code number} isn't a valid decimal
	 */
	public DecimalParam(String number) throws WebApplicationException {
		super(number);
	}

	@Override
	protected BigDecimal parse(String param) throws Exception {
		try {
			return new BigDecimal(param);
		} catch (Throwable e) {
			throw new IllegalArgumentException("not a valid decimal");
		}
	}

}

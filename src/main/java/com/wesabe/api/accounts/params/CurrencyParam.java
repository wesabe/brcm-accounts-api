package com.wesabe.api.accounts.params;

import java.util.Currency;

import javax.ws.rs.WebApplicationException;

import com.codahale.shore.params.AbstractParam;
import com.wesabe.api.util.money.CurrencyCodeParser;

/**
 * Parses an ISO 4217 currency code into a {@link Currency}.
 * 
 * @author coda
 * @see <a href="http://www.iso.org/iso/iso_catalogue/catalogue_tc/catalogue_detail.htm?csnumber=46121">ISO 4217</a>
 */
public class CurrencyParam extends AbstractParam<Currency> {
	private static final CurrencyCodeParser CURRENCY_CODE_PARSER = new CurrencyCodeParser();
	
	/**
	 * Creates a new {@link CurrencyParam} for a given ISO 4217 currency code.
	 * 
	 * @param currencyCode an ISO 4217 currency code
	 * @throws WebApplicationException if currencyCode is unknown
	 * @see CurrencyCodeParser#parse(String)
	 */
	public CurrencyParam(String currencyCode) throws WebApplicationException {
		super(currencyCode);
	}

	@Override
	protected Currency parse(String currencyCode) throws Exception {
		return CURRENCY_CODE_PARSER.parse(currencyCode);
	}

}

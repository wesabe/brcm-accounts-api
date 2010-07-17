package com.wesabe.api.accounts.params;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import com.codahale.shore.params.AbstractParam;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sun.jersey.api.uri.UriTemplate;

/**
 * Parses a string into a {@link URI}.
 * 
 * @author coda
 *
 * @see URI#URI(String)
 * @see <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>
 */
public class UriParam extends AbstractParam<URI> {

	/**
	 * Creates a new {@link UriParam} for a given URI string.
	 * 
	 * @param uri a URI string
	 * @throws WebApplicationException if {@code uri} isn't a valid URI
	 */
	public UriParam(String uri) throws WebApplicationException {
		super(uri);
	}

	@Override
	protected URI parse(String param) throws Exception {
		try {
			return new URI(param);
		} catch (Throwable e) {
			throw new IllegalArgumentException("not a valid URI");
		}
	}
	
	public Map<String, String> match(UriTemplate template) {
		final Map<String, String> values = Maps.newHashMap();
		if (template.match(getValue().toASCIIString(), values)) {
			return ImmutableMap.copyOf(values);
		}
		
		return ImmutableMap.of();
	}
	
	public String match(UriTemplate template, String variable) {
		return match(template).get(variable);
	}
}

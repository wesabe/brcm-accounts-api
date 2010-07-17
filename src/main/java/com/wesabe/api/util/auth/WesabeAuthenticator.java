package com.wesabe.api.util.auth;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.security.B64Code;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.util.StringUtil;

/**
 * An {@link Authenticator} which parses Wesabe credentials.
 * 
 * @author coda
 * 
 */
public class WesabeAuthenticator implements Authenticator {
	private static final char CREDENTIAL_DELIMITER = ':';
	private static final String HEADER_PREFIX = "Wesabe ";
	private static final long serialVersionUID = 2330872978583046225L;
	private final SecurityHandler handler;
	
	public WesabeAuthenticator(SecurityHandler handler) {
		this.handler = handler;
	}

	@Override
	public String getAuthMethod() {
		return "Wesabe";
	}

	@Override
	public boolean secureResponse(ServletRequest request, ServletResponse response,
		boolean mandatory, User validatedUser) throws ServerAuthException {
		return true;
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		// nothing to do
	}

	@Override
	public Authentication validateRequest(ServletRequest req, ServletResponse res, boolean mandatory)
		throws ServerAuthException {
		
		if (!mandatory) {
			return Authentication.NOT_CHECKED;
		}

		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		try {
			try {
				if ((header != null) && header.startsWith(HEADER_PREFIX)) {
					final String encodedCredentials = header.substring(header.indexOf(' ') + 1);
					final String credentials = B64Code.decode(encodedCredentials, StringUtil.__UTF8);
					final int i = credentials.indexOf(CREDENTIAL_DELIMITER);
					
					final String username = credentials.substring(0, i);
					final String password = credentials.substring(i + 1);

					final UserIdentity user = WesabeUser.create(username, password);
					if (user != null) {
						return new UserAuthentication(this, user);
					}
				}
			} catch (IllegalArgumentException e) {
				// fall through to sending an auth challenge
			} catch (StringIndexOutOfBoundsException e) {
				// fall through to sending an auth challenge
			}

			response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Wesabe realm=\"" + handler.getRealmName() + "\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return Authentication.SEND_CONTINUE;
		} catch (Exception e) {
			throw new ServerAuthException(e);
		}
	}
}

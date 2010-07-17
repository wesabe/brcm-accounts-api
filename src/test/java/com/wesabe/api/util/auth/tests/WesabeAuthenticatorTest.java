package com.wesabe.api.util.auth.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import com.wesabe.api.util.auth.WesabeAuthenticator;
import com.wesabe.api.util.auth.WesabeUser;

@RunWith(Enclosed.class)
public class WesabeAuthenticatorTest {
	static abstract class Context {
		protected Principal principal;
		protected Request request;
		protected Response response;
		protected SecurityHandler handler;
		protected WesabeAuthenticator authenticator;
		
		public void setup() throws Exception {
			this.principal = mock(Principal.class);
			
			this.handler = mock(SecurityHandler.class);
			
			when(handler.getRealmName()).thenReturn("Test API");
			this.request = mock(Request.class);
			this.response = mock(Response.class);
			this.authenticator = new WesabeAuthenticator(handler);
		}

		protected Authentication authenticate() throws ServerAuthException {
			return authenticator.validateRequest(request, response, true);
		}

		protected void assertReturnedChallenge(Authentication authentication) throws Exception {
			InOrder inOrder = inOrder(response);
			inOrder.verify(response).setHeader(HttpHeaders.WWW_AUTHENTICATE, "Wesabe realm=\"Test API\"");
			inOrder.verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
			
			assertThat(authentication).isEqualTo(Authentication.SEND_CONTINUE);
		}
		
	}
	
	public static class Authenticating_A_Nonmandatory_Resource extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itReturnsNotChecked() throws Exception {
			assertThat(authenticator.validateRequest(request, response, false)).isEqualTo(Authentication.NOT_CHECKED);
		}
	}
	
	public static class Authenticating_A_Valid_Request extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Wesabe MjA6ZjYwNzhlYmUwYzJmMDhjMjI1YzAzNDlhZWYyZmUwNjJkNzFiOTcyZTNjOTFiOTY4N2NjY2RmZjI0ZDBjOGFjZA==");
		}
		
		@Test
		public void itReturnsAUserAuthentication() throws Exception {
			assertThat(authenticate()).isInstanceOf(UserAuthentication.class);
		}
		
		@Test
		public void itCreatesAWesabeUserInstance() throws Exception {
			final UserAuthentication auth = (UserAuthentication) authenticate();
			assertThat(auth.getUserIdentity().getUserPrincipal()).isInstanceOf(WesabeUser.class);
			
			final WesabeUser user = (WesabeUser) auth.getUserIdentity().getUserPrincipal();
			assertThat(user.getUserId()).isEqualTo(20);
			assertThat(user.getAccountKey()).isEqualTo("f6078ebe0c2f08c225c0349aef2fe062d71b972e3c91b9687cccdff24d0c8acd");
		}
	}
	
	public static class Authenticating_A_Request_Without_Authorization_Headers extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();

			when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
		}

		@Test
		public void itReturnsAnAuthenticationChallenge() throws Exception {
			assertReturnedChallenge(authenticate());
		}
	}

	public static class Authenticating_A_Request_With_NonWesabe_Authorization_Headers extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();

			when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic MjA6ZjYwNzhlYmUwYzJmMDhjMjI1YzAzNDlhZWYyZmUwNjJkNzFiOTcyZTNjOTFiOTY4N2NjY2RmZjI0ZDBjOGFjZA==");
		}

		@Test
		public void itReturnsAnAuthenticationChallenge() throws Exception {
			assertReturnedChallenge(authenticate());
		}
	}

	public static class Authenticating_A_Request_With_Badly_Encoded_Authorization_Headers extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();

			when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Wesabe MjA6ZjYwNz%%mUwYzJmMDhjMjI1YzAzNDlhZWYyZmUwNjJkNzFiOTcyZTNjOTFiOTY4N2NjY2RmZjI0ZDBjOGFjZA==");
		}

		@Test
		public void itReturnsAnAuthenticationChallenge() throws Exception {
			assertReturnedChallenge(authenticate());
		}
	}

	public static class Authenticating_A_Request_With_Malformed_Credentials extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();

			when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Wesabe aGVsbG8gdGhlcmU=");
		}
		
		@Test
		public void itReturnsAnAuthenticationChallenge() throws Exception {
			assertReturnedChallenge(authenticate());
		}
	}
	
	public static class Authenticating_A_Request_With_A_Malformed_User_ID extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();

			when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Wesabe NGY0OmY2MDc4ZWJlMGMyZjA4YzIyNWMwMzQ5YWVmMmZlMDYyZDcxYjk3MmUzYzkxYjk2ODdjY2NkZmYyNGQwYzhhY2Q=");
		}
		
		@Test
		public void itReturnsAnAuthenticationChallenge() throws Exception {
			assertReturnedChallenge(authenticate());
		}
	}
	
	public static class Authenticating_A_Request_With_A_Malformed_Account_Key extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();

			when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Wesabe MjA6ZjYwNzhlYmUwYzJmMDhjMjI1YzAzNDlhZVdPT09PZjJmZTA2MmQ3MWI5NzJlM2M5MWI5Njg3Y2NjZGZmMjRkMGM4YWNk");
		}
		
		@Test
		public void itReturnsAnAuthenticationChallenge() throws Exception {
			assertReturnedChallenge(authenticate());
		}
	}
}

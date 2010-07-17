package com.wesabe.api.util.auth;

import java.security.Principal;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import org.eclipse.jetty.server.UserIdentity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Credentials for a Wesabe user, with a user ID and an account key.
 * 
 * @author coda
 *
 */
public class WesabeUser implements Principal, UserIdentity {
	private static final Pattern VALID_USER_ID = Pattern.compile("^[\\d]+$");
	private static final Pattern VALID_ACCOUNT_KEY = Pattern.compile("^[\\da-f]{64}$");
	private final int userId;
	private final String accountKey;
	
	/**
	 * If {@code userId} is a valid user ID and {@code accountKey} is a valid
	 * account key, returns a {@link WesabeUser} instance with the given user ID
	 * and account key. Otherwise, returns {@code null}.
	 * 
	 * @param userId the user's ID
	 * @param accountKey the user's account key
	 * @return a valid {@link WesabeUser} or {@code null}
	 */
	public static WesabeUser create(String userId, String accountKey) {
		if (VALID_USER_ID.matcher(userId).matches()
				&& VALID_ACCOUNT_KEY.matcher(accountKey).matches()) {
			return new WesabeUser(Integer.valueOf(userId), accountKey);
		}

		return null;
	}

	private WesabeUser(int userId, String accountKey) {
		this.userId = userId;
		this.accountKey = accountKey;
	}

	@Override
	public String getName() {
		return Integer.toString(userId);
	}

	public int getUserId() {
		return userId;
	}

	public String getAccountKey() {
		return accountKey;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(userId, accountKey);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WesabeUser) {
			final WesabeUser other = (WesabeUser) obj;
			return (userId == other.userId) && Objects.equal(accountKey, other.accountKey);
		}

		return false;
	}

	@Override
	public String toString() {
		return String.format("WesabeUser [userId=%d, accountKey=%s]", userId, accountKey);
	}

	@Override
	public Subject getSubject() {
		return new Subject(true, ImmutableSet.of(this), ImmutableSet.of(), ImmutableSet.of());
	}

	@Override
	public Principal getUserPrincipal() {
		return this;
	}

	@Override
	public boolean isUserInRole(String role, Scope scope) {
		return true;
	}
}

package com.wesabe.api.accounts.dao;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.codahale.shore.dao.AbstractDAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountStatus;
import com.wideplay.warp.persist.Transactional;

/**
 * A data access object for retrieving and storing {@link Account} instances.
 * 
 * @author coda
 * @see Account
 */
public class AccountDAO extends AbstractDAO<Account> {
	private static final EnumSet<AccountStatus> ACTIVE_STATUSES = EnumSet.of(AccountStatus.ACTIVE, AccountStatus.ARCHIVED);

	@Inject
	public AccountDAO(Provider<Session> provider) {
		super(provider, Account.class);
	}
	
	public Account findAccount(String accountKey, Integer accountId) {
		return uniqueResult(
				namedQuery("com.wesabe.api.accounts.Account.findByAccountKeyAndRelativeId")
				.setString("accountKey", accountKey)
				.setInteger("accountId", accountId)
		);
	}
	/**
	 * Queries the database for a list of accounts belonging to the user with a
	 * given account key. Returns only active and archived accounts.
	 * 
	 * @param accountKey the user's account key
	 * @return a list of active and archived accounts belonging to the user
	 */
	public List<Account> findVisibleAccounts(String accountKey) {
		return findAllAccountsByAccountKey(accountKey, ACTIVE_STATUSES);
	}
	
	/**
	 * Queries the database for a list of accounts belonging to the user with
	 * a given account key. Returns only accounts which have a status in
	 * {@code statuses}.
	 * 
	 * @param accountKey the user's account key
	 * @return a list of accounts belonging to the user
	 */
	public List<Account> findAllAccountsByAccountKey(String accountKey, Set<AccountStatus> statuses) {
		return list(
			namedQuery("com.wesabe.api.accounts.Account.findAllByAccountKey")
				.setString("accountKey", accountKey)
				.setParameterList("statuses", AccountStatus.toValues(statuses))
		);
	}
	
	/**
	 * Updates the given {@code account}'s database record with the data contained
	 * in the {@code account}.
	 * 
	 * @param account the account to persist to the database
	 * @return the updated account
	 */
	@Transactional
	public Account update(Account account) {
		currentSession().update(account);
		return account;
	}
}

package com.wesabe.api.accounts.dao;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.codahale.shore.dao.AbstractDAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.entities.AccountStatus;

/**
 * A data access object for retrieving and storing {@link InvestmentAccount} instances.
 * 
 * @author coda
 * @see InvestmentAccount
 */
public class InvestmentAccountDAO extends AbstractDAO<InvestmentAccount> {
	private static final EnumSet<AccountStatus> ACTIVE_STATUSES = EnumSet.of(AccountStatus.ACTIVE, AccountStatus.ARCHIVED);

	@Inject
	public InvestmentAccountDAO(Provider<Session> provider) {
		super(provider, InvestmentAccount.class);
	}
	
	/**
	 * Queries the database for a list of accounts belonging to the user with a
	 * given account key. Returns only active and archived accounts.
	 * 
	 * @param accountKey the user's account key
	 * @return a list of active and archived accounts belonging to the user
	 */
	public List<InvestmentAccount> findVisibleInvestmentAccounts(String accountKey) {
		return findAllInvestmentAccountsByInvestmentAccountKey(accountKey, ACTIVE_STATUSES);
	}
	
	/**
	 * Queries the database for a list of accounts belonging to the user with
	 * a given account key. Returns only accounts which have a status in
	 * {@code statuses}.
	 * 
	 * @param accountKey the user's account key
	 * @return a list of accounts belonging to the user
	 */
	public List<InvestmentAccount> findAllInvestmentAccountsByInvestmentAccountKey(String accountKey, Set<AccountStatus> statuses) {
		return list(
			namedQuery("com.wesabe.api.accounts.InvestmentAccount.findAllByAccountKey")
				.setString("accountKey", accountKey)
				.setParameterList("statuses", AccountStatus.toValues(statuses))
		);
	}
}

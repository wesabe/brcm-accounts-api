package com.wesabe.api.accounts.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.codahale.shore.dao.AbstractDAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Txaction;

/**
 * A data access object for retrieving and storing {@link Txaction} instances.
 * 
 * @author coda
 * @see Txaction
 */
public class TxactionDAO extends AbstractDAO<Txaction> {
	
	@Inject
	public TxactionDAO(Provider<Session> provider) {
		super(provider, Txaction.class);
	}

	/**
	 * Finds all {@link Txaction} instances which belong to a set of
	 * {@link Account}s and which were posted in a given {@link Interval}.
	 * 
	 * @param accounts
	 *            a set of Accounts
	 * @param dateRange
	 *            the interval of time
	 * @return a list of transactions in reverse chronological order
	 */
	public List<Txaction> findTxactionsInDateRange(Collection<Account> accounts, Interval dateRange) {
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		
		return list(
				namedQuery("com.wesabe.api.accounts.Txaction.findInDateRange")
					.setParameterList("accounts", accounts)
					.setParameter("startDate", dateRange.getStart())
					.setParameter("endDate", dateRange.getEnd())
		);
	}

	/**
	 * Finds all {@link Txaction} instances which belong to a set of
	 * {@link Account}s and which were posted before a given {@link DateTime}.
	 * 
	 * @param accounts
	 *            a set of Accounts
	 * @param endDate
	 *            the date after which transactions should be ignored
	 * @return a list of transactions in reverse chronological order
	 */
	public List<Txaction> findTxactionsBeforeDate(Collection<Account> accounts, DateTime endDate) {
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		
		return list(
				namedQuery("com.wesabe.api.accounts.Txaction.findBeforeDate")
					.setParameterList("accounts", accounts)
					.setParameter("endDate", endDate)
		);
	}

	/**
	 * Finds all {@link Txaction} instances which belong to a set of
	 * {@link Account}s and which were posted after a given {@link DateTime}.
	 * 
	 * @param accounts
	 *            a set of Accounts
	 * @param startDate
	 *            the date before which transactions should be ignored
	 * @return a list of transactions in reverse chronological order
	 */
	public List<Txaction> findTxactionsAfterDate(Collection<Account> accounts, DateTime startDate) {
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		
		return list(
				namedQuery("com.wesabe.api.accounts.Txaction.findAfterDate")
					.setParameterList("accounts", accounts)
					.setParameter("startDate", startDate)
		);
	}

	/**
	 * Finds all {@link Txaction} instances which belong to a set of
	 * {@link Account}s.
	 * 
	 * @param accounts
	 *            a set of Accounts
	 * @return a list of transactions in reverse chronological order
	 */
	public List<Txaction> findTxactions(Collection<Account> accounts) {
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		
		return list(
				namedQuery("com.wesabe.api.accounts.Txaction.findInAccounts")
					.setParameterList("accounts", accounts)
		);
	}
}
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
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.entities.InvestmentTxaction;

/**
 * A data access object for retrieving and storing {@link InvestmentTxaction} instances.
 * 
 * @author brad
 * @see InvestmentTxaction
 */
public class InvestmentTxactionDAO extends AbstractDAO<InvestmentTxaction> {
	
	@Inject
	public InvestmentTxactionDAO(Provider<Session> provider) {
		super(provider, InvestmentTxaction.class);
	}

	/**
	 * Finds all {@link InvestmentTxaction} instances which belong to a set of
	 * {@link Account}s and which were posted in a given {@link Interval}.
	 * 
	 * @param accounts
	 *            a set of Accounts
	 * @param dateRange
	 *            the interval of time
	 * @return a list of transactions in reverse chronological order
	 */
	public List<InvestmentTxaction> findInvestmentTxactionsInDateRange(Collection<InvestmentAccount> accounts, Interval dateRange) {
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		
		return list(
				namedQuery("com.wesabe.api.accounts.InvestmentTxaction.findInDateRange")
					.setParameterList("accounts", accounts)
					.setParameter("startDate", dateRange.getStart())
					.setParameter("endDate", dateRange.getEnd())
		);
	}

	/**
	 * Finds all {@link InvestmentTxaction} instances which belong to a set of
	 * {@link Account}s and which were posted before a given {@link DateTime}.
	 * 
	 * @param accounts
	 *            a set of Accounts
	 * @param endDate
	 *            the date after which transactions should be ignored
	 * @return a list of transactions in reverse chronological order
	 */
	public List<InvestmentTxaction> findInvestmentTxactionsBeforeDate(Collection<InvestmentAccount> accounts, DateTime endDate) {
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		
		return list(
				namedQuery("com.wesabe.api.accounts.InvestmentTxaction.findBeforeDate")
					.setParameterList("accounts", accounts)
					.setParameter("endDate", endDate)
		);
	}

	/**
	 * Finds all {@link InvestmentTxaction} instances which belong to a set of
	 * {@link Account}s and which were posted after a given {@link DateTime}.
	 * 
	 * @param accounts
	 *            a set of Accounts
	 * @param startDate
	 *            the date before which transactions should be ignored
	 * @return a list of transactions in reverse chronological order
	 */
	public List<InvestmentTxaction> findInvestmentTxactionsAfterDate(Collection<InvestmentAccount> accounts, DateTime startDate) {
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		
		return list(
				namedQuery("com.wesabe.api.accounts.InvestmentTxaction.findAfterDate")
					.setParameterList("accounts", accounts)
					.setParameter("startDate", startDate)
		);
	}

	/**
	 * Finds all {@link InvestmentTxaction} instances which belong to a set of
	 * {@link Account}s.
	 * 
	 * @param accounts
	 *            a set of Accounts
	 * @return a list of transactions in reverse chronological order
	 */
	public List<InvestmentTxaction> findInvestmentTxactions(Collection<InvestmentAccount> accounts) {
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		
		return list(
				namedQuery("com.wesabe.api.accounts.InvestmentTxaction.findInAccounts")
					.setParameterList("accounts", accounts)
		);
	}
}
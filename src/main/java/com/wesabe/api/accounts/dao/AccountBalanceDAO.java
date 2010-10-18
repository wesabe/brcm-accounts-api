package com.wesabe.api.accounts.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;

import com.codahale.shore.dao.AbstractDAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.api.accounts.entities.AccountBalance;

public class AccountBalanceDAO extends AbstractDAO<AccountBalance> {
	private Provider<DateTime> currentDateTimeProvider;

	@Inject
	public AccountBalanceDAO(Provider<Session> provider, Provider<DateTime> currentDateTimeProvider) {
		super(provider, AccountBalance.class);
		this.currentDateTimeProvider = currentDateTimeProvider;
	}

	public AccountBalance findAccountBalance(String accountKey, Integer accountBalanceId) {
		return uniqueResult(
				namedQuery("com.wesabe.api.accounts.AccountBalance.findByAccountKeyAndRelativeAccountIdAndBalanceId")
				.setString("accountKey", accountKey)
				.setInteger("accountBalanceId", accountBalanceId)
		);
	}
	
	public void create(AccountBalance accountBalance) {
		Transaction transaction = currentSession().beginTransaction();
		
		accountBalance.setCreatedAt(currentDateTimeProvider.get());
		accountBalance.setUpdatedAt(currentDateTimeProvider.get());
		
		currentSession().save(accountBalance);
		currentSession().flush();
		
		transaction.commit();
	}
}

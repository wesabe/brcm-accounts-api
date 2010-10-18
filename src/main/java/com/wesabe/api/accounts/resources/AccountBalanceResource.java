package com.wesabe.api.accounts.resources;

import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.wesabe.api.accounts.dao.AccountBalanceDAO;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.presenters.AccountBalancePresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.xmlson.XmlsonObject;

@Path("/v2/accounts/accounts/{accountId}/balances/{balanceId}")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AccountBalanceResource {
	private AccountBalanceDAO accountBalanceDAO;
	private AccountDAO accountDAO;
	private AccountBalancePresenter accountBalancePresenter;

	@Inject
	public AccountBalanceResource(AccountBalanceDAO accountBalanceDAO, AccountDAO accountDAO, AccountBalancePresenter accountBalancePresenter) {
		this.accountBalanceDAO = accountBalanceDAO;
		this.accountDAO = accountDAO;
		this.accountBalancePresenter = accountBalancePresenter;
	}
	
	@GET
	public XmlsonObject show(@Context WesabeUser user,
			@Context Locale locale,
			@PathParam("accountId") IntegerParam accountId,
			@PathParam("balanceId") IntegerParam balanceId) {
		
		final Account account = accountDAO.findAccount(user.getAccountKey(), accountId.getValue());
		final AccountBalance accountBalance = accountBalanceDAO.findAccountBalance(user.getAccountKey(), balanceId.getValue());
		
		if (accountBalance == null || account == null || !account.equals(accountBalance.getAccount())) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		return accountBalancePresenter.present(accountBalance, locale);
	}
}

package com.wesabe.api.accounts.resources;

import java.util.Locale;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.wesabe.api.accounts.dao.AccountBalanceDAO;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.api.accounts.params.DecimalParam;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.presenters.AccountBalancePresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

@Path("/v2/accounts/accounts/{accountId}/balances")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AccountBalancesResource {
	private AccountBalancePresenter presenter;
	private AccountDAO accountDAO;
	private AccountBalanceDAO accountBalanceDAO;

	@Inject
	public AccountBalancesResource(AccountBalanceDAO accountBalanceDAO, AccountDAO accountDAO, AccountBalancePresenter presenter) {
		this.presenter = presenter;
		this.accountDAO = accountDAO;
		this.accountBalanceDAO = accountBalanceDAO;
	}
	
	@GET
	public XmlsonArray list(@Context WesabeUser user,
			@Context Locale locale,
			@PathParam("accountId") IntegerParam accountId) {
		
		final XmlsonArray result = new XmlsonArray("account-balances");
		final Account account = accountDAO.findAccount(user.getAccountKey(), accountId.getValue());

		if (account.hasBalance()) {
			for (AccountBalance accountBalance : account.getAccountBalances()) {
				result.add(presenter.present(accountBalance, locale));
			}
		}
		
		return result;
	}
	
	@POST
	public XmlsonObject create(@Context WesabeUser user,
			@Context Locale locale,
			@PathParam("accountId") IntegerParam accountId,
			@FormParam("balance") DecimalParam balance) {
		
		if (balance == null) {
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST)
						.entity("balance is required")
						.build());
		}
		
		final Account account = accountDAO.findAccount(user.getAccountKey(), accountId.getValue());
		
		if (account == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (!account.hasBalance()) {
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST)
						.entity(String.format("%s accounts do not have balances", account.getAccountType().toString()))
						.build());
		}
		
		final AccountBalance accountBalance = new AccountBalance(account, balance.getValue(), new DateTime());
		
		accountBalanceDAO.create(accountBalance);
		
		return presenter.present(accountBalance, locale);
	}
}

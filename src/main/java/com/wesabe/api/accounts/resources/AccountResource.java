package com.wesabe.api.accounts.resources;

import java.util.Locale;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.InvalidStateException;

import com.google.inject.Inject;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.presenters.AccountPresenter;
import com.wesabe.api.accounts.presenters.InvalidStateExceptionPresenter;
import com.wesabe.api.accounts.presenters.InvestmentAccountPresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.xmlson.XmlsonObject;

/**
 * Return account details for a single account
 * 
 * @author brad
 */
@Path("/v2/accounts/accounts/{accountId}")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AccountResource {
	private final AccountDAO accountDAO;
	private final AccountPresenter accountPresenter;
	private final InvestmentAccountPresenter investmentAccountPresenter;
	
	@Inject
	public AccountResource(AccountDAO accountDAO, AccountPresenter accountPresenter, 
			InvestmentAccountPresenter investmentAccountPresenter) {
		this.accountDAO = accountDAO;
		this.accountPresenter = accountPresenter;
		this.investmentAccountPresenter = investmentAccountPresenter;
	}
	
	@GET
	public XmlsonObject show(@Context WesabeUser user,
			@Context Locale locale,
			@PathParam("accountId") IntegerParam accountId) {
		
		final Account account = accountDAO.findAccount(user.getAccountKey(), accountId.getValue());
		if (account == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		return present(account, locale);
	}
	
	@PUT
	public XmlsonObject update(@Context WesabeUser user,
			@Context Locale locale,
			@PathParam("accountId") IntegerParam accountId,
			@FormParam("name") String name,
			@FormParam("currency") CurrencyParam currency) {
		
		final Account account = accountDAO.findAccount(user.getAccountKey(), accountId.getValue());
		if (account == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (name != null) {
			account.setName(name);
		}
		
		if (currency != null) {
			account.setCurrency(currency.getValue());
		}
		
		try {
			accountDAO.update(account);
		} catch (InvalidStateException ex) {
			throw new WebApplicationException(
					Response
						.status(Status.BAD_REQUEST)
						.entity(new InvalidStateExceptionPresenter().present(ex))
						.build()
			);
		}
		
		return present(account, locale);
	}
	
	private XmlsonObject present(Account account, Locale locale) {
		if (account instanceof InvestmentAccount) {
			return investmentAccountPresenter.present(account, locale);
		} else {
			return accountPresenter.present(account, locale);
		}
	}
}

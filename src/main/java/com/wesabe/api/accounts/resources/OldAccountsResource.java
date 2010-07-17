package com.wesabe.api.accounts.resources;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.AccountStatus;
import com.wesabe.api.accounts.params.BooleanParam;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.presenters.AccountListPresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.xmlson.XmlsonObject;

// REVIEW coda@wesabe.com -- May 22, 2009: Replace OldAccountsResource with a redirect to the new stuff.

/**
 * The legacy interface for an accounts listing.
 * 
 * @author coda
 */
@Path("/v2/accounts/accounts/all/{currency}")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class OldAccountsResource {
	private final AccountDAO accountDAO;
	private final AccountListPresenter presenter;
	
	@Inject
	public OldAccountsResource(AccountDAO accountDAO, AccountListPresenter presenter) {
		this.accountDAO = accountDAO;
		this.presenter = presenter;
	}
	
	@GET
	public XmlsonObject show(@Context WesabeUser user,
			@Context Locale locale,
			@PathParam("currency") CurrencyParam currency,
			@QueryParam("include_archived") @DefaultValue("false") BooleanParam includeArchived) {
		
		final AccountList accounts = new AccountList(accountDAO.findAllAccountsByAccountKey(
			user.getAccountKey(),
			getStatuses(includeArchived.getValue())
		));
		
		return presenter.present(accounts, currency.getValue(), locale);
	}

	private Set<AccountStatus> getStatuses(Boolean includeArchived) {
		if (includeArchived) {
			return EnumSet.of(AccountStatus.ACTIVE, AccountStatus.ARCHIVED);
		}
		
		return EnumSet.of(AccountStatus.ACTIVE);
	}
}

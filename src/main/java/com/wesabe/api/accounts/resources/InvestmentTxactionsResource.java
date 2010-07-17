package com.wesabe.api.accounts.resources;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.joda.time.Interval;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sun.jersey.api.uri.UriTemplate;
import com.wesabe.api.accounts.analytics.InvestmentTxactionListBuilder;
import com.wesabe.api.accounts.dao.InvestmentAccountDAO;
import com.wesabe.api.accounts.dao.InvestmentTxactionDAO;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.entities.InvestmentTxaction;
import com.wesabe.api.accounts.entities.InvestmentTxactionList;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.params.UriParam;
import com.wesabe.api.accounts.presenters.InvestmentTxactionListPresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.xmlson.XmlsonObject;

@Path("/v2/accounts/investment-transactions/{currency}")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class InvestmentTxactionsResource {
	private static final Pattern VALID_ACCOUNT_ID = Pattern.compile("^[0-9]+$");
	private final InvestmentAccountDAO investmentAccountDAO;
	private final InvestmentTxactionDAO investmentTxactionDAO;
	private final Provider<InvestmentTxactionListBuilder> builderProvider;
	private final InvestmentTxactionListPresenter presenter;
	
	@Inject
	public InvestmentTxactionsResource(InvestmentAccountDAO investmentAccountDAO, InvestmentTxactionDAO InvestmentTxactionDAO, Provider<InvestmentTxactionListBuilder> builderProvider, InvestmentTxactionListPresenter presenter) {
		this.investmentAccountDAO = investmentAccountDAO;
		this.investmentTxactionDAO = InvestmentTxactionDAO;
		this.builderProvider = builderProvider;
		this.presenter = presenter;
	}
		
	@GET
	public XmlsonObject show(@Context WesabeUser user,
			@Context Locale locale,
			@PathParam("currency") CurrencyParam currency,
			@QueryParam("limit") IntegerParam limit,
			@QueryParam("offset") IntegerParam offset,
			@QueryParam("start") ISODateParam startDate,
			@QueryParam("end") ISODateParam endDate,
			@QueryParam("account") Set<UriParam> accountUris,
			@QueryParam("investment-security") Set<String> investmentSecurityNames) {
		
		final List<InvestmentAccount> accounts = getAccounts(user, accountUris);
		final InvestmentTxactionList investmentTxactions = filterTxactions(
				accounts,
				getInvestmentTxactions(accounts, startDate, endDate),
				limit, offset, investmentSecurityNames
		);

		return presenter.present(investmentTxactions, locale);
	}

	private InvestmentTxactionList filterTxactions(List<InvestmentAccount> accounts,
			List<InvestmentTxaction> investmentTxactions, IntegerParam limit, 
			IntegerParam offset, Set<String> investmentSecurityNames) {
		final InvestmentTxactionListBuilder investmentTxactionListBuilder = builderProvider.get();
		investmentTxactionListBuilder.setAccounts(accounts);

		if (!investmentSecurityNames.isEmpty()) {
			investmentTxactionListBuilder.setInvestmentSecurityNames(investmentSecurityNames);
		}
		if (limit != null) {
			investmentTxactionListBuilder.setLimit(limit.getValue());
		}
		if (offset != null) {
			investmentTxactionListBuilder.setOffset(offset.getValue());
		}
		
		final InvestmentTxactionList filteredTxactions = investmentTxactionListBuilder.build(investmentTxactions);
		return filteredTxactions;
	}

	private List<InvestmentTxaction> getInvestmentTxactions(List<InvestmentAccount> accounts, ISODateParam startDate,
			ISODateParam endDate) {
		final List<InvestmentTxaction> investmentTxactions;
		if ((startDate != null) && (endDate != null)) {
			try {
				final Interval dateRange = new Interval(startDate.getValue(), endDate.getValue());
				investmentTxactions = investmentTxactionDAO.findInvestmentTxactionsInDateRange(accounts, dateRange);
			} catch (IllegalArgumentException e) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
		} else if (startDate != null) {
			investmentTxactions = investmentTxactionDAO.findInvestmentTxactionsAfterDate(accounts, startDate.getValue());
		} else if (endDate != null) {
			investmentTxactions = investmentTxactionDAO.findInvestmentTxactionsBeforeDate(accounts, endDate.getValue());
		} else {
			investmentTxactions = investmentTxactionDAO.findInvestmentTxactions(accounts);
		}
		return investmentTxactions;
	}

	private List<InvestmentAccount> getAccounts(WesabeUser user, Set<UriParam> accountUris) {
		final List<InvestmentAccount> accounts = investmentAccountDAO.findVisibleInvestmentAccounts(user.getAccountKey());
		
		if (!accountUris.isEmpty()) {
			final Set<Integer> selectedRelativeIds = getRelativeIds(accountUris);
			final ImmutableList.Builder<InvestmentAccount> filteredAccounts = ImmutableList.builder();
			for (InvestmentAccount account : accounts) {
				if (selectedRelativeIds.contains(account.getRelativeId())) {
					filteredAccounts.add(account);
				}
			}
			return filteredAccounts.build();
		}
		
		return accounts;
	}

	private Set<Integer> getRelativeIds(Set<UriParam> accountUris) {
		final Builder<Integer> relativeIds = ImmutableSet.builder();
		final UriTemplate template = new UriTemplate("/accounts/{id}");
		for (UriParam accountUri : accountUris) {
			final String relativeId = accountUri.match(template, "id");
			if ((relativeId != null) && VALID_ACCOUNT_ID.matcher(relativeId).matches()) {
				relativeIds.add(Integer.valueOf(relativeId));
			}
		}
		
		return relativeIds.build();
	}
}

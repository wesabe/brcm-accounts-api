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

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;
import com.sun.jersey.api.uri.UriTemplate;
import com.wesabe.api.accounts.analytics.NetWorthSummarizer;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.dao.TxactionDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.params.IntervalTypeParam;
import com.wesabe.api.accounts.params.UriParam;
import com.wesabe.api.accounts.presenters.NetWorthSummaryPresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonObject;

@Path("/v2/accounts/analytics/summaries/net-worth/{interval}/{start}/{end}/{currency}")
@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class NetWorthSummaryResource {
	private static final Pattern VALID_ACCOUNT_ID = Pattern.compile("^[0-9]+$");
	private final AccountDAO accountDAO;
	private final TxactionDAO txactionDAO;
	private final NetWorthSummarizer summarizer;
	private final NetWorthSummaryPresenter presenter;

	@Inject
	public NetWorthSummaryResource(AccountDAO accountDAO, TxactionDAO txactionDAO,
			NetWorthSummarizer summarizer, NetWorthSummaryPresenter presenter) {
		this.accountDAO = accountDAO;
		this.txactionDAO = txactionDAO;
		this.summarizer = summarizer;
		this.presenter = presenter;
	}

	@GET
	public XmlsonObject show(@Context WesabeUser user, @Context Locale locale,
			@PathParam("interval") IntervalTypeParam intervalType,
			@PathParam("currency") CurrencyParam currency,
			@PathParam("start") ISODateParam startDate,
			@PathParam("end") ISODateParam endDate,
			@QueryParam("account") Set<UriParam> accountUris,
			@QueryParam("ignore-tag") Set<Tag> ignoredTags) {
		
		final DateTime intervalStartDate = intervalType.getValue().currentInterval(startDate.getValue()).getStart();
		final DateTime intervalEndDate = intervalType.getValue().currentInterval(endDate.getValue()).getEnd();
		
		if (intervalEndDate.isBefore(intervalStartDate)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		final Interval dateRange = new Interval(intervalStartDate, intervalEndDate);

		final List<Account> accounts = getAccounts(user, accountUris);

		final List<Txaction> txactions = txactionDAO.findTxactionsInDateRange(accounts, dateRange);
		
		final ImmutableMap<Interval, Money> summaries = summarizer.summarize(accounts, txactions, dateRange, intervalType.getValue(), currency.getValue(), ignoredTags);

		return presenter.present(summaries, locale);
	}
	
	private List<Account> getAccounts(WesabeUser user, Set<UriParam> accountUris) {
		final List<Account> accounts = accountDAO.findVisibleAccounts(user.getAccountKey());
		
		if (!accountUris.isEmpty()) {
			final Set<Integer> selectedRelativeIds = getRelativeIds(accountUris);
			final ImmutableList.Builder<Account> filteredAccounts = ImmutableList.builder();
			for (Account account : accounts) {
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

package com.wesabe.api.accounts.resources;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.joda.time.Interval;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.analytics.TagSummarizer;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.dao.TxactionDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.presenters.TagSummaryPresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.xmlson.XmlsonObject;

@Path("/v2/accounts/analytics/summaries/tags/{start}/{end}/{currency}")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class OldTagSummaryResource {
	private final AccountDAO accountDAO;
	private final TxactionDAO txactionDAO;
	private final TagSummarizer summarizer;
	private final TagSummaryPresenter presenter;

	@Inject
	public OldTagSummaryResource(AccountDAO accountDAO, TxactionDAO txactionDAO,
			TagSummarizer summarizer, TagSummaryPresenter presenter) {
		this.accountDAO = accountDAO;
		this.txactionDAO = txactionDAO;
		this.summarizer = summarizer;
		this.presenter = presenter;
	}

	@GET
	public XmlsonObject show(@Context WesabeUser user, @Context Locale locale,
			@PathParam("currency") CurrencyParam currency,
			@PathParam("start") ISODateParam startDate,
			@PathParam("end") ISODateParam endDate) {

		if (endDate.getValue().isBefore(startDate.getValue())) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		final Interval dateRange = new Interval(startDate.getValue(), endDate.getValue());

		final List<Account> accounts = accountDAO.findVisibleAccounts(user.getAccountKey());

		final List<Txaction> txactions = txactionDAO.findTxactionsInDateRange(accounts, dateRange);

		final ImmutableMap<Tag, MonetarySummary> results = summarizer.summarize(txactions, currency
				.getValue());

		return presenter.present(results, locale);
	}
}

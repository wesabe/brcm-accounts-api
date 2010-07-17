package com.wesabe.api.accounts.resources;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.DefaultValue;
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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.wesabe.api.accounts.analytics.TagHierarchy;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder.HierarchyType;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder.TagImportanceScheme;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.dao.TxactionDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.params.BooleanParam;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.presenters.TagHierarchyPresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.xmlson.XmlsonObject;

@Path("/v2/accounts/analytics/summaries/tag-hierarchies/{start}/{end}/{currency}")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class TagHierarchyResource {
	private final AccountDAO accountDAO;
	private final TxactionDAO txactionDAO;
	private final TagHierarchyBuilder tagHierarchyBuilder;
	private final TagHierarchyPresenter presenter;

	@Inject
	public TagHierarchyResource(AccountDAO accountDAO,
		TxactionDAO txactionDAO, TagHierarchyBuilder tagHierarchyBuilder,
		TagHierarchyPresenter presenter) {
		this.accountDAO = accountDAO;
		this.txactionDAO = txactionDAO;
		this.tagHierarchyBuilder = tagHierarchyBuilder;
		this.presenter = presenter;
	}

	@GET
	public XmlsonObject show(@Context WesabeUser user, @Context Locale locale,
			@PathParam("currency") CurrencyParam currency,
			@PathParam("start") ISODateParam startDate,
			@PathParam("end") ISODateParam endDate,
			@QueryParam("max-tags") @DefaultValue("5") IntegerParam maxTags,
			@QueryParam("rank-by-amount") @DefaultValue("true") BooleanParam rankByAmount,
			@QueryParam("spending") @DefaultValue("true") BooleanParam spending) {

		if (endDate.getValue().isBefore(startDate.getValue())) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		final Interval dateRange = new Interval(startDate.getValue(), endDate.getValue());

		final List<Account> accounts = accountDAO.findVisibleAccounts(user.getAccountKey());

		final List<Txaction> txactions = txactionDAO.findTxactionsInDateRange(accounts, dateRange);

		final TagHierarchy tagHierarchy = tagHierarchyBuilder.build(
			txactions,
			currency.getValue(),
			getTagImportanceScheme(rankByAmount.getValue()),
			getHierarchyType(spending.getValue()),
			ImmutableSet.<Tag> of(),
			maxTags.getValue()
		);

		return presenter.present(tagHierarchy, locale);
	}

	private HierarchyType getHierarchyType(boolean spending) {
		if (spending) {
			return TagHierarchyBuilder.HierarchyType.SPENDING;
		}
		return TagHierarchyBuilder.HierarchyType.EARNINGS;
	}

	private TagImportanceScheme getTagImportanceScheme(boolean rankByAmount) {
		if (rankByAmount) {
			return TagHierarchyBuilder.TagImportanceScheme.RANK_BY_AMOUNT;
		}
		
		return TagHierarchyBuilder.TagImportanceScheme.RANK_BY_COUNT;
	}
}

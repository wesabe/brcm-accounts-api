package com.wesabe.api.accounts.resources;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sun.jersey.api.uri.UriTemplate;
import com.wesabe.api.accounts.analytics.TxactionListBuilder;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.dao.TxactionDAO;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.params.BooleanParam;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.params.UriParam;
import com.wesabe.api.accounts.presenters.TxactionListPresenter;
import com.wesabe.api.util.auth.WesabeUser;
import com.wesabe.xmlson.XmlsonObject;

@Path("/v2/accounts/transactions/{currency}")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class OldTxactionsResource {
	private static final Pattern VALID_ACCOUNT_ID = Pattern.compile("^[0-9]+$");
	private final AccountDAO accountDAO;
	private final TxactionDAO txactionDAO;
	private final Provider<TxactionListBuilder> builderProvider;
	private final TxactionListPresenter presenter;
	
	@Inject
	public OldTxactionsResource(AccountDAO accountDAO, TxactionDAO txactionDAO, Provider<TxactionListBuilder> builderProvider, TxactionListPresenter presenter) {
		this.accountDAO = accountDAO;
		this.txactionDAO = txactionDAO;
		this.builderProvider = builderProvider;
		this.presenter = presenter;
	}
	
	// REVIEW coda@wesabe.com -- Jun 5, 2009: Refactor into Parameter Object.
	// Build a @Context-sensitive parameter class out of this message. (Though
	// this may affect WADL generation.)
	
	@GET
	public XmlsonObject show(@Context WesabeUser user,
			@Context Locale locale,
			@PathParam("currency") CurrencyParam currency,
			@QueryParam("unedited") @DefaultValue("false") BooleanParam uneditedOnly,
			@QueryParam("limit") IntegerParam limit,
			@QueryParam("offset") IntegerParam offset,
			@QueryParam("start") ISODateParam startDate,
			@QueryParam("end") ISODateParam endDate,
			@QueryParam("account") Set<UriParam> accountUris,
			@QueryParam("tag") Set<String> tagUris,
			@QueryParam("merchant") Set<String> merchantNames,
			@QueryParam("amount") BigDecimal amount,
			@QueryParam("query") String query) {
		
		final List<Account> accounts = getAccounts(user, accountUris);
		final TxactionList txactions = filterTxactions(
				accounts,
				getTxactions(accounts, startDate, endDate),
				currency, uneditedOnly, limit, offset,
				tagUris, merchantNames, amount, query
		);

		return presenter.present(txactions, locale);
	}

	private TxactionList filterTxactions(List<Account> accounts,
			List<Txaction> txactions, CurrencyParam currency, BooleanParam uneditedOnly,
			IntegerParam limit, IntegerParam offset, Set<String> tagUris,
			Set<String> merchantNames, BigDecimal amount, String query) {
		final TxactionListBuilder txactionListBuilder = builderProvider.get();
		txactionListBuilder.setAccounts(accounts);
		txactionListBuilder.setUnedited(uneditedOnly.getValue());
		txactionListBuilder.setTags(getTags(tagUris));
		txactionListBuilder.setCurrency(currency.getValue());

		if (!merchantNames.isEmpty()) {
			txactionListBuilder.setMerchantNames(merchantNames);
		}
		if (limit != null) {
			txactionListBuilder.setLimit(limit.getValue());
		}
		if (offset != null) {
			txactionListBuilder.setOffset(offset.getValue());
		}
		if (amount != null) {
			txactionListBuilder.setAmount(amount);
		}
		if (query != null) {
			txactionListBuilder.setQuery(query);
		}
		
		final TxactionList filteredTxactions = txactionListBuilder.build(txactions);
		return filteredTxactions;
	}

	private Set<Tag> getTags(Set<String> tagUris) {
		final Builder<Tag> tags = ImmutableSet.builder();
		final String uriPrefix = "/tags/";
		if (!tagUris.isEmpty()) {
			for (String uriParam : tagUris) {
				if (uriParam.startsWith(uriPrefix)) {
					tags.add(new Tag(uriParam.substring(uriPrefix.length())));
				}
			}
		}
		
		return tags.build();
	}

	private List<Txaction> getTxactions(List<Account> accounts, ISODateParam startDate,
			ISODateParam endDate) {
		final List<Txaction> txactions;
		if ((startDate != null) && (endDate != null)) {
			try {
				final Interval dateRange = new Interval(startDate.getValue(), endDate.getValue());
				txactions = txactionDAO.findTxactionsInDateRange(accounts, dateRange);
			} catch (IllegalArgumentException e) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
		} else if (startDate != null) {
			txactions = txactionDAO.findTxactionsAfterDate(accounts, startDate.getValue());
		} else if (endDate != null) {
			txactions = txactionDAO.findTxactionsBeforeDate(accounts, endDate.getValue());
		} else {
			txactions = txactionDAO.findTxactions(accounts);
		}
		return txactions;
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

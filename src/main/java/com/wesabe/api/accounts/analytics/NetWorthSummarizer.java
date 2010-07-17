package com.wesabe.api.accounts.analytics;

import java.util.Collection;
import java.util.Currency;
import java.util.NavigableMap;
import java.util.Set;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.entities.TxactionListItem;
import com.wesabe.api.util.money.Money;

public class NetWorthSummarizer {
	private final Provider<TxactionListBuilder> listBuilderProvider;
	
	@Inject
	public NetWorthSummarizer(Provider<TxactionListBuilder> listBuilderProvider) {
		this.listBuilderProvider = listBuilderProvider;
	}
	
	public ImmutableMap<Interval, Money> summarize(Collection<Account> accounts, Collection<Txaction> txactions,
		Interval dateRange, IntervalType intervalType, Currency currency, Set<Tag> filteredTags) {
		
		final TxactionListBuilder txactionListBuilder = listBuilderProvider.get();
		txactionListBuilder.setAccounts(accounts);
		txactionListBuilder.setCurrency(currency);
		txactionListBuilder.setCalculateBalances(true);
		
		final TxactionList txactionList = txactionListBuilder.build(txactions);
		final NavigableMap<DateTime, Money> balancesByDate = Maps.newTreeMap();
		for (TxactionListItem listItem : txactionList) {
			balancesByDate.put(listItem.getTxaction().getDatePosted(), listItem.getBalance());
		}
		
		final ImmutableMap.Builder<Interval, Money> builder = ImmutableMap.builder();
		for (Interval interval : intervalType.getIntervals(dateRange)) {
			final Entry<DateTime, Money> entry = balancesByDate.floorEntry(interval.getEnd());
			if (entry != null) {
				builder.put(interval, entry.getValue());
			} else {
				builder.put(interval, Money.zero(currency));
			}
		}
		
		return builder.build();
	}
}

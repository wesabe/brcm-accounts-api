package com.wesabe.api.accounts.analytics;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.ProvidedBy;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.entities.InvestmentSecurity;
import com.wesabe.api.accounts.entities.InvestmentTxaction;
import com.wesabe.api.accounts.entities.InvestmentTxactionList;
import com.wesabe.api.accounts.entities.InvestmentTxactionListItem;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@ProvidedBy(InvestmentTxactionListBuilderProvider.class)
public class InvestmentTxactionListBuilder {
	private Collection<InvestmentAccount> accounts = ImmutableSet.of();
	private Collection<String> investmentSecurityNames = ImmutableSet.of();
	private int offset = 0;
	private int limit = 0;
	private CurrencyExchangeRateMap exchangeRateMap;
	
	public InvestmentTxactionList build(Collection<InvestmentTxaction> investmentTxactions) {
		InvestmentTxactionList investmentTxactionList = new InvestmentTxactionList();
		
		// remove disabled, deleted, etc, sort them in reverse chronological order
		final List<InvestmentTxaction> resultTxactions = sort(filter(investmentTxactions));
		
		// set the total count to all the ones that could ever be shown
		investmentTxactionList.setTotalCount(resultTxactions.size());
		
		if (resultTxactions.isEmpty()) {
			return investmentTxactionList;
		}
		
		// hack of the end of the list since they are not useful in balance calculation
		applyLimit(resultTxactions);

		for (InvestmentTxaction investmentTxaction : resultTxactions) {
			investmentTxactionList.add(new InvestmentTxactionListItem(investmentTxaction));
		}
		
		// hack off the front of the list we were asked to hide
		applyOffset(investmentTxactionList);
		
		return investmentTxactionList;
	}

	/**
	 * Removes elements from the end of {@code list} starting at
	 * {@code offset + limit} and going to the end of the list.
	 * 
	 * Must be used before {@code applyOffset}, since this accounts for offset.
	 */
	private void applyLimit(List<?> list) {
		if ((limit != 0) && ((offset + limit) <= list.size())) {
			list.subList(offset+limit, list.size()).clear();
		}
	}

	/**
	 * Removes elements from {@code list} up to the offset index. If the offset
	 * index is greater than that of the last element in the list, all elements
	 * are removed.
	 * 
	 * Must be used after {@code applyLimit}, since that accounts for offset.
	 */
	private void applyOffset(List<?> list) {
		if (offset != 0) {
			list.subList(0, Math.min(offset, list.size())).clear();
		}
	}

	/**
	 * Sorts the {@link InvestmentTxaction} list in place and reverses it (to get newest
	 * first).
	 */
	private List<InvestmentTxaction> sort(List<InvestmentTxaction> investmentTxactions) {
		Collections.sort(investmentTxactions);
		Collections.reverse(investmentTxactions);
		return investmentTxactions;
	}

	private List<InvestmentTxaction> filter(Collection<InvestmentTxaction> investmentTxactions) {
		List<InvestmentTxaction> filteredTxactions = filterHiddenTxactions(investmentTxactions);
		
		if (!accounts.isEmpty()) {
			filteredTxactions = filterByAccounts(filteredTxactions);
		}
		
		if (!investmentSecurityNames.isEmpty()) {
			filteredTxactions = filterByInvestmentSecurities(filteredTxactions);
		}
				
		return filteredTxactions;
	}
	
	private List<InvestmentTxaction> filterByInvestmentSecurities(List<InvestmentTxaction> investmentTxactions) {
		return Lists.newArrayList(
			Iterables.filter(investmentTxactions, new Predicate<InvestmentTxaction>() {
				@Override
				public boolean apply(InvestmentTxaction investmentTxaction) {
					final InvestmentSecurity investmentSecurity = investmentTxaction.getInvestmentSecurity();
					return (investmentSecurity != null) && investmentSecurityNames.contains(investmentSecurity.getName());
				}
			})
		);
	}

	private List<InvestmentTxaction> filterHiddenTxactions(Collection<InvestmentTxaction> investmentTxactions) {
		return Lists.newArrayList(
			Iterables.filter(investmentTxactions, new Predicate<InvestmentTxaction>() {
				@Override
				public boolean apply(InvestmentTxaction investmentTxaction) {
					return !investmentTxaction.isDeleted() && !investmentTxaction.isDisabled();
				}
			})
		);
	}

	private List<InvestmentTxaction> filterByAccounts(List<InvestmentTxaction> investmentTxactions) {
		return Lists.newArrayList(
			Iterables.filter(investmentTxactions, new Predicate<InvestmentTxaction>() {
				@Override
				public boolean apply(InvestmentTxaction investmentTxaction) {
					return accounts.contains(investmentTxaction.getAccount());
				}
			})
		);
	}
	
	public InvestmentTxactionListBuilder setInvestmentSecurityNames(Collection<String> investmentSecurityNames) {
		this.investmentSecurityNames = ImmutableSet.copyOf(investmentSecurityNames);
		return this;
	}
	
	public InvestmentTxactionListBuilder setAccounts(Collection<InvestmentAccount> accounts) {
		this.accounts = ImmutableSet.copyOf(accounts);
		return this;
	}
	
	public InvestmentTxactionListBuilder setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public InvestmentTxactionListBuilder setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public InvestmentTxactionListBuilder setCurrencyExchangeRateMap(CurrencyExchangeRateMap exchangeRateMap) {
		this.exchangeRateMap = exchangeRateMap;
		return this;
	}

	public CurrencyExchangeRateMap getCurrencyExchangeRateMap() {
		return exchangeRateMap;
	}
}

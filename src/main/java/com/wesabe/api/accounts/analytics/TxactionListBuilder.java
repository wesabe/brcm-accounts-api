package com.wesabe.api.accounts.analytics;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.ProvidedBy;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Merchant;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.entities.TxactionListItem;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

@ProvidedBy(TxactionListBuilderProvider.class)
public class TxactionListBuilder {
	private Collection<Account> accounts = ImmutableSet.of();
	private Collection<Tag> tags = ImmutableSet.of();
	private Collection<String> merchantNames = ImmutableSet.of();
	private boolean unedited = false;
	private boolean calculateBalances = true;
	private int offset = 0;
	private int limit = 0;
	private Currency currency;
	private CurrencyExchangeRateMap exchangeRateMap;
	
	public TxactionList build(Collection<Txaction> txactions) {
		TxactionList txactionList = new TxactionList();
		
		// remove disabled, deleted, etc, sort them in reverse chronological order
		final List<Txaction> resultTxactions = sort(filter(txactions));
		
		// set the total count to all the ones that could ever be shown
		txactionList.setTotalCount(resultTxactions.size());
		
		if (resultTxactions.isEmpty()) {
			return txactionList;
		}
		
		// hack of the end of the list since they are not useful in balance calculation
		applyLimit(resultTxactions);

		for (Txaction txaction : resultTxactions) {
			txactionList.add(new TxactionListItem(txaction));
		}
		
		if (tags.isEmpty() && merchantNames.isEmpty() && calculateBalances) {
			// calculate balances before removing the offset as those Txactions may affect the balances
			txactionList.calculateRunningTotalBalances(accounts, currency, exchangeRateMap);
		}
		
		// hack off the front of the list we were asked to hide
		applyOffset(txactionList);
		
		return txactionList;
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
	 * Sorts the {@link Txaction} list in place and reverses it (to get newest
	 * first).
	 */
	private List<Txaction> sort(List<Txaction> txactions) {
		Collections.sort(txactions);
		Collections.reverse(txactions);
		return txactions;
	}

	private List<Txaction> filter(Collection<Txaction> txactions) {
		List<Txaction> filteredTxactions = filterHiddenTxactions(txactions);
		
		if (!accounts.isEmpty()) {
			filteredTxactions = filterByAccounts(filteredTxactions);
		}
		
		if (!merchantNames.isEmpty()) {
			filteredTxactions = filterByMerchants(filteredTxactions);
		}
		
		if (!tags.isEmpty()) {
			filteredTxactions = filterByTags(filteredTxactions);
		}
		
		if (unedited) {
			filteredTxactions = filterByUnedited(filteredTxactions);
		}
		
		return filteredTxactions;
	}
	
	private List<Txaction> filterByMerchants(List<Txaction> txactions) {
		return Lists.newArrayList(
			Iterables.filter(txactions, new Predicate<Txaction>() {
				@Override
				public boolean apply(Txaction txaction) {
					final Merchant merchant = txaction.getMerchant();
					return (merchant != null) && merchantNames.contains(merchant.getName());
				}
			})
		);
	}

	private List<Txaction> filterHiddenTxactions(Collection<Txaction> txactions) {
		return Lists.newArrayList(
			Iterables.filter(txactions, new Predicate<Txaction>() {
				@Override
				public boolean apply(Txaction txaction) {
					return !txaction.isDeleted() && !txaction.isDisabled();
				}
			})
		);
	}

	private List<Txaction> filterByAccounts(List<Txaction> txactions) {
		return Lists.newArrayList(
			Iterables.filter(txactions, new Predicate<Txaction>() {
				@Override
				public boolean apply(Txaction txaction) {
					return accounts.contains(txaction.getAccount());
				}
			})
		);
	}

	private List<Txaction> filterByTags(List<Txaction> txactions) {
		return Lists.newArrayList(
			Iterables.filter(txactions, new Predicate<Txaction>() {
				@Override
				public boolean apply(Txaction txaction) {
					return Iterables.any(txaction.getTaggedAmounts(), new Predicate<TaggedAmount>() {
						@Override
						public boolean apply(TaggedAmount taggedAmount) {
							return tags.contains(taggedAmount.getTag());
						}
					});
				}
			})
		);
	}
	
	private List<Txaction> filterByUnedited(List<Txaction> txactions) {
		return Lists.newArrayList(
			Iterables.filter(txactions, new Predicate<Txaction>() {
				@Override
				public boolean apply(Txaction txaction) {
					return txaction.isUnedited();
				}
			})
		);
	}
	
	public TxactionListBuilder setMerchantNames(Collection<String> merchantNames) {
		this.merchantNames = ImmutableSet.copyOf(merchantNames);
		return this;
	}
	
	public TxactionListBuilder setAccounts(Collection<Account> accounts) {
		this.accounts = ImmutableSet.copyOf(accounts);
		return this;
	}
	
	public TxactionListBuilder setTags(Collection<Tag> tags) {
		this.tags = tags;
		return this;
	}

	public TxactionListBuilder setUnedited(boolean flag) {
		this.unedited = flag;
		return this;
	}
	
	public TxactionListBuilder setCalculateBalances(boolean calculateBalances) {
		this.calculateBalances = calculateBalances;
		return this;
	}

	public TxactionListBuilder setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public TxactionListBuilder setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public TxactionListBuilder setCurrency(Currency currency) {
		this.currency = currency;
		return this;
	}

	public TxactionListBuilder setCurrencyExchangeRateMap(CurrencyExchangeRateMap exchangeRateMap) {
		this.exchangeRateMap = exchangeRateMap;
		return this;
	}

	public CurrencyExchangeRateMap getCurrencyExchangeRateMap() {
		return exchangeRateMap;
	}
}

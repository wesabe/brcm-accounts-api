package com.wesabe.api.accounts.entities;

import java.util.Collection;
import java.util.Currency;
import java.util.List;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;

public class TxactionList extends ForwardingList<TxactionListItem> {
	private static final long serialVersionUID = 3619821041312882849L;
	private int totalCount = 0;
	private final List<TxactionListItem> items;

	public TxactionList(TxactionListItem... items) {
		this.items = Lists.newArrayList(items);
	}
	
	public TxactionList(Txaction... txactions) {
		this.items = Lists.newArrayListWithCapacity(txactions.length);
		for (Txaction txaction : txactions) {
			items.add(new TxactionListItem(txaction));
		}
	}
	
	public TxactionList() {
		this.items = Lists.newArrayList();
	}
	
	public void calculateRunningTotalBalances(Collection<Account> accounts, Currency currency, CurrencyExchangeRateMap exchangeRateMap) {
		final TxactionBalanceCalculator calculator = new TxactionBalanceCalculator(accounts, getTxactions(), currency, exchangeRateMap);
		for (TxactionListItem item : items) {
			item.setBalance(calculator.getBalance(item.getTxaction()));
		}
	}

	public List<Txaction> getTxactions() {
		final ImmutableList.Builder<Txaction> txactions = ImmutableList.builder();
		for (TxactionListItem item : items) {
			txactions.add(item.getTxaction());
		}
		return txactions.build();
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	protected List<TxactionListItem> delegate() {
		return items;
	}
}
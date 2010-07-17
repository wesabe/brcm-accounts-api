package com.wesabe.api.accounts.entities;

import java.util.List;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class InvestmentTxactionList extends ForwardingList<InvestmentTxactionListItem> {
	private static final long serialVersionUID = 3619821041312882849L;
	private int totalCount = 0;
	private final List<InvestmentTxactionListItem> items;

	public InvestmentTxactionList(InvestmentTxactionListItem... items) {
		this.items = Lists.newArrayList(items);
	}
	
	public InvestmentTxactionList(InvestmentTxaction... txactions) {
		this.items = Lists.newArrayListWithCapacity(txactions.length);
		for (InvestmentTxaction txaction : txactions) {
			items.add(new InvestmentTxactionListItem(txaction));
		}
	}
	
	public InvestmentTxactionList() {
		this.items = Lists.newArrayList();
	}

	public List<InvestmentTxaction> getInvestmentTxactions() {
		final ImmutableList.Builder<InvestmentTxaction> txactions = ImmutableList.builder();
		for (InvestmentTxactionListItem item : items) {
			txactions.add(item.getInvestmentTxaction());
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
	protected List<InvestmentTxactionListItem> delegate() {
		return items;
	}
}
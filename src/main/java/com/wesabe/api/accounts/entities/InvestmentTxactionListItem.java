package com.wesabe.api.accounts.entities;

import com.google.common.base.Objects;

public class InvestmentTxactionListItem {
	private InvestmentTxaction txaction;
	
	public InvestmentTxactionListItem() {
	}
	
	public InvestmentTxactionListItem(InvestmentTxaction txaction) {
		this.txaction = txaction;
	}
	
	public InvestmentTxaction getInvestmentTxaction() {
		return txaction;
	}
	
	public void setInvestmentTxaction(InvestmentTxaction txaction) {
		this.txaction = txaction;
	}
	
	@Override
	public String toString() {
		return String.format("(%s)",getInvestmentTxaction());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InvestmentTxactionListItem) {
			final InvestmentTxactionListItem other = (InvestmentTxactionListItem) obj;
			return Objects.equal(getInvestmentTxaction(), other.getInvestmentTxaction());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getInvestmentTxaction());
	}
}

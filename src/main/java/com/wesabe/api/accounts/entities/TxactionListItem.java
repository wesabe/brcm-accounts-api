package com.wesabe.api.accounts.entities;

import com.google.common.base.Objects;
import com.wesabe.api.util.money.Money;

public class TxactionListItem {
	private Txaction txaction;
	private Money balance;
	
	public TxactionListItem() {
	}
	
	public TxactionListItem(Txaction txaction) {
		this.txaction = txaction;
	}
	
	public TxactionListItem(Txaction txaction, Money balance) {
		this.txaction = txaction;
		this.balance = balance;
	}
	
	public Txaction getTxaction() {
		return txaction;
	}
	
	public void setTxaction(Txaction txaction) {
		this.txaction = txaction;
	}
	
	public Money getBalance() {
		return balance;
	}
	
	public void setBalance(Money balance) {
		this.balance = balance;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", getBalance(), getTxaction());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TxactionListItem) {
			final TxactionListItem other = (TxactionListItem) obj;
			return Objects.equal(getTxaction(), other.getTxaction()) && Objects.equal(getBalance(), other.getBalance());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getTxaction(), getBalance());
	}
}

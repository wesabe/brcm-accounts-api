package com.wesabe.api.accounts.entities;

import com.google.common.base.Objects;

public class AccountGroup {
	private final String name;
	private final String id;
	private final AccountList accounts;

	public AccountGroup(String name, String id, AccountList accounts) {
		this.name = name;
		this.id = id;
		this.accounts = accounts;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public AccountList getAccounts() {
		return accounts;
	}
	
	@Override
	public String toString() {
		return String.format("(%s:%s %s)", getId(), getName(), getAccounts());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AccountGroup) {
			final AccountGroup other = (AccountGroup) obj;
			return Objects.equal(name, other.name)
						&& Objects.equal(id, other.id)
						&& Objects.equal(accounts, other.accounts);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(name, id, accounts);
	}
}

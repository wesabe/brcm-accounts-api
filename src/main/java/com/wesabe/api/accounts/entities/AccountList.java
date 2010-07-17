package com.wesabe.api.accounts.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.Money;

public class AccountList extends ForwardingList<Account> {
	private static final long serialVersionUID = -4885233195234065520L;
	private final List<Account> accounts;
	
	public AccountList() {
		this.accounts = Lists.newArrayList();
	}
	
	public AccountList(Collection<Account> accounts) {
		this.accounts = Lists.newArrayList(accounts);
	}

	public AccountList(Account... accounts) {
		this.accounts = Lists.newArrayList(accounts);
	}
	
	@Override
	protected List<Account> delegate() {
		return accounts;
	}
	
	public Money getTotal(Currency currency, CurrencyExchangeRateMap exchangeRates) {
		final DateTime now = new DateTime();
		Money total = Money.zero(currency);
		for (Account account : accounts) {
			if (account.hasBalance() && account.isActive()) {
				final Money balance = account.getBalance();
				if (balance != null) {
					total = total.add(balance.convert(exchangeRates, currency, now));
				}
				
			}
		}
		return total;
	}

	/**
	 * Returns a list of {@link AccountGroup}s containing {@link Account}s of
	 * like types. Long-term this method probably shouldn't exist since we'd
	 * like to allow users to create their own groups.
	 * 
	 * @return a list of account groups containing like types
	 */
	public List<AccountGroup> getAccountGroups() {
		// We use higher order groupings for display, so some account types are
		// merged together into groups.
		Map<AccountType, AccountList> accountsByType = Maps.newHashMap();
		
		// Cash = Cash + Manual
		accountsByType.put(AccountType.CASH, new AccountList());
		accountsByType.put(AccountType.MANUAL, accountsByType.get(AccountType.CASH));
		// Checking = Checking
		accountsByType.put(AccountType.CHECKING, new AccountList());
		// Credit = Credit Card + Credit Line
		accountsByType.put(AccountType.CREDIT_CARD, new AccountList());
		accountsByType.put(AccountType.CREDIT_LINE, accountsByType.get(AccountType.CREDIT_CARD));
		// Savings = Savings + Money Market + Certificate
		accountsByType.put(AccountType.SAVINGS, new AccountList());
		accountsByType.put(AccountType.MONEY_MARKET, accountsByType.get(AccountType.SAVINGS));
		accountsByType.put(AccountType.CERTIFICATE, accountsByType.get(AccountType.SAVINGS));		
		// Investments = Investment + Brokerage
		accountsByType.put(AccountType.INVESTMENT, new AccountList());
		accountsByType.put(AccountType.BROKERAGE, accountsByType.get(AccountType.INVESTMENT));
		// Loans = Loan + Mortgage
		accountsByType.put(AccountType.LOAN, new AccountList());
		accountsByType.put(AccountType.MORTGAGE, accountsByType.get(AccountType.LOAN));		
		// Unknown = Unknown
		accountsByType.put(AccountType.UNKNOWN, new AccountList());
		
		AccountList archivedAccounts = new AccountList();
		
		for (Account account : accounts) {
			if (account.isArchived()) {
				archivedAccounts.add(account);
			} else {
				accountsByType.get(account.getAccountType()).add(account);
			}
		}
		
		ArrayList<AccountGroup> accountGroups = Lists.newArrayList(
				new AccountGroup("Cash", 		"cash", 		accountsByType.get(AccountType.CASH)),
				new AccountGroup("Checking", 	"checking", 	accountsByType.get(AccountType.CHECKING)),
				new AccountGroup("Credit", 		"credit", 		accountsByType.get(AccountType.CREDIT_CARD)),
				new AccountGroup("Savings", 	"savings", 		accountsByType.get(AccountType.SAVINGS)),
				new AccountGroup("Investments", "investments", 	accountsByType.get(AccountType.INVESTMENT)),
				new AccountGroup("Loans",       "loans",        accountsByType.get(AccountType.LOAN)),
				new AccountGroup("Unknown", 	"unknown",		accountsByType.get(AccountType.UNKNOWN)),
				new AccountGroup("Archived",    "archived",     archivedAccounts)
		);
		
		return Lists.newArrayList(Iterables.filter(accountGroups, new Predicate<AccountGroup>() {
			@Override
			public boolean apply(AccountGroup group) {
				return !group.getAccounts().isEmpty();
			}
		}));
	}
}

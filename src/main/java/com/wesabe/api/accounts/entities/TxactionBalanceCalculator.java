package com.wesabe.api.accounts.entities;

import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.Money;

public class TxactionBalanceCalculator {
	private final Iterable<Txaction> txactions;
	private final Iterable<Account> accounts;
	private final Currency targetCurrency;
	private final CurrencyExchangeRateMap exchangeRates;
	private Map<Txaction, Money> balancesByTxaction = Maps.newHashMap();
	private Map<Account, AccountBalance> runningAccountBalances = Maps.newHashMap();
	
	public TxactionBalanceCalculator(Iterable<Account> accounts, Iterable<Txaction> txactions, Currency targetCurrency, CurrencyExchangeRateMap exchangeRates) {
		this.accounts = accounts;
		this.txactions = txactions;
		this.targetCurrency = targetCurrency;
		this.exchangeRates = exchangeRates;
		calculate();
	}

	public Money getBalance(Txaction txaction) {
		return balancesByTxaction.get(txaction);
	}

	private void calculate() {
		runningAccountBalances = calculateInitialBalancesByAccount();
		final List<Txaction> txactions = getSortedListOfTxactions();
		
		for (Txaction txaction : txactions) {
			calculateAndStoreTxactionBalance(txaction);
			if (txaction.getAccount().hasBalance()) {
				adjustBalanceForAccountGiven(txaction);
			}
		}
	}

	private Map<Account, AccountBalance> calculateInitialBalancesByAccount() {
		for (Account account : accounts) {
			if (account.hasBalance()) {
				DateTime balanceDate = account.getBalanceDate();
				if (balanceDate == null) {
					balanceDate = new DateTime();
				}
				
				final Money balance = account.getBalance();
				if (balance != null) {
					AccountBalance initialAccountBalance = new AccountBalance(
							account, balance, balanceDate);
					runningAccountBalances.put(account, initialAccountBalance);
				}
			}
		}
		return runningAccountBalances;
	}

	private List<Txaction> getSortedListOfTxactions() {
		List<Txaction> txactionList = Lists.newArrayList(txactions);
		
		// filter out anything with a null date
		txactionList = Lists.newArrayList(
			Iterables.filter(txactionList, new Predicate<Txaction>() {
				@Override
				public boolean apply(Txaction object) {
					return object.getDatePosted() != null;
				}
			})
		);
		
		// sort by reverse chronological order
		Collections.sort(txactionList, new Comparator<Txaction>() {
			@Override
			public int compare(Txaction t1, Txaction t2) {
				return t2.getDatePosted().compareTo(t1.getDatePosted());
			}
		});
		
		return txactionList;
	}

	private void calculateAndStoreTxactionBalance(Txaction txaction) {
		balancesByTxaction.put(txaction, calculateSumOfBalancesByAccount());
	}

	private Money calculateSumOfBalancesByAccount() {
		Money sum = Money.zero(targetCurrency);
		for (AccountBalance accountBalance : runningAccountBalances.values()) {
			sum = sum.add(accountBalance.getBalance().convert(exchangeRates, targetCurrency, accountBalance.getDate()));
		}
		return sum;
	}

	private void adjustBalanceForAccountGiven(Txaction txaction) {
		AccountBalance accountBalance = runningAccountBalances.get(txaction.getAccount());
		if (accountBalance != null) {
			final Money balance = accountBalance.getBalance();
			if (balance != null) {
				Money adjustedAmount = balance.subtract(txaction.getAmount());
				runningAccountBalances.put(txaction.getAccount(), new AccountBalance(
						accountBalance.getAccount(), adjustedAmount, txaction.getDatePosted()));
			}
		}
	}
}

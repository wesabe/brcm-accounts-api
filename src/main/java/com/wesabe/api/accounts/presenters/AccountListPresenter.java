package com.wesabe.api.accounts.presenters;

import java.util.Currency;
import java.util.Locale;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.entities.AccountGroup;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link AccountList} instances.
 * 
 * @author coda
 *
 */
public class AccountListPresenter {
	private final CurrencyExchangeRateMap exchangeRateMap;
	private final MoneyPresenter moneyPresenter;
	private final AccountPresenter accountPresenter;
	private final InvestmentAccountPresenter investmentAccountPresenter;
	private final AccountGroupPresenter accountGroupPresenter;

	@Inject
	public AccountListPresenter(CurrencyExchangeRateMap exchangeRateMap,
			MoneyPresenter moneyPresenter, AccountPresenter accountPresenter,
			InvestmentAccountPresenter investmentAccountPresenter,
			AccountGroupPresenter accountGroupPresenter) {
		this.exchangeRateMap = exchangeRateMap;
		this.moneyPresenter = moneyPresenter;
		this.accountPresenter = accountPresenter;
		this.investmentAccountPresenter = investmentAccountPresenter;
		this.accountGroupPresenter = accountGroupPresenter;
	}

	public XmlsonObject present(AccountList accountList, Currency currency, Locale locale) {
		final XmlsonObject root = new XmlsonObject("account-list");

		final XmlsonArray accounts = new XmlsonArray("accounts");
		for (Account account : accountList) {
			final XmlsonObject accountNode;
			if (account instanceof InvestmentAccount) {
				accountNode = investmentAccountPresenter.present((InvestmentAccount)account, locale);
			} else {
				accountNode = accountPresenter.present(account, locale);				
			}
			accounts.add(accountNode);
		}
		root.add(accounts);

		final XmlsonArray groups = new XmlsonArray("account-groups");
		for (AccountGroup group : accountList.getAccountGroups()) {
			groups.add(accountGroupPresenter.present(group, currency, locale));
		}
		root.add(groups);

		root.add(
			moneyPresenter.present(
				"total",
				accountList.getTotal(currency, exchangeRateMap),
				locale
			)
		);

		return root;
	}
}

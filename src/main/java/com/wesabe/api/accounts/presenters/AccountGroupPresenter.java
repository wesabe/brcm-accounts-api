package com.wesabe.api.accounts.presenters;

import java.util.Currency;
import java.util.Locale;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountGroup;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link AccountGroup} instances.
 * 
 * @author coda
 *
 */
public class AccountGroupPresenter {
	private final CurrencyExchangeRateMap exchangeRateMap;
	private final MoneyPresenter moneyPresenter;
	private final AccountReferencePresenter accountReferencePresenter;

	@Inject
	public AccountGroupPresenter(CurrencyExchangeRateMap exchangeRateMap,
			MoneyPresenter moneyPresenter, AccountReferencePresenter accountReferencePresenter) {
		this.exchangeRateMap = exchangeRateMap;
		this.moneyPresenter = moneyPresenter;
		this.accountReferencePresenter = accountReferencePresenter;
	}

	public XmlsonObject present(AccountGroup group, Currency currency, Locale locale) {
		final XmlsonObject root = new XmlsonObject("group");
		root.addProperty("name", group.getName());
		// REVIEW coda@wesabe.com -- May 21, 2009: Replace account group URI building once AccountGroupResource is written
		root.addProperty("uri", String.format("/account-groups/%s", group.getId()));
		
		final XmlsonArray accounts = new XmlsonArray("accounts");
		boolean hasTotal = false;
		for (Account account : group.getAccounts()) {
			accounts.add(accountReferencePresenter.present(account));
			if (account.hasBalance()) {
				hasTotal = true;
			}
		}
		root.add(accounts);

		if (hasTotal) {
			root.add(
				moneyPresenter.present(
					"total",
					group.getAccounts().getTotal(currency, exchangeRateMap),
					locale
				)
			);
		}

		return root;
	}
}

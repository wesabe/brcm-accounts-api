package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import org.joda.time.format.ISODateTimeFormat;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.AccountBalance;
import com.wesabe.xmlson.XmlsonObject;

public class AccountBalancePresenter {
	private final MoneyPresenter moneyPresenter;
	
	@Inject
	public AccountBalancePresenter(MoneyPresenter moneyPresenter) {
		this.moneyPresenter = moneyPresenter;
	}
	
	public XmlsonObject present(AccountBalance accountBalance, Locale locale) {
		final XmlsonObject result = new XmlsonObject("account-balance");
		
		result.addProperty("uri", String.format("/accounts/%d/balances/%d", accountBalance.getAccount().getRelativeId(), accountBalance.getId()));
		result.add(moneyPresenter.present("balance", accountBalance.getBalance(), locale));
		result.addProperty("date", ISODateTimeFormat.basicDate().print(accountBalance.getDate()));
		
		return result;
	}
}

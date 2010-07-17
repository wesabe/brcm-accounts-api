package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.FinancialInst;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link Account} instances.
 * 
 * @author coda
 *
 */
public class AccountPresenter {
	private static final DateTimeFormatter ISO_DATETIME = ISODateTimeFormat.basicDateTimeNoMillis();
	protected final MoneyPresenter moneyPresenter;
	protected final FinancialInstPresenter financialInstPresenter;
	
	@Inject
	public AccountPresenter(MoneyPresenter moneyPresenter, FinancialInstPresenter financialInstPresenter) {
		this.moneyPresenter = moneyPresenter;
		this.financialInstPresenter = financialInstPresenter;
	}

	public XmlsonObject present(Account account, Locale locale) {
		final XmlsonObject result = new XmlsonObject("account");
		result.addProperty("name", account.getName());
		result.addProperty("position", account.getPosition());
		// REVIEW coda@wesabe.com -- May 21, 2009: Replace account URI building once AccountResource is written
		result.addProperty("uri", String.format("/accounts/%d", account.getRelativeId()));
		result.addProperty("type", account.getAccountType().toString());
		result.addProperty("currency", account.getCurrency().getCurrencyCode());
		result.addProperty("status", account.getStatus().toString().toLowerCase(Locale.US));
		if (account.hasBalance()) {
			final Money balance = account.getBalance();
			result.add(moneyPresenter.present("balance", balance, locale));
			result.addProperty("last-balance-at", ISO_DATETIME.print(account.getLastActivityDate()));
		}
		FinancialInst financialInst = account.getFinancialInst();
		if (financialInst != null) {
			result.add(financialInstPresenter.present(financialInst));
		}
		return result;
	}

}

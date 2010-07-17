package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.InvestmentTxaction;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link InvestmentTxaction} instances.
 * 
 * @author coda
 *
 */
public class InvestmentTxactionPresenter {
	private static final DateTimeFormatter ISO_BASIC = ISODateTimeFormat.basicDate();
	private final MoneyPresenter moneyPresenter;
	private final AccountBriefPresenter accountPresenter;
	private final InvestmentSecurityPresenter investmentSecurityPresenter;

	@Inject
	public InvestmentTxactionPresenter(MoneyPresenter moneyPresenter, AccountBriefPresenter accountPresenter,
			InvestmentSecurityPresenter investmentSecurityPresenter) {
		this.moneyPresenter = moneyPresenter;
		this.accountPresenter = accountPresenter;
		this.investmentSecurityPresenter = investmentSecurityPresenter;
	}

	public XmlsonObject present(InvestmentTxaction investmentTxaction, Locale locale) {
		final XmlsonObject root = new XmlsonObject("investment-transaction");
		root.addProperty("id", investmentTxaction.getId());
		root.addProperty("uri", String.format("/investment-transactions/%d", investmentTxaction.getId()));
		root.add(accountPresenter.present(investmentTxaction.getAccount()));
		root.addProperty("trade-date", ISO_BASIC.print(investmentTxaction.getTradeDate()));
		root.addProperty("memo", investmentTxaction.getMemo());
		root.addProperty("units", investmentTxaction.getUnits());
		root.add(moneyPresenter.present("unit-price", investmentTxaction.getUnitPrice(), locale));
		Money total = investmentTxaction.getTotal();
		if (total != null) {
			root.add(moneyPresenter.present("total", investmentTxaction.getTotal(), locale));
		} else {
			root.addNullProperty("total");
		}

		if (investmentTxaction.getInvestmentSecurity() != null) {
			root.add(investmentSecurityPresenter.present(investmentTxaction.getInvestmentSecurity()));
		} else {
			root.addNullProperty("investment-security");
		}
		
		return root;
	}
}

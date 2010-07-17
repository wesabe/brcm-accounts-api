package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import com.wesabe.api.accounts.entities.InvestmentPosition;
import com.wesabe.xmlson.XmlsonObject;
import com.google.inject.Inject;

/**
 * A presenter for {@link InvestmentPosition} instances
 * 
 * @author brad
 *
 */
public class InvestmentPositionPresenter {
	private final MoneyPresenter moneyPresenter;
	private final InvestmentSecurityPresenter investmentSecurityPresenter;
	
	@Inject
	public InvestmentPositionPresenter(MoneyPresenter moneyPresenter, 
			InvestmentSecurityPresenter investmentSecurityPresenter) {
		this.moneyPresenter = moneyPresenter;
		this.investmentSecurityPresenter = investmentSecurityPresenter;
	}
	
	public XmlsonObject present(InvestmentPosition position, Locale locale) {
		final XmlsonObject root = new XmlsonObject("investment-position");
		if (position.getInvestmentSecurity() != null) {
			root.add(investmentSecurityPresenter.present(position.getInvestmentSecurity()));
		} else {
			root.addNullProperty("investment-security");
		}
		root.addProperty("units", position.getUnits());
		root.add(moneyPresenter.present("unit-price", position.getUnitPrice(), locale));
		root.add(moneyPresenter.present("market-value", position.getMarketValue(), locale));
		return root;
	}
}
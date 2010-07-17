package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.entities.InvestmentAccountBalance;
import com.wesabe.api.accounts.entities.InvestmentPosition;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link InvestmentAccount} instances.
 * 
 * @author coda
 *
 */
public class InvestmentAccountPresenter extends AccountPresenter {
	private final InvestmentPositionPresenter investmentPositionPresenter;
	
	@Inject
	public InvestmentAccountPresenter(MoneyPresenter moneyPresenter, InvestmentPositionPresenter investmentPositionPresenter, FinancialInstPresenter financialInstPresenter) {
		super(moneyPresenter, financialInstPresenter);
		this.investmentPositionPresenter = investmentPositionPresenter;
	}

	public XmlsonObject present(InvestmentAccount account, Locale locale) {
		final XmlsonObject result = super.present(account, locale);
		final XmlsonArray positions = new XmlsonArray("investment-positions");
		for (InvestmentPosition position : account.getCurrentInvestmentPositions()) {
			positions.add(investmentPositionPresenter.present(position, locale));
		}
		result.add(positions);
		
		result.add(moneyPresenter.present("market-value", account.getMarketValue(), locale));
		
		final InvestmentAccountBalance balance = account.getCurrentInvestmentAccountBalance();
		final String nodeName = "investment-balance";
		if (balance != null) {
			final XmlsonObject balanceNode = new XmlsonObject(nodeName);
			if (balance.getAvailableCash() != null) {
				balanceNode.add(moneyPresenter.present("available-cash", balance.getAvailableCash(), locale));
			}
			if (balance.getMarginBalance() != null) {
				balanceNode.add(moneyPresenter.present("margin-balance", balance.getMarginBalance(), locale));
			}
			if (balance.getShortBalance() != null) {
				balanceNode.add(moneyPresenter.present("short-balance", balance.getShortBalance(), locale));
			}
			if (balance.getBuyingPower() != null) {
				balanceNode.add(moneyPresenter.present("buying-power", balance.getBuyingPower(), locale));
			}
			result.add(balanceNode);
		} else {
			result.addNullProperty(nodeName);
		}
		return result;
	}

}

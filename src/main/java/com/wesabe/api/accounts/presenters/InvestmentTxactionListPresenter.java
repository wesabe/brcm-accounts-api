package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.InvestmentTxactionList;
import com.wesabe.api.accounts.entities.InvestmentTxactionListItem;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

public class InvestmentTxactionListPresenter {
	private final InvestmentTxactionPresenter investmentTxactionPresenter;

	@Inject
	public InvestmentTxactionListPresenter(MoneyPresenter moneyPresenter, InvestmentTxactionPresenter investmentTxactionPresenter) {
		this.investmentTxactionPresenter = investmentTxactionPresenter;
	}

	public XmlsonObject present(InvestmentTxactionList investmentTxactions, Locale locale) {
		final XmlsonObject root = new XmlsonObject("investment-transaction-list");

		root.add(new XmlsonObject("count").addProperty("total", investmentTxactions.getTotalCount()));

		final XmlsonArray list = new XmlsonArray("investment-transactions");
		for (InvestmentTxactionListItem item : investmentTxactions) {
			final XmlsonObject investmentTxaction = investmentTxactionPresenter.present(item.getInvestmentTxaction(), locale);
			list.add(investmentTxaction);
		}
		root.add(list);

		return root;
	}
}

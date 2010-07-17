package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.entities.TxactionListItem;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

public class TxactionListPresenter {
	private final MoneyPresenter moneyPresenter;
	private final TxactionPresenter txactionPresenter;

	@Inject
	public TxactionListPresenter(MoneyPresenter moneyPresenter, TxactionPresenter txactionPresenter) {
		this.moneyPresenter = moneyPresenter;
		this.txactionPresenter = txactionPresenter;
	}

	public XmlsonObject present(TxactionList txactions, Locale locale) {
		final XmlsonObject root = new XmlsonObject("transaction-list");

		root.add(new XmlsonObject("count").addProperty("total", txactions.getTotalCount()));

		final XmlsonArray list = new XmlsonArray("transactions");
		for (TxactionListItem item : txactions) {
			final XmlsonObject txaction = txactionPresenter.present(item.getTxaction(), locale);
			if (item.getBalance() != null) {
				txaction.add(moneyPresenter.present("balance", item.getBalance(), locale));
			}
			list.add(txaction);
		}
		root.add(list);

		return root;
	}
}

package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import com.google.inject.Inject;
import com.wesabe.api.accounts.analytics.SumOfMoney;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link SumOfMoney} instances.
 * 
 * @author coda
 *
 */
public class SumOfMoneyPresenter {
	private final MoneyPresenter moneyPresenter;
	
	@Inject
	public SumOfMoneyPresenter(MoneyPresenter moneyPresenter) {
		this.moneyPresenter = moneyPresenter;
	}
	
	public XmlsonObject present(String name, SumOfMoney sum, Locale locale) {
		final XmlsonObject root = moneyPresenter.present(name, sum.getAmount(), locale);
		root.addProperty("count", sum.getCount());
		return root;
	}
}

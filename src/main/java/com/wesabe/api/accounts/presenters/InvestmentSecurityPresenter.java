package com.wesabe.api.accounts.presenters;

import com.wesabe.api.accounts.entities.InvestmentSecurity;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link InvestmentSecurity}
 * 
 * @author brad
 *
 */
public class InvestmentSecurityPresenter {
	public XmlsonObject present(InvestmentSecurity security) {
		final XmlsonObject root = new XmlsonObject("investment-security");
		root.addProperty("name", security.getName());
		if (security.getDisplayName() != null) {
			root.addProperty("display-name", security.getDisplayName());
		}
		root.addProperty("ticker", security.getTicker());
		root.addProperty("id", security.getId());
		root.addProperty("uri", String.format("/investment-transactions/investment-security/%d", security.getId()));
		return root;
	}
}

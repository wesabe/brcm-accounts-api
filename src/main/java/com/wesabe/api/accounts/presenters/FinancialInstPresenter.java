package com.wesabe.api.accounts.presenters;

import com.wesabe.api.accounts.entities.FinancialInst;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link FinancialInst} instances.
 *
 * @author brad
 *
 */
public class FinancialInstPresenter {
	public FinancialInstPresenter() {}

	public XmlsonObject present(FinancialInst financialInst) {
		final XmlsonObject result = new XmlsonObject("financial-institution");
		result.addProperty("name", financialInst.getName());
		result.addProperty("id", financialInst.getWesabeId());
		return result;
	}

}

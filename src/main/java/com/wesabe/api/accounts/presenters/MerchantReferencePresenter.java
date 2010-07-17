package com.wesabe.api.accounts.presenters;

import com.wesabe.api.accounts.entities.Merchant;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link Merchant} instances when only a reference, not a full
 * representation, is needed.
 * 
 * @author coda
 *
 */
public class MerchantReferencePresenter {
	public XmlsonObject present(Merchant merchant) {
		final XmlsonObject root = new XmlsonObject("merchant");
		root.addProperty("name", merchant.getName());
		root.addProperty("id", merchant.getId());
		root.addProperty("uri", String.format("/transactions/merchant/%d", merchant.getId()));
		return root;
	}
}

package com.wesabe.api.accounts.presenters;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link Account} instances when only a reference, not a full
 * representation, is needed
 * 
 * @author coda
 *
 */
public class AccountReferencePresenter {
	public XmlsonObject present(Account account) {
		// REVIEW coda@wesabe.com -- May 21, 2009: Replace account URI building once AccountResource is written
		final XmlsonObject root = new XmlsonObject("account");
		root.addProperty("uri", String.format("/accounts/%d", account.getRelativeId()));
		return root;
	}
}

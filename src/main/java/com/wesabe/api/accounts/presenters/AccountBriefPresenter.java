package com.wesabe.api.accounts.presenters;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link Account} instances where more information is needed
 * than {@link AccountReferencePresenter}, but less than
 * {@link AccountPresenter}.
 * 
 * @author coda
 *
 */
public class AccountBriefPresenter {
	public XmlsonObject present(Account account) {
		final XmlsonObject root = new XmlsonObject("account");
		root.addProperty("id", account.getRelativeId());
		root.addProperty("uri", String.format("/accounts/%d", account.getRelativeId()));
		root.addProperty("type", account.getAccountType().toString());
		return root;
	}
}

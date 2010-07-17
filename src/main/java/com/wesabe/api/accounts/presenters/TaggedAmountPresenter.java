package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link TaggedAmount} instances.
 * 
 * @author coda
 *
 */
public class TaggedAmountPresenter {
	private final MoneyPresenter moneyPresenter;
	
	@Inject
	public TaggedAmountPresenter(MoneyPresenter moneyPresenter) {
		this.moneyPresenter = moneyPresenter;
	}
	
	public XmlsonObject present(TaggedAmount taggedAmount, Locale locale) {
		final XmlsonObject root = new XmlsonObject("tag");
		root.addProperty("name", taggedAmount.getTag().toString());
		root.addProperty("uri", String.format("/tags/%s", taggedAmount.getTag()));
		if (taggedAmount.isSplit()) {
			root.add(moneyPresenter.present("amount", taggedAmount.getAmount(), locale));
		}
		return root;
	}
	
}

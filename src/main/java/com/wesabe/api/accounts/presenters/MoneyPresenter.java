package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import com.wesabe.api.util.money.Money;

import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link Money} instances.
 * 
 * @author coda
 *
 */
public class MoneyPresenter {
	private static final String DISPLAY_KEY = "display";
	private static final String VALUE_KEY = "value";
	
	/**
	 * Returns an {@link XmlsonObject} describing a {@link Money} in a
	 * particular {@link Locale}.
	 * 
	 * @param name the name of the {@link XmlsonObject}
	 * @param amount the amount
	 * @param locale the {@link Locale} for which the representation should be formatted
	 * @return an {@link XmlsonObject}
	 */
	public XmlsonObject present(String name, Money amount, Locale locale) {
		final XmlsonObject root = new XmlsonObject(name);
		root.addProperty(DISPLAY_KEY, amount.toString(locale));
		root.addProperty(VALUE_KEY, amount.toPlainString());
		return root;
	}
}

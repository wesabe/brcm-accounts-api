package com.wesabe.api.accounts.presenters;

import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@code ImmutableMap<Tag, MonetarySummary>} instances.
 * 
 * @author coda
 *
 */
public class TagSummaryPresenter {
	private final SumOfMoneyPresenter sumOfMoneyPresenter;
	
	@Inject
	public TagSummaryPresenter(SumOfMoneyPresenter moneyPresenter) {
		this.sumOfMoneyPresenter = moneyPresenter;
	}
	
	public XmlsonObject present(ImmutableMap<Tag, MonetarySummary> summaries, Locale locale) {
		final XmlsonObject root = new XmlsonObject("tag-summary");
		final XmlsonArray array = new XmlsonArray("summaries");
		
		for (Map.Entry<Tag, MonetarySummary> summary : summaries.entrySet()) {
			final XmlsonObject item = new XmlsonObject("summary");
			
			final XmlsonObject tag = new XmlsonObject("tag");
			tag.addProperty("name", summary.getKey().toString());
			item.add(tag);
			item.add(sumOfMoneyPresenter.present("spending", summary.getValue().getSpending(), locale));
			item.add(sumOfMoneyPresenter.present("earnings", summary.getValue().getEarnings(), locale));
			item.add(sumOfMoneyPresenter.present("net", summary.getValue().getNet(), locale));
			
			array.add(item);
			
		}
		root.add(array);
		
		return root;
	}
}

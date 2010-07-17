package com.wesabe.api.accounts.presenters;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.wesabe.api.accounts.analytics.MonetarySummary;
import com.wesabe.api.accounts.analytics.MonetarySummaryWithSplits;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@code ImmutableMap<Tag, MonetarySummary> instances}.
 * 
 * @author coda
 *
 */
public class IntervalSummaryPresenter {
	private static final DateTimeFormatter ISO_BASIC = ISODateTimeFormat.basicDate();
	private final SumOfMoneyPresenter sumOfMoneyPresenter;
	
	@Inject
	public IntervalSummaryPresenter(SumOfMoneyPresenter moneyPresenter) {
		this.sumOfMoneyPresenter = moneyPresenter;
	}
	
	public XmlsonObject present(ImmutableMap<Interval, MonetarySummaryWithSplits> summaries, Locale locale) {
		final XmlsonObject root = new XmlsonObject("interval-summary");
		final XmlsonArray array = new XmlsonArray("summaries");
		
		for (Map.Entry<Interval, MonetarySummaryWithSplits> summary : summaries.entrySet()) {
			final XmlsonObject item = new XmlsonObject("summary");
			
			final XmlsonObject interval = new XmlsonObject("interval");
			interval.addProperty("start", ISO_BASIC.print(summary.getKey().getStart()));
			interval.addProperty("end", ISO_BASIC.print(summary.getKey().getEnd()));
			item.add(interval);
			item.add(sumOfMoneyPresenter.present("spending", summary.getValue().getSpending(), locale));
			item.add(sumOfMoneyPresenter.present("earnings", summary.getValue().getEarnings(), locale));
			item.add(sumOfMoneyPresenter.present("net", summary.getValue().getNet(), locale));
			
			final XmlsonArray splits = new XmlsonArray("splits");
			for (Entry<Tag, MonetarySummary> entry : summary.getValue().getSplitSummaries().entrySet()) {
				final XmlsonObject splitsSummary = new XmlsonObject("split");
				
				final XmlsonObject tagName = new XmlsonObject("tag");
				tagName.addProperty("name", entry.getKey().toString());
				splitsSummary.add(tagName);
				
				splitsSummary.add(sumOfMoneyPresenter.present("spending", entry.getValue().getSpending(), locale));
				splitsSummary.add(sumOfMoneyPresenter.present("earnings", entry.getValue().getEarnings(), locale));
				splitsSummary.add(sumOfMoneyPresenter.present("net", entry.getValue().getNet(), locale));
				splits.add(splitsSummary);
			}
			item.add(splits);
			
			array.add(item);
		}
		root.add(array);
		
		return root;
	}
}

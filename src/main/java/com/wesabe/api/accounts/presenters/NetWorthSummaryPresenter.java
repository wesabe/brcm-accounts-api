package com.wesabe.api.accounts.presenters;

import java.util.Locale;
import java.util.Map;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

public class NetWorthSummaryPresenter {
	private static final DateTimeFormatter ISO_BASIC = ISODateTimeFormat.basicDate();
	private final MoneyPresenter moneyPresenter;
	
	
	@Inject
	public NetWorthSummaryPresenter(MoneyPresenter moneyPresenter) {
		this.moneyPresenter = moneyPresenter;
	}
	
	public XmlsonObject present(ImmutableMap<Interval, Money> summaries, Locale locale) {
		final XmlsonObject root = new XmlsonObject("net-worth-summary");
		final XmlsonArray array = new XmlsonArray("summaries");
		
		for (Map.Entry<Interval, Money> summary : summaries.entrySet()) {
			final XmlsonObject item = new XmlsonObject("summary");
			
			final XmlsonObject interval = new XmlsonObject("interval");
			interval.addProperty("start", ISO_BASIC.print(summary.getKey().getStart()));
			interval.addProperty("end", ISO_BASIC.print(summary.getKey().getEnd()));
			item.add(interval);
			item.add(moneyPresenter.present("balance", summary.getValue(), locale));
			
			array.add(item);
		}
		root.add(array);
		
		return root;
	}
}

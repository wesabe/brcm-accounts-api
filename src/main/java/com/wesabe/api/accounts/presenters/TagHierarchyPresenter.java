package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import com.google.inject.Inject;
import com.wesabe.api.accounts.analytics.TagHierarchy;
import com.wesabe.api.accounts.analytics.TagHierarchy.Node;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

public class TagHierarchyPresenter {
	private final SumOfMoneyPresenter sumOfMoneyPresenter;
	
	@Inject
	public TagHierarchyPresenter(SumOfMoneyPresenter moneyPresenter) {
		this.sumOfMoneyPresenter = moneyPresenter;
	}
	
	public XmlsonObject present(TagHierarchy tagHierarchy, Locale locale) {
		final XmlsonObject root = new XmlsonObject("tag-hierarchy");
		root.add(sumOfMoneyPresenter.present("sum", tagHierarchy.getSum(), locale));
		
		final XmlsonArray nodes = new XmlsonArray("nodes");
		for (Node node : tagHierarchy.getChildren().values()) {
			nodes.add(present(node, locale));
		}
		root.add(nodes);
		
		return root;
	}

	private XmlsonObject present(Node node, Locale locale) {
		final XmlsonObject root = new XmlsonObject("node");
		
		final XmlsonObject tag = new XmlsonObject("tag");
		tag.addProperty("name", node.getTag().toString());
		root.add(tag);
		
		root.add(sumOfMoneyPresenter.present("sum", node.getSum(), locale));
		
		if (!node.getChildren().isEmpty()) {
			final XmlsonArray nodes = new XmlsonArray("nodes");
			for (Node child : node.getChildren().values()) {
				nodes.add(present(child, locale));
			}
			root.add(nodes);
		}
		
		return root;
	}
}

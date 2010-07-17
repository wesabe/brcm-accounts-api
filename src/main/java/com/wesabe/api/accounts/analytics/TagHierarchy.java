package com.wesabe.api.accounts.analytics;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.wesabe.api.accounts.entities.Tag;

public class TagHierarchy {
	private static final Function<Node, Tag> TAG_OF_NODE = new Function<Node, Tag>() {
		@Override
		public Tag apply(Node node) {
			return node.getTag();
		}
	};
	
	public static class Node {
		private final Tag tag;
		private final SumOfMoney sum;
		private final Map<Tag, Node> children;
		
		public Node(Tag tag, SumOfMoney sum, Iterable<Node> children) {
			this.tag = tag;
			this.sum = sum;
			this.children = Maps.uniqueIndex(children, TAG_OF_NODE);
		}
		
		public Tag getTag() {
			return tag;
		}
		
		public SumOfMoney getSum() {
			return sum;
		}
		
		public Map<Tag, Node> getChildren() {
			return children;
		}
		
		public String toString(int level) {
			final StringBuilder builder = new StringBuilder();
			builder.append("<Node tag=");
			builder.append(tag);
			builder.append(", sum=");
			builder.append(sum);
			builder.append(", children={");
			for (Entry<Tag, Node> child : children.entrySet()) {
				builder.append("\n");
				for (int i = 0; i < level+1; i++) {
					builder.append("\t");
				}
				builder.append(child.getKey());
				builder.append("=");
				builder.append(child.getValue().toString(level+1));
			}
			
			if (!children.isEmpty()) {
				builder.append("\n");
				for (int i = 0; i < level; i++) {
					builder.append("\t");
				}
			}
			
			
			
			builder.append("}>");
			return builder.toString();
		}
		
		@Override
		public String toString() {
			return toString(0);
		}
	}
	
	private final Map<Tag, Node> children;
	private final SumOfMoney sum;
	
	public TagHierarchy(Iterable<Node> children, SumOfMoney sum) {
		this.children = Maps.uniqueIndex(children, TAG_OF_NODE);
		this.sum = sum;
	}
	
	public Map<Tag, Node> getChildren() {
		return children;
	}
	
	public SumOfMoney getSum() {
		return sum;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("<TagHierarchy\n");
		for (Entry<Tag, Node> child : children.entrySet()) {
			builder.append("\t");
			builder.append(child.getKey());
			builder.append("=");
			builder.append(child.getValue().toString(1));
			builder.append("\n");
		}
		builder.append(">");
		return builder.toString();
	}
}

package com.wesabe.api.util.rails;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * A bug-for-bug implementation of Wesabe's custom Rails inflector rules.
 * 
 * @author coda
 *
 */
public class Inflector {
	private static class Rule {
		private final Pattern pattern;
		private final String replacement;
		
		public Rule(Pattern pattern, String replacement) {
			this.pattern = pattern;
			this.replacement = replacement;
		}
		
		public boolean matches(String input) {
			return pattern.matcher(input).find();
		}
		
		public String replace(String input) {
			return pattern.matcher(input).replaceFirst(replacement);
		}
	}
	
	private static Rule rule(String pattern, String replacement) {
		return new Rule(Pattern.compile(pattern), replacement);
	}
	
	private final List<Rule> pluralizationRules, singularizationRules;
	private final Set<String> uncountableWords;
	
	public Inflector() {
		this(
			// singular-to-plural rules
			new Rule[] {
				rule("(?-mix:(.)$)", "$1s"),
				rule("(?i-mx:s$)", "s"),
				rule("(?i-mx:x$)", "xes"),
				rule("(?i-mx:z$)", "zzes"),
				rule("(?i-mx:(ax|test)is$)", "$1es"),
				rule("(?i-mx:(octop)us$)", "$1i"),
				rule("(?i-mx:(alias|status|virus)$)", "$1es"),
				rule("(?i-mx:(bu)s$)", "$1ses"),
				rule("(?i-mx:(buffal|tomat)o$)", "$1oes"),
				rule("(?i-mx:([ti])um$)", "$1a"),
				rule("(?i-mx:sis$)", "ses"),
				rule("(?i-mx:(?:([^f])fe|([lr])f)$)", "$1$2ves"),
				rule("(?i-mx:([^aeiouy]|qu)y$)", "$1ies"),
				rule("(?i-mx:(x|ch|ss|sh)$)", "$1es"),
				rule("(?i-mx:(matr|vert|ind)(?:ix|ex)$)", "$1ices"),
				rule("(?i-mx:([m|l])ouse$)", "$1ice"),
				rule("(?i-mx:^(ox)$)", "$1en"),
				rule("(?i-mx:^(gas)$)", "$1es"),
				rule("(?i-mx:(p)erson$)", "$1eople"),
				rule("(?i-mx:(m)an$)", "$1en"),
				rule("(?i-mx:(c)hild$)", "$1hildren"),
			},
				
			// plural-to-singular rules
			new Rule[] {
				rule("(?i-mx:s$)", ""),
				rule("(?-mix:(\\w)\\1es$)", "$1"),
				rule("(?i-mx:(new|ga|serie)s$)", "$1s"),
				rule("(?i-mx:([ti])a$)", "$1um"),
				rule("(?i-mx:((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$)", "$1$2sis"),
				rule("(?i-mx:^(analy)ses$)", "$1sis"),
				rule("(?i-mx:([^f])ves$)", "$1fe"),
				rule("(?i-mx:([lr])ves$)", "$1f"),
				rule("(?i-mx:oves$)", "ove"),
				rule("(?i-mx:([^aeiouy]|qu)ies$)", "$1y"),
				rule("(?i-mx:(x|ch|ss|sh)es$)", "$1"),
				rule("(?i-mx:([m|l])ice$)", "$1ouse"),
				rule("(?i-mx:(bus)es$)", "$1"),
				rule("(?i-mx:(o)es$)", "$1"),
				rule("(?i-mx:^(cris|ax|test)es$)", "$1is"),
				rule("(?i-mx:(octop)i$)", "$1us"),
				rule("(?i-mx:(alias|status|virus)es$)", "$1"),
				rule("(?i-mx:^(ox)en)", "$1"),
				rule("(?i-mx:(vert|ind)ices$)", "$1ex"),
				rule("(?i-mx:(matr)ices$)", "$1ix"),
				rule("(?i-mx:(quiz)zes$)", "$1"),
				rule("(?i-mx:(hive|tive|movie|shoe)s$)", "$1"),
				rule("(?i-mx:(p)eople$)", "$1erson"),
				rule("(?i-mx:(m)en$)", "$1an"),
				rule("(?i-mx:(c)hildren$)", "$1hild"),
			},
			
			// uncountable words
			"equipment information rice money species series fish sheep feedback".split(" ")
		);
	}
	
	private Inflector(Rule[] pluralizationRules, Rule[] singularizationRules, String[] uncountableWords) {
		this.pluralizationRules = ImmutableList.copyOf(Iterables.reverse(ImmutableList.of(pluralizationRules)));
		this.singularizationRules = ImmutableList.copyOf(Iterables.reverse(ImmutableList.of(singularizationRules)));
		this.uncountableWords = ImmutableSet.of(uncountableWords);
	}
	
	private String processRules(String word, List<Rule> rules) {
		if (uncountableWords.contains(word.toLowerCase())) {
			return word;
		}
		
		for (Rule rule : rules) {
			if (rule.matches(word)) {
				return rule.replace(word);
			}
		}
		
		return word;
	}
	
	/**
	 * Returns the plural of a singular word or phrase.
	 * 
	 * @param singular a singular word or phrase
	 * @return the plural of {@code singular}
	 */
	public String pluralize(String singular) {
		return processRules(singular, pluralizationRules);
	}
	
	/**
	 * Returns the singular of a plural word or phrase.
	 * 
	 * @param plural a plural word or phrase
	 * @return the singular of {@code plural}
	 */
	public String singularize(String plural) {
		return processRules(plural, singularizationRules);
	}
}

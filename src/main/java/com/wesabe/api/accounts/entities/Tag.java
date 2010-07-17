package com.wesabe.api.accounts.entities;

import java.util.regex.Pattern;

import com.google.common.base.Objects;
import com.wesabe.api.util.rails.Inflector;

/**
 * A user-entered tag with a fuzzy equality property.
 * <p>
 * <b>N.B.:</b> This is not the same thing as a PFC {@code Tag}. It's created from user-entered text
 * (e.g., {@code grocery}), but is equal to other tags which differ in name by whitespace,
 * punctuation, plurality, etc.
 * 
 * @author coda
 *
 */
public class Tag {
	private static final Inflector INFLECTOR = new Inflector();
	private static final Pattern SPLIT = Pattern.compile(":.*");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]+", Pattern.CASE_INSENSITIVE);
	private static final Pattern PUNCTUATION = Pattern.compile("[\\p{Punct}\\p{Space}]", Pattern.CASE_INSENSITIVE);

	private final String nameWithSplit, name, hash;

	public Tag(String name) {
		this.nameWithSplit = name;
		this.name = stripSplit(nameWithSplit);
		this.hash = hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tag) {
			final Tag other = (Tag) obj;
			return Objects.equal(hash, other.hash);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hash);
	}

	@Override
	public String toString() {
		return name;
	}

	private static String hash(String tagName) {
		final String noSplit = stripSplit(tagName.toLowerCase());
		final String noWhitespace = stripWhitespace(noSplit);
		final String noPunctuation = stripPunctuation(noWhitespace);
		if (noPunctuation.isEmpty()) {
			return noWhitespace;
		}

		final String singular = INFLECTOR.singularize(noPunctuation);
		if (singular.isEmpty()) {
			return noPunctuation;
		}

		return singular;
	}

	private static String stripSplit(String tagName) {
		return SPLIT.matcher(tagName).replaceAll("");
	}

	private static String stripWhitespace(String tagName) {
		return WHITESPACE.matcher(tagName).replaceAll("");
	}

	private static String stripPunctuation(String tagName) {
		return PUNCTUATION.matcher(tagName).replaceAll("");
	}

}

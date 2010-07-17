package com.wesabe.api.util.guid;

import java.security.SecureRandom;

import com.google.common.base.Objects;

/**
 * A globally-unique identifier.
 * <p>
 * <strong>This class does not implement Microsoft GUIDs.</strong> Instead, it
 * is a convenience class which wraps the creation and comparison of unique
 * identifiers.
 * </p>
 * 
 * @author coda
 *
 */
public final class GUID {
	private final String value;

	/**
	 * Generates a random, hexadecimal GUID of the specified length.
	 * 
	 * @param length the number of characters in the generated GUID
	 * @return a random, hexadecimal GUID
	 */
	public static GUID generateRandom(int length) {
		return generateRandom(length, CharacterSet.HEXADECIMAL);
	}

	/**
	 * Generates a random GUID of the specified length using the specified
	 * character set.
	 * 
	 * @param length the number of characters in the generated GUID
	 * @param charSet the character set of the generated GUID
	 * @return a random GUID
	 */
	public static GUID generateRandom(int length, CharacterSet charSet) {
		final StringBuilder builder = new StringBuilder();
		final SecureRandom rng = new SecureRandom();

		for (int i = 0; i < length; i++) {
			builder.append(charSet.getCharacter(rng.nextInt(charSet.length())));
		}

		return new GUID(builder.toString());
	}
	
	/**
	 * Instantiates a {@link GUID} object with a pre-generated GUID.
	 * 
	 * @param guidAsString a pre-generated GUID
	 */
	public GUID(String guidAsString) {
		this.value = guidAsString;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GUID) {
			final GUID that = (GUID) obj;
			return Objects.equal(value, that.value);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}

	/**
	 * The number of characters in this {@link GUID}.
	 * 
	 * @return the number of characters in this GUID
	 */
	public int length() {
		return value.length();
	}
}

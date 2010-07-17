package com.wesabe.api.util.guid;

/**
 * A set of characters from which a GUID can be generated.
 * 
 * @author coda
 *
 */
public class CharacterSet {
	public static final CharacterSet HEXADECIMAL = new CharacterSet("abcdef0123456789");
	public static final CharacterSet SAFE_ALPHANUM = new CharacterSet("BCDFGHJKLMNPQRSTVWXZbcdfghjklmnpqrstvwxz0123456789");
	public static final CharacterSet SAFE_LOWERCASE_ALPHANUM = new CharacterSet("bcdfghjklmnpqrstvwxz0123456789");

	private final char[] characters;
	
	public CharacterSet(String characters) {
		this.characters = characters.toCharArray();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return "CharacterSet{" + new String(characters) + "}";
	}

	/**
	 * The number of characters in the set.
	 * 
	 * @return the number of characters in the set
	 */
	public int length() {
		return characters.length;
	}

	/**
	 * Returns the {@code index}th character in the set.
	 * 
	 * @param index the index of the character
	 * @return the character
	 * @throws IndexOutOfBoundsException if index is less than zero or greater
	 * 			than the character set
	 */
	public char getCharacter(int index) {
		return characters[index];
	}
}
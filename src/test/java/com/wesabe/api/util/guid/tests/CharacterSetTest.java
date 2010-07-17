package com.wesabe.api.util.guid.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.guid.CharacterSet;

@RunWith(Enclosed.class)
public class CharacterSetTest {
	
	public static class Hexadecimal {
		@Test
		public void shouldBeHexadecimal() throws Exception {
			assertEquals("CharacterSet{abcdef0123456789}",
					CharacterSet.HEXADECIMAL.toString());
		}
		
		@Test
		public void shouldHaveSixteenCharacters() throws Exception {
			assertEquals(16, CharacterSet.HEXADECIMAL.length());
		}
	}
	
	public static class Safe_Alphanumeric {
		@Test
		public void shouldBeAlphanumeric() throws Exception {
			assertEquals("CharacterSet{BCDFGHJKLMNPQRSTVWXZbcdfghjklmnpqrstvwxz0123456789}",
					CharacterSet.SAFE_ALPHANUM.toString());
		}
		
		@Test
		public void shouldHaveFiftyCharacters() throws Exception {
			assertEquals(50, CharacterSet.SAFE_ALPHANUM.length());
		}
	}
	
	public static class Safe_Lowercase_Alphanumeric {
		@Test
		public void shouldBeLowercaseAlphanumeric() throws Exception {
			assertEquals("CharacterSet{bcdfghjklmnpqrstvwxz0123456789}",
					CharacterSet.SAFE_LOWERCASE_ALPHANUM.toString());
		}
		
		@Test
		public void shouldHaveThirtyCharacters() throws Exception {
			assertEquals(30, CharacterSet.SAFE_LOWERCASE_ALPHANUM.length());
		}
	}
	
}

package com.wesabe.api.util.guid.tests;

import static com.wesabe.api.tests.util.RegularExpressionHelper.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.guid.CharacterSet;
import com.wesabe.api.util.guid.GUID;

@RunWith(Enclosed.class)
public class GUIDTest {
	
	public static class Generating_A_GUID {
		@Test
		public void shouldProduceARandomGUIDOfTheRequestedLength() throws Exception {
			GUID guid = GUID.generateRandom(40);
			assertEquals(40, guid.length());
		}
		
		@Test
		public void shouldBeAllHex() throws Exception {
			GUID guid = GUID.generateRandom(40);
			
			assertMatches("^[0-9a-f]+$", guid.toString());
		}
	}
	
	public static class Generating_A_GUID_Using_A_Different_Character_Set {
		@Test
		public void shouldBeAllHex() throws Exception {
			GUID guid = GUID.generateRandom(40, CharacterSet.SAFE_LOWERCASE_ALPHANUM);
			
			
			assertMatches("^[bcdfghjklmnpqrstvwxz0123456789]+$", guid.toString());
		}
	}
	
	public static class Two_Equivalent_GUIDs {
		@Test
		public void shouldBeEqual() throws Exception {
			GUID guid1 = new GUID("yay");
			GUID guid2 = new GUID("yay");
			assertEquals(guid1, guid2);
		}
		
		@Test
		public void shouldHaveEqualHashCodes() throws Exception {
			GUID guid1 = new GUID("yay");
			GUID guid2 = new GUID("yay");
			assertEquals(guid1.hashCode(), guid2.hashCode());
		}
	}
	
	public static class Two_Different_GUIDs {
		@Test
		public void shouldNotBeEqual() throws Exception {
			GUID guid1 = new GUID("yay");
			GUID guid2 = new GUID("boo");
			assertFalse(guid1.equals(guid2));
		}
		
		@Test
		public void shouldHaveDifferentHashCodes() throws Exception {
			GUID guid1 = new GUID("yay");
			GUID guid2 = new GUID("boo");
			assertFalse(guid1.hashCode() == guid2.hashCode());
		}
	}
	
	public static class A_GUID {
		@Test
		public void shouldNotBeEqualToANonGUID() throws Exception {
			GUID guid1 = new GUID("yay");
			assertFalse(guid1.equals("yay"));
		}
		
		@Test
		public void shouldNotBeEqualToNull() throws Exception {
			GUID guid1 = new GUID("yay");
			assertFalse(guid1.equals(null));
		}
		
		@Test
		public void shouldBeEqualToItself() throws Exception {
			GUID guid1 = new GUID("yay");
			assertTrue(guid1.equals(guid1));
		}
		
		@Test
		public void shouldRepresentItselfAsAString() throws Exception {
			GUID guid1 = new GUID("yay");
			assertEquals("yay", guid1.toString());
		}
	}
	
	
	
}

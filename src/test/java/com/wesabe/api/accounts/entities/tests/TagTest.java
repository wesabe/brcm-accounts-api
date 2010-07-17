package com.wesabe.api.accounts.entities.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Tag;

@RunWith(Enclosed.class)
public class TagTest {
	public static class A_Singular_Lowercase_Tag {
		private final Tag food = new Tag("food");
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertEquals("food", food.toString());
		}
		
		@Test
		public void itIsEqualToItself() throws Exception {
			final Tag other = new Tag("food");
			assertTrue(food.equals(other));
			assertEquals(food.hashCode(), other.hashCode());
		}
		
		@Test
		public void itIsEqualToAnUppercaseVersionOfItself() throws Exception {
			final Tag other = new Tag("FOOD");
			assertTrue(food.equals(other));
			assertEquals(food.hashCode(), other.hashCode());
		}
		
		@Test
		public void itIsEqualToAPluralVersionOfItself() throws Exception {
			final Tag other = new Tag("foods");
			assertTrue(food.equals(other));
			assertEquals(food.hashCode(), other.hashCode());
		}
		
		@Test
		public void itIsEqualToAPunctuatedVersionOfItself() throws Exception {
			final Tag other = new Tag("food!");
			assertTrue(food.equals(other));
			assertEquals(food.hashCode(), other.hashCode());
		}
		
		@Test
		public void itIsEqualToAVersionOfItselfWithWhitespace() throws Exception {
			final Tag other = new Tag("f o\tod");
			assertTrue(food.equals(other));
			assertEquals(food.hashCode(), other.hashCode());
		}
		
		@Test
		public void itIsNotEqualToNull() throws Exception {
			assertFalse(food.equals(null));
		}
		
		@Test
		public void itIsNotEqualToANonTag() throws Exception {
			assertFalse(food.equals("DUDE WHAT"));
		}
	}
	
	public static class A_Tag_With_A_Split {
		private final Tag rent = new Tag("rent:50%");
		
		@Test
		public void itDisplaysWithoutSplit() throws Exception {
			assertEquals("rent", rent.toString());
		}
		
		@Test
		public void itIsEqualToItself() throws Exception {
			final Tag other = new Tag("rent:50%");
			assertTrue(rent.equals(other));
			assertEquals(rent.hashCode(), other.hashCode());
		}
		
		@Test
		public void itIsEqualToATagOfTheSameNameWithDifferentSplit() throws Exception {
			final Tag other = new Tag("rent:450");
			assertTrue(rent.equals(other));
			assertEquals(rent.hashCode(), other.hashCode());
		}
	}
	
	public static class A_Tag_Of_Only_Punctuation {
		@Test
		public void itIsEqualToItself() throws Exception {
			final Tag tag = new Tag("???__?");
			final Tag other = new Tag("???__?");
			assertTrue(tag.equals(other));
			assertEquals(tag.hashCode(), other.hashCode());
		}
		
		@Test
		public void itIsEqualToItselfWithWhitespace() throws Exception {
			final Tag tag = new Tag("???__?");
			final Tag other = new Tag("???__   ?");
			assertTrue(tag.equals(other));
			assertEquals(tag.hashCode(), other.hashCode());
		}
	}
	
	public static class A_Tag_Of_Only_The_Letter_S {
		@Test
		public void itIsEqualToItself() throws Exception {
			final Tag tag = new Tag("s");
			final Tag other = new Tag("S");
			assertTrue(tag.equals(other));
			assertEquals(tag.hashCode(), other.hashCode());
		}
	}
	
	public static class A_Plural_Tag {
		@Test
		public void itIsEqualToAPluralVersionOfItself() throws Exception {
			final Tag tag = new Tag("cars");
			final Tag other = new Tag("car");
			assertTrue(tag.equals(other));
			assertEquals(tag.hashCode(), other.hashCode());
		}
	}
}

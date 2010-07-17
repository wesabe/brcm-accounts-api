package com.wesabe.api.tests.util;

import static org.junit.Assert.*;

public class RegularExpressionHelper {
	public static void assertMatches(String pattern, String actual) {
		assertTrue("expected \"" + actual + "\" to match " + pattern, actual.matches(pattern));
	}
}
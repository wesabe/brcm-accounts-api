package com.wesabe.api.tests.util;

import com.wesabe.api.accounts.entities.Tag;

public class TagHelper {
	public static Tag tag(String name) {
		return new Tag(name);
	}
}

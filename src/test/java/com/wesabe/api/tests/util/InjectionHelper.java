package com.wesabe.api.tests.util;

import java.lang.reflect.Field;

public class InjectionHelper {
	public static void inject(Class<?> klass, Object obj, String name, Object value)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Field field = klass.getDeclaredField(name);
		field.setAccessible(true);
		field.set(obj, value);
	}
}

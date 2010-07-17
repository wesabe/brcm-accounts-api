package com.wesabe.api.tests.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateHelper {
	public static final DateTime dec31st = date(2007, 12, 31);
	public static final DateTime  jan1st = date(2008,  1,  1);
	public static final DateTime  jan2nd = date(2008,  1,  2);
	public static final DateTime  apr1st = date(2008,  4,  1);
	public static final DateTime jun14th = date(2008,  6, 14);
	public static final DateTime jun15th = date(2008,  6, 15);
	public static final DateTime jun16th = date(2008,  6, 16);
	public static final DateTime jun17th = date(2008,  6, 17);
	public static final DateTime  jul1st = date(2008,  7,  1);
	public static final DateTime  oct1st = date(2008, 10,  1);
	public static final DateTime oct13th = date(2008, 10, 13);
	public static final DateTime oct17th = date(2008, 10, 17);
	public static final DateTime oct20th = date(2008, 10, 20);
	public static final DateTime oct27th = date(2008, 10, 27);
	public static final DateTime  nov1st = date(2008, 11,  1);
	public static final DateTime  nov3nd = date(2008, 11,  3);
	public static final DateTime  dec1st = date(2008, 12,  1);

	public static final DateTime valentinesDay08 = date(2008, 2, 14);
	public static final DateTime mayDay08 = date(2008, 5, 1);
	public static final DateTime nationalDayOfEncouragement08 = date(2008, 9, 12);
	public static final DateTime germanAmericanDay08 = date(2008, 10, 6);
	
	public static final DateTime newYearsUTC = time(2009, 1, 1, 0, 0, 0, DateTimeZone.UTC);
	
	public static DateTime date(int year, int month, int day) {
		return new DateTime(year, month, day, 0, 0, 0, 0);
	}
	
	public static DateTime date(long instant) {
	  return new DateTime(instant);
	}
	
	public static DateTime time(int year, int month, int day, int hour, int minute, int second, DateTimeZone zone) {
		return new DateTime(year, month, day, hour, minute, second, 0, zone);
	}
	
	public static DateTime now() {
		return new DateTime();
	}
	
	public static DateTime now(DateTimeZone zone) {
		return now().withZone(zone);
	}
}
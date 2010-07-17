package com.wesabe.api.accounts.analytics.tests;

import static com.wesabe.api.tests.util.DateHelper.*;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.analytics.IntervalType;

@RunWith(Enclosed.class)
public class IntervalTypeTest {
	
	public static class A_Daily_Interval {
		private final IntervalType intervalType = IntervalType.DAILY;
		private final Interval allOfJune15th = new Interval(jun15th, jun16th);
		private final Interval allOfJune16th = new Interval(jun16th, jun17th);
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertEquals("daily", intervalType.toString());
		}
		
		@Test
		public void itConvertsADateTimeIntoADayLongInterval() throws Exception {
			final DateTime noonOnJune15th = jun15th.plusHours(12);
			assertEquals(allOfJune15th, intervalType.currentInterval(noonOnJune15th));
		}
		
		@Test
		public void itIncrementsAnIntervalByADay() throws Exception {
			assertEquals(allOfJune16th, intervalType.nextInterval(allOfJune15th));
		}
		
		@Test
		public void itIteratesOverTheDailyIntervalsInALargerInterval() throws Exception {
			final ImmutableList<Interval> daysBetweenJune15thAndJune17th =
				ImmutableList.of(allOfJune15th, allOfJune16th);
			
			assertEquals(
				daysBetweenJune15thAndJune17th,
				ImmutableList.copyOf(intervalType.getIntervals(
					new Interval(jun15th, jun17th)
				))
			);
		}
		
		@Test
		public void itIteratesNonDestructively() throws Exception {
			final Iterator<Interval> iterator = intervalType.getIntervals(allOfJune15th).iterator();
			
			boolean raisedAnError = false;
			try {
				iterator.remove();
			} catch (UnsupportedOperationException e) {
				raisedAnError = true;
			}
			
			assertTrue(raisedAnError);
		}
	}
	
	public static class A_Weekly_Interval {
		private final IntervalType intervalType = IntervalType.WEEKLY;
		private final Interval the42ndWeekOf2008 = new Interval(oct13th, oct20th);
		private final Interval the43rdWeekOf2008 = new Interval(oct20th, oct27th);
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertEquals("weekly", intervalType.toString());
		}
		
		@Test
		public void itConvertsADateTimeIntoAWeekLongInterval() throws Exception {
			assertEquals(the42ndWeekOf2008, intervalType.currentInterval(oct17th.plusHours(18).plusMinutes(2)));
		}
		
		@Test
		public void itIncrementsAnIntervalByAWeek() throws Exception {
			assertEquals(the43rdWeekOf2008, intervalType.nextInterval(the42ndWeekOf2008));
		}
		
		@Test
		public void itIteratesOverTheWeeklyIntervalsBetweenTwoDates() throws Exception {
			final List<Interval> weeksBetweenOct13thAndOct27th =
				ImmutableList.of(the42ndWeekOf2008, the43rdWeekOf2008);
			
			assertEquals(
				weeksBetweenOct13thAndOct27th,
				ImmutableList.copyOf(intervalType.getIntervals(
					new Interval(oct13th, oct27th)
				))
			);
		}
	}
	
	public static class A_Monthly_Interval {
		private final IntervalType intervalType = IntervalType.MONTHLY;
		private final Interval oct2008 = new Interval(oct1st, nov1st);
		private final Interval nov2008 = new Interval(nov1st, dec1st);
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertEquals("monthly", intervalType.toString());
		}
		
		@Test
		public void itConvertsADateTimeIntoAMonthLongInterval() throws Exception {
			assertEquals(oct2008, intervalType.currentInterval(oct13th));
		}
		
		@Test
		public void itIncrementAnIntervalByAMonth() throws Exception {
			assertEquals(nov2008, intervalType.nextInterval(oct2008));
		}
		
		@Test
		public void itIteratesOverTheMonthlyIntervalsBetweenTwoDates() throws Exception {
			final List<Interval> monthsBetweenOct1stAndNov1st =
				ImmutableList.of(oct2008, nov2008);
			
			assertEquals(
				monthsBetweenOct1stAndNov1st,
				ImmutableList.copyOf(intervalType.getIntervals(
					new Interval(oct1st, dec1st)
				))
			);
		}
	}
	
	public static class A_Quarterly_Interval {
		private final IntervalType intervalType = IntervalType.QUARTERLY;
		private final Interval firstQuarterOf2008 = new Interval(jan1st, apr1st);
		private final Interval secondQuarterOf2008 = new Interval(apr1st, jul1st);
		private final Interval thirdQuarterOf2008 = new Interval(jul1st, oct1st);
		private final Interval fourthQuarterOf2008 = new Interval(oct1st, jan1st.plusYears(1));
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertEquals("quarterly", intervalType.toString());
		}
		
		@Test
		public void itConvertsADateInTheFirstQuarterOfTheYearIntoAQuarterLongInterval() throws Exception {
			assertEquals(firstQuarterOf2008, intervalType.currentInterval(valentinesDay08));
		}
		
		@Test
		public void itConvertsADateInTheSecondQuarterOfTheYearIntoAQuarterLongInterval() throws Exception {
			assertEquals(secondQuarterOf2008, intervalType.currentInterval(mayDay08));
		}
		
		@Test
		public void itConvertsADateInTheThirdQuarterOfTheYearIntoAQuarterLongInterval() throws Exception {
			assertEquals(thirdQuarterOf2008, intervalType.currentInterval(nationalDayOfEncouragement08));
		}
		
		@Test
		public void itConvertsADateInTheFourthQuarterOfTheYearIntoAQuarterLongInterval() throws Exception {
			assertEquals(fourthQuarterOf2008, intervalType.currentInterval(germanAmericanDay08));
		}
		
		@Test
		public void itIncrementsAnIntervalByAThreeMonths() throws Exception {
			assertEquals(secondQuarterOf2008, intervalType.nextInterval(firstQuarterOf2008));
		}
		
		@Test
		public void itIteratesOverTheQuarterlyIntervalsBetweenTwoDates() throws Exception {
			final List<Interval> quartersBetweenApr1stAndJul1st =
				ImmutableList.of(firstQuarterOf2008, secondQuarterOf2008);
			
			
			assertEquals(
				quartersBetweenApr1stAndJul1st,
				ImmutableList.copyOf(intervalType.getIntervals(new Interval(jan1st, jul1st)))
			);
		}
	}
	
	public static class A_Yearly_Interval {
		private final IntervalType intervalType = IntervalType.YEARLY;
		private final Interval allOf2008 = new Interval(jan1st, jan1st.plusYears(1));
		private final Interval allOf2009 = new Interval(jan1st.plusYears(1), jan1st.plusYears(2));
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertEquals("yearly", intervalType.toString());
		}
		
		@Test
		public void itConvertsADateIntoAYearLongInterval() throws Exception {
			assertEquals(allOf2008, intervalType.currentInterval(valentinesDay08));
		}
		
		@Test
		public void itIncrementsAnIntervalByAYear() throws Exception {
			assertEquals(allOf2009, intervalType.nextInterval(allOf2008));
		}
		
		@Test
		public void itIteratesOverTheYearlyIntervalsBetweenTwoDates() throws Exception {
			final List<Interval> yearsBetween2008And2010 =
				ImmutableList.of(allOf2008, allOf2009);
			
			assertEquals(
				yearsBetween2008And2010,
				ImmutableList.copyOf(intervalType.getIntervals(
					new Interval(jan1st, jan1st.plusYears(2))
				))
			);
		}
	}
	
}

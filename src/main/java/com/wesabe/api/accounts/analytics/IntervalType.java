package com.wesabe.api.accounts.analytics;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * An enumeration representing the various intervals by which a user's spending
 * and earning volume can be calculated: daily, weekly, biweekly, monthly,
 * quarterly, and yearly.
 * 
 * @author coda
 *
 */
public enum IntervalType {
	
	/**
	 * A single day.
	 * 
	 */
	DAILY {
		@Override
		protected Period getPeriod() {
			return ONE_DAY;
		}
		
		@Override
		protected DateTime getIntervalStart(DateTime dateTime) {
			return beginningOfDay(dateTime);
		}
	},
	
	/**
	 * A week.
	 * 
	 */
	WEEKLY {
		@Override
		protected Period getPeriod() {
			return ONE_WEEK;
		}
		
		@Override
		protected DateTime getIntervalStart(DateTime dateTime) {
			return beginningOfWeek(beginningOfDay(dateTime));
		}
	},
	
	/**
	 * A month.
	 * 
	 * <p><strong>N.B.:</strong> This is not strictly 30 days -- a month from
	 * February 20th is considered to be March 20th. This makes this interval
	 * slightly uneven in terms of days, but date math sucks anyways.</p>
	 * 
	 */
	MONTHLY {
		
		@Override
		protected Period getPeriod() {
			return ONE_MONTH;
		}
		
		@Override
		protected DateTime getIntervalStart(DateTime dateTime) {
			return beginningOfMonth(beginningOfDay(dateTime));
		}
	},
	
	/**
	 * A quarter. (Three months).
	 * 
	 * <p>See the documentation for the monthly interval for how months are
	 * calculated.</p>
	 * 
	 */
	QUARTERLY {
		@Override
		protected Period getPeriod() {
			return THREE_MONTHS;
		}
		
		@Override
		protected DateTime getIntervalStart(DateTime dateTime) {
			return beginningOfQuarter(beginningOfMonth(beginningOfDay(dateTime)));
		}
	},
	
	/**
	 * A year.
	 * 
	 * <p><strong>N.B.:</strong> Like the monthly interval, a year from
	 * 2004-02-01 is 2005-02-01, which ignores leap years and such. Again, this
	 * is an uneven interval in terms of days; again, date math sucks.</p>
	 * 
	 */
	YEARLY {
		@Override
		protected Period getPeriod() {
			return ONE_YEAR;
		}
		
		@Override
		protected DateTime getIntervalStart(DateTime dateTime) {
			return beginningOfYear(beginningOfMonth(beginningOfDay(dateTime)));
		}
	};
	
	// constants used as instance periods
	private static final Period ONE_DAY = Period.days(1);
	private static final Period ONE_WEEK = Period.weeks(1);
	private static final Period ONE_MONTH = Period.months(1);
	private static final Period THREE_MONTHS = Period.months(3);
	private static final Period ONE_YEAR = Period.years(1);

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
	/**
	 * Iterates over the intervals of a given type between two dates.
	 * 
	 * @author coda
	 */
	private static class IntervalIterator implements Iterator<Interval>, Iterable<Interval> {
		private final IntervalType intervalType;
		private final Interval endInterval;
		private Interval currentInterval;
		
		public IntervalIterator(DateTime start, DateTime end, IntervalType intervalType) {
			this.endInterval = intervalType.currentInterval(end.minusMillis(1));
			this.intervalType = intervalType;
			this.currentInterval = intervalType.currentInterval(start);
		}
		
		@Override
		public boolean hasNext() {
			return currentInterval.isBefore(endInterval) || currentInterval.contains(endInterval.getStart());
		}

		@Override
		public Interval next() {
			Interval thisInterval = currentInterval;
			currentInterval = intervalType.nextInterval(currentInterval);
			return thisInterval;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<Interval> iterator() {
			return this;
		}

	}
	
	/**
	 * Converts a {@link DateTime} into the beginning instant of the interval
	 * type.
	 * 
	 * @param dateTime an instant in an interval
	 * @return beginning instant of the interval
	 */
	protected abstract DateTime getIntervalStart(DateTime dateTime);
	
	/**
	 * The length of the interval type.
	 * 
	 * @return length of the interval type
	 */
	protected abstract Period getPeriod();
	
	/**
	 * Converts a {@link DateTime} into the {@link Interval} of the given type
	 * which contains the instant.
	 * 
	 * @param dateTime an instant
	 * @return an interval containing the instant
	 */
	public Interval currentInterval(DateTime dateTime) {
		return new Interval(getIntervalStart(dateTime), getPeriod());
	}

	/**
	 * Given an {@link Interval}, returns the next interval of the given type.
	 * 
	 * @param current an interval
	 * @return the next interval
	 */
	public Interval nextInterval(Interval current) {
		return new Interval(current.getEnd(), getPeriod());
	}
	
	/**
	 * Returns the beginning of a day.
	 * 
	 * @param dateTime a day
	 * @return the beginning of the day
	 */
	protected DateTime beginningOfDay(DateTime dateTime) {
		return dateTime.withTime(0, 0, 0, 0);
	}
	
	/**
	 * Returns the beginning of a week.
	 * 
	 * @param dateTime a day
	 * @return the beginning of the week
	 */
	protected DateTime beginningOfWeek(DateTime dateTime) {
		return dateTime.withDayOfWeek(1);
	}
	
	/**
	 * Returns the beginning of a month.
	 * 
	 * @param dateTime a day
	 * @return the beginning of the month
	 */
	protected DateTime beginningOfMonth(DateTime dateTime) {
		return dateTime.withDayOfMonth(1);
	}
	
	/**
	 * Returns the beginning of a quarter.
	 * 
	 * @param dateTime a day
	 * @return the beginning of the quarter
	 */
	protected DateTime beginningOfQuarter(DateTime dateTime) {
		return dateTime.withMonthOfYear((((dateTime.getMonthOfYear() - 1) / 3) * 3) + 1);
	}
	
	/**
	 * Returns the beginning of a year.
	 * 
	 * @param dateTime a day
	 * @return the beginning of the year
	 */
	protected DateTime beginningOfYear(DateTime dateTime) {
		return dateTime.withMonthOfYear(1);
	}

	/**
	 * Returns an {@link Iterable} which will iterate over each
	 * interval of this type between the start point and end point of a given
	 * interval.
	 * 
	 * @param interval a range of time
	 * @return an interval iterator
	 */
	public Iterable<Interval> getIntervals(Interval interval) {
		return new IntervalIterator(interval.getStart(), interval.getEnd(), this);
	}
}

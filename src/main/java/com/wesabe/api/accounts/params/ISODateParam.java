package com.wesabe.api.accounts.params;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import com.codahale.shore.params.AbstractParam;
import com.google.common.collect.ImmutableList;

/**
 * Parses an ISO 8601 date into a {@link DateTime}.
 * 
 * Handles the following types of dates:
 * <ul>
 * 	<li>Basic Calendar Date &mdash; {@code 20070605}</li>
 * 	<li>Extended Calendar Date &mdash; {@code 2007-06-05}</li>
 * 	<li>Basic Week Date &mdash; {@code 2006W27}</li>
 * 	<li>Extended Week Date &mdash; {@code 2006-W27}</li>
 * 	<li>Basic Week Date With Weekday &mdash; {@code 2006W273}</li>
 * 	<li>Extended Week Date With Weekday &mdash; {@code 2006-W27-3}</li>
 * 	<li>Basic Ordinal Date &mdash; {@code 2006186}</li>
 * 	<li>Extended Ordinal Date &mdash; {@code 2006-186}</li>
 * </ul>
 * 
 * @author coda
 *
 * @see ISODateTimeFormat#basicDate()
 * @see ISODateTimeFormat#date()
 * @see ISODateTimeFormat#basicWeekDate()
 * @see ISODateTimeFormat#weekDate()
 * @see ISODateTimeFormat#basicOrdinalDate()
 * @see ISODateTimeFormat#ordinalDate()
 * @see ISODateTimeFormat#weekyearWeek()
 */
public class ISODateParam extends AbstractParam<DateTime> {
	private static final List<DateTimeFormatter> FORMATS = ImmutableList.of(
		ISODateTimeFormat.basicDate(),
		ISODateTimeFormat.date(),
		ISODateTimeFormat.basicWeekDate(),
		ISODateTimeFormat.weekDate(),
		ISODateTimeFormat.basicOrdinalDate(),
		ISODateTimeFormat.ordinalDate(),
		ISODateTimeFormat.weekyearWeek(),
		new DateTimeFormatterBuilder()
			.append(ISODateTimeFormat.weekyear())
			.appendLiteral('W')
			.appendWeekOfWeekyear(2)
			.toFormatter()
	);
	
	/**
	 * Creates a new {@link ISODateParam} for a given ISO 8601 date string.
	 * 
	 * @param date an ISO 8601 date string
	 * @throws WebApplicationException if {@code date} isn't a valid ISO 8601 date
	 */
	public ISODateParam(String date) throws WebApplicationException {
		super(date);
	}

	@Override
	protected DateTime parse(String param) throws Exception {
		for (DateTimeFormatter format : FORMATS) {
			try {
				return format.parseDateTime(param);
			} catch (IllegalArgumentException e) {
				// don't do anything -- just try the next format
			}
		}
		
		throw new IllegalArgumentException("not a valid ISO 8601 date");
	}
}

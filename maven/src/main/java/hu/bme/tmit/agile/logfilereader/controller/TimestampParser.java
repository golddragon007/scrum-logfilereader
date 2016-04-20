package hu.bme.tmit.agile.logfilereader.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import util.RegexpPatterns;

public class TimestampParser {

	private static final int YEAR_GROUP_INDEX = 1;
	private static final int MONTH_GROUP_INDEX = 2;
	private static final int DAY_GROUP_INDEX = 3;

	private static final int HOUR_GROUP_INDEX = 1;
	private static final int MINUTE_GROUP_INDEX = 2;
	private static final int SECOND_GROUP_INDEX = 3;
	private static final int MICRO_GROUP_INDEX = 4;

	private static final String INPUT_MONTH_FORMAT = "MMM";
	private static final String OUTPUT_MONTH_FORMAT = "MM";

	public static LogTimestamp parse(String[] parts) throws ParseException {
		String dateString = parts[0];
		String timeString = parts[1];

		int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0, micro = 0;

		Pattern datePattern = Pattern.compile(RegexpPatterns.datePattern);
		Matcher dateMatcher = datePattern.matcher(dateString);
		if (dateMatcher.matches()) {
			year = Integer.parseInt(dateMatcher.group(YEAR_GROUP_INDEX));
			month = parseMonth(dateMatcher);
			day = Integer.parseInt(dateMatcher.group(DAY_GROUP_INDEX));
		}

		Pattern timePattern = Pattern.compile(RegexpPatterns.timePattern);
		Matcher timeMatcher = timePattern.matcher(timeString);
		if (timeMatcher.matches()) {
			hour = Integer.parseInt(timeMatcher.group(HOUR_GROUP_INDEX));
			minute = Integer.parseInt(timeMatcher.group(MINUTE_GROUP_INDEX));
			second = Integer.parseInt(timeMatcher.group(SECOND_GROUP_INDEX));
			micro = Integer.parseInt(timeMatcher.group(MICRO_GROUP_INDEX));
		}

		return new LogTimestamp(year, month, day, hour, minute, second, micro);
	}

	private static int parseMonth(Matcher dateMatcher) throws ParseException {
		String monthString = dateMatcher.group(MONTH_GROUP_INDEX);
		SimpleDateFormat inputFormat = new SimpleDateFormat(INPUT_MONTH_FORMAT, Locale.ENGLISH);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputFormat.parse(monthString));
		SimpleDateFormat outputFormat = new SimpleDateFormat(OUTPUT_MONTH_FORMAT);
		int month = Integer.parseInt(outputFormat.format(calendar.getTime()));
		return month;
	}

}

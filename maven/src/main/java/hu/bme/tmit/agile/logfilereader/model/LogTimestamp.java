package hu.bme.tmit.agile.logfilereader.model;

import org.joda.time.DateTime;

public class LogTimestamp {

	private DateTime dt;
	private int micro = 0;

	public LogTimestamp() {
		dt = new DateTime();
	}

	public LogTimestamp(int year, int month, int day, int hour, int minute, int second, int micro) {
		this.dt = new DateTime(year, month, day, hour, minute, second);
		this.micro = micro;
	}

	public DateTime getDateTime() {
		return dt;
	}

	public void setDateTime(DateTime dt) {
		this.dt = dt;
	}

	public int getMicro() {
		return micro;
	}

	public void setMicro(int micro) {
		this.micro = micro;
	}

	public String toDateTimeString() {
		return dt.getYear() + "." + dt.getMonthOfYear() + "." + dt.getDayOfMonth() + " " + dt.getHourOfDay() + ":"
				+ dt.getMinuteOfHour() + ":" + dt.getSecondOfMinute();
	}

	@Override
	public String toString() {
		return toDateTimeString() + "." + micro;
	}

	public boolean isEqual(LogTimestamp lt) {
		if (dt.isEqual(lt.getDateTime())) {
			return micro == lt.getMicro();
		}
		return false;
	}

	public boolean isBefore(LogTimestamp lt) {
		if (dt.isEqual(lt.getDateTime())) {
			return micro < lt.getMicro();
		}
		return dt.isBefore(lt.getDateTime());
	}

	public boolean isAfter(LogTimestamp lt) {
		if (dt.isEqual(lt.getDateTime())) {
			return micro > lt.getMicro();
		}
		return dt.isAfter(lt.getDateTime());
	}
}

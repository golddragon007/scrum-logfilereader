package hu.bme.tmit.agile.logfilereader.model;

import org.joda.time.DateTime;

public class LogTimestamp {

	private DateTime dt;
	private int micro = 0;

	public LogTimestamp(int year, int month, int day, int hour, int minute, int second, int micro) {
		this.dt = new DateTime(year, month, day, hour, minute, second);
		this.micro = micro;
	}

	public DateTime getDt() {
		return dt;
	}

	public void setDt(DateTime dt) {
		this.dt = dt;
	}

	public int getMicro() {
		return micro;
	}

	public void setMicro(int micro) {
		this.micro = micro;
	}

	@Override
	public String toString() {
		return dt.getYear() + "." + dt.getMonthOfYear() + "." + dt.getDayOfMonth() + " " + dt.getHourOfDay() + ":"
				+ dt.getMinuteOfHour() + ":" + dt.getSecondOfMinute() + "." + micro;
	}
}

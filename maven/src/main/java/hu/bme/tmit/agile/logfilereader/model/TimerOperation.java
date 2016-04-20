package hu.bme.tmit.agile.logfilereader.model;

public class TimerOperation extends TtcnEvent {

	public enum EventType {
		Start("Start"), Stop("Stop"), Timeout("Timeout");
		private String type;

		EventType(String str) {
			type = str;
		};
	}

	private String name;
	private double duration;
	EventType eventType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
}

package hu.bme.tmit.agile.logfilereader.model;

public class TimerOperation extends TtcnEvent {

	public enum EventType {
		Start("Start"), Stop("Stop"), Timeout("Timeout");
		private String type;

		EventType(String str) {
			type = str;
		};

		public static EventType getEventTypeFromString(String typeString) {
			if (typeString != null) {
				for (EventType timerEventType : EventType.values()) {
					if (typeString.equalsIgnoreCase(timerEventType.type)) {
						return timerEventType;
					}
				}
			}
			return null;
		}
	}

	private String name;
	private double duration;
	EventType eventType;
	private String owner;

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

	@Override
	public String toString() {
		return timestamp.toString() + " " + sender + " " + fileName + " " + eventType + " " + name + " " + duration;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}
}

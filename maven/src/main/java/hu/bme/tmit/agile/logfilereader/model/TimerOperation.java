package hu.bme.tmit.agile.logfilereader.model;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;

public class TimerOperation extends TtcnEvent {

	public enum EventType {
		Start("Start"), Stop("Stop"), Timeout("Timeout");
		private String type;

		EventType(String str) {
			type = str;
		};

		//From String method will return you the Enum for the provided input string
	    public static EventType fromString(String parameterName) {
	        if (parameterName != null) {
	            for (EventType objType : EventType.values()) {
	                if (parameterName.equalsIgnoreCase(objType.type)) {
	                    return objType;
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

package hu.bme.tmit.agile.logfilereader.model;

public class Message extends TtcnEvent{
	
	public enum EventType {
		Send("Send"), Receive("Receive");
		private String type;

		EventType(String str) {
			type = str;
		};
	}
	
	private Long pkid;
	private String param;
	private LogTimestamp timestamp;
	private String destination;
	private String name;
	private String port;
	private EventType eventType;
	
	
	public Long getPkid() {
		return pkid;
	}
	public void setPkid(Long pkid) {
		this.pkid = pkid;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public LogTimestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LogTimestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
}

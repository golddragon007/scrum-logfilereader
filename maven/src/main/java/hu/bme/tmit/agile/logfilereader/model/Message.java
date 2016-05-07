package hu.bme.tmit.agile.logfilereader.model;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;

public class Message extends TtcnEvent {

	public enum MessageType {
		Send("Send"), Receive("Receive");
		private String type;

		MessageType(String str) {
			type = str;
		};
		
		//From String method will return you the Enum for the provided input string
	    public static MessageType fromString(String parameterName) {
	        if (parameterName != null) {
	            for (MessageType objType : MessageType.values()) {
	                if (parameterName.equalsIgnoreCase(objType.type)) {
	                    return objType;
	                }
	            }
	        }
	        return null;
	    }
	}

	private Long pkid;
	private String param;
	private String destination;
	private String name;
	private String port;
	private MessageType messageType;

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

	public MessageType getEventType() {
		return messageType;
	}

	public void setEventType(MessageType eventType) {
		this.messageType = eventType;
	}
}

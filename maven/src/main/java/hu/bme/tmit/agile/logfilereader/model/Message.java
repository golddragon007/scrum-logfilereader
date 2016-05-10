package hu.bme.tmit.agile.logfilereader.model;

public class Message extends TtcnEvent {

	public enum MessageType {
		Send("Send"), Receive("Receive");
		private String type;

		MessageType(String str) {
			type = str;
		};

		public static MessageType getMessageTypeFromString(String typeString) {
			if (typeString != null) {
				for (MessageType messType : MessageType.values()) {
					if (typeString.equalsIgnoreCase(messType.type)) {
						return messType;
					}
				}
			}
			return null;
		}
	}

	private String param;
	private String destination;
	private String name;
	private String port;
	private MessageType messageType;

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

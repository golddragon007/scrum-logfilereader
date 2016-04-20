package hu.bme.tmit.agile.logfilereader.model;

public class TtcnEvent {

	protected LogTimestamp timestamp;
	protected String sender;

	public LogTimestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LogTimestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

}

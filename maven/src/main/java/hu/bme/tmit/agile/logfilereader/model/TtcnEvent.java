package hu.bme.tmit.agile.logfilereader.model;

abstract public class TtcnEvent {

	protected LogTimestamp timestamp;
	protected String sender;
	protected String fileName;

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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
}

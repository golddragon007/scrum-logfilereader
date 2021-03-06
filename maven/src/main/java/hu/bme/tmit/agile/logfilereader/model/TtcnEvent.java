package hu.bme.tmit.agile.logfilereader.model;

public abstract class TtcnEvent implements Comparable<TtcnEvent> {

	protected LogTimestamp timestamp;
	protected String sender;
	protected String fileName;
	protected Integer micro;
	protected Integer id;

	public LogTimestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LogTimestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public Integer getMicro() {
		return micro;
	}
	
	public void setMicro(Integer micro) {
		this.micro = micro;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getTimestampString() {
		return timestamp.toDateTimeString();
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

	public int compareTo(TtcnEvent te) {
		if (getTimestamp().isEqual(te.getTimestamp())) {
			return 0;
		}
		if (getTimestamp().isBefore(te.getTimestamp())) {
			return -1;
		}
		return 1;
	}
}

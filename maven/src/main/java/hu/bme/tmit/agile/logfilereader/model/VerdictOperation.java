package hu.bme.tmit.agile.logfilereader.model;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;

public class VerdictOperation extends TtcnEvent {

	public enum VerdictType {
		Fail("fail"), Pass("pass"), Inconclusive("inconclusive");
		private String type;

		VerdictType(String str) {
			type = str;
		};

		//From String method will return you the Enum for the provided input string
	    public static VerdictType fromString(String parameterName) {
	        if (parameterName != null) {
	            for (VerdictType objType : VerdictType.values()) {
	                if (parameterName.equalsIgnoreCase(objType.type)) {
	                    return objType;
	                }
	            }
	        }
	        return null;
	    }
	}

	VerdictType verdictType;
	private String miscText;
	private String componentName;
	private int portNumber;
	private String owner;

	public String getMiscText() {
		return miscText;
	}

	public void setMiscText(String text) {
		this.miscText = text;
	}

	public VerdictType getVerdictType() {
		return this.verdictType;
	}

	public void setVerdictType(VerdictType verdictType) {
		this.verdictType = verdictType;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String component) {
		this.componentName = component;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int port) {
		this.portNumber = port;
	}

	@Override
	public String toString() {
		return timestamp.toString() + " " + sender + " " + fileName + " " + verdictType + " " + miscText + " "
				+ componentName + " " + portNumber;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}

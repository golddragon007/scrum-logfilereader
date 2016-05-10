package hu.bme.tmit.agile.logfilereader.model;

public class VerdictOperation extends TtcnEvent {

	public enum VerdictType {
		Fail("fail"), Pass("pass"), Inconclusive("inconclusive");
		private String type;

		VerdictType(String str) {
			type = str;
		};

		public static VerdictType getVerdictTypeFromString(String typeString) {
			if (typeString != null) {
				for (VerdictType vType : VerdictType.values()) {
					if (typeString.equalsIgnoreCase(vType.type)) {
						return vType;
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

package hu.bme.tmit.agile.logfilereader.model;

public class ComponentEvent extends TtcnEvent {

	public enum ComponentEventType {
		Create("create"), Terminate("terminate");
		private String type;

		ComponentEventType(String str) {
			type = str;
		};
		
		//From String method will return you the Enum for the provided input string
	    public static ComponentEventType fromString(String parameterName) {
	        if (parameterName != null) {
	            for (ComponentEventType objType : ComponentEventType.values()) {
	                if (parameterName.equalsIgnoreCase(objType.type)) {
	                    return objType;
	                }
	            }
	        }
	        return null;
	    }
	}

	public ComponentEventType cet;
	private int processID;
	private int componentReference;
	private String componentType;
	private String testcaseName;

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}

	public int getComponentReference() {
		return componentReference;
	}

	public void setComponentReference(int componentReference) {
		this.componentReference = componentReference;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getTestcaseName() {
		return testcaseName;
	}

	public void setTestcaseName(String testcaseName) {
		this.testcaseName = testcaseName;
	}

	public ComponentEventType getComponentEventType() {
		return cet;
	}

	public void setComponentEventType(ComponentEventType eventType) {
		this.cet = eventType;
	}

	@Override
	public String toString() {
		return timestamp.toString() + " " + sender + " " + fileName + " " + processID + " " + componentReference + " "
				+ componentType + " " + cet + " " + testcaseName;
	}
}

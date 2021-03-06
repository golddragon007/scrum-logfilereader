package hu.bme.tmit.agile.logfilereader.model;

public class ComponentEvent extends TtcnEvent {

	public enum ComponentEventType {
		Create("create"), Terminate("terminate");
		private String type;

		ComponentEventType(String str) {
			type = str;
		};

		public static ComponentEventType getComponentTypeFromString(String componentTypeString) {
			if (componentTypeString != null) {
				for (ComponentEventType compType : ComponentEventType.values()) {
					if (componentTypeString.equalsIgnoreCase(compType.type)) {
						return compType;
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
	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

package hu.bme.tmit.agile.logfilereader.model;

public class ComponentEvent extends TtcnEvent {

	public enum ComponentType {
		Create("create"), Terminate("terminate");
		private String type;

		ComponentType(String str) {
			type = str;
		};
	}

	public ComponentType compType;
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

	public ComponentType getCompType() {
		return compType;
	}

	public void setCompType(ComponentType eventType) {
		this.compType = eventType;
	}

	@Override
	public String toString() {
		return timestamp.toString() + " " + sender + " " + fileName + " " + processID + " " + componentReference + " "
				+ componentType + " " + compType + " " + testcaseName;
	}

}

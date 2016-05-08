package hu.bme.tmit.agile.logfilereader.controller;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent;
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;
import util.Utils;

public class CreatedComponentParser {

	public static ComponentEvent parseCreatedComponent(String[] parts) {

		int componentReference = Integer.parseInt(Utils.removeLastCharacter(parts[10]));
		String componentType = Utils.removeLastCharacter(parts[13]);
		String testcaseName = Utils.removeLastCharacter(parts[16]);
		int processID = Integer.parseInt(Utils.removeLastCharacter(parts[19]));

		ComponentEvent ce = getComponentEvent(componentReference, componentType, testcaseName, processID);
		return ce;
	}

	private static ComponentEvent getComponentEvent(int componentReference, String componentType, String testcaseName,
			int processID) {
		ComponentEvent ce = new ComponentEvent();
		ce.setComponentReference(componentReference);
		ce.setComponentType(componentType);
		ce.setTestcaseName(testcaseName);
		ce.setProcessID(processID);
		ce.setComponentEventType(ComponentEventType.Create);
		return ce;
	}
}

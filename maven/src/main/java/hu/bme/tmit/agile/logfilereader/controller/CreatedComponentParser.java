package hu.bme.tmit.agile.logfilereader.controller;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent;
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;
import util.Utils;

public class CreatedComponentParser {

	public static ComponentEvent parseCreatedComponent(String[] parts) {
		ComponentEvent ce = new ComponentEvent();

		int componentReference = Integer.parseInt(Utils.removeLastCharacter(parts[10]));
		String componentType = Utils.removeLastCharacter(parts[13]);
		String testcaseName = Utils.removeLastCharacter(parts[16]);
		int processID = Integer.parseInt(Utils.removeLastCharacter(parts[19]));

		ce.setComponentReference(componentReference);
		ce.setComponentType(componentType);
		ce.setTestcaseName(testcaseName);
		ce.setProcessID(processID);
		ce.setCompType(ComponentEventType.Create);

		return ce;
	}
}

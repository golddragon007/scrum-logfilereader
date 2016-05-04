package hu.bme.tmit.agile.logfilereader.controller;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent;
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentType;
import util.Utils;

public class TerminatedComponentParser {

	public static ComponentEvent parseTerminatedComponent(String[] parts) {
		ComponentEvent ce = new ComponentEvent();
		String componentType = Utils.removeLastCharacter(parts[8]);
		ce.setComponentType(componentType);
		ce.setCompType(ComponentType.Terminate);
		return ce;
	}
}

package hu.bme.tmit.agile.logfilereader.controller;

import hu.bme.tmit.agile.logfilereader.model.CreatedTerminatedComponent;
import hu.bme.tmit.agile.logfilereader.model.CreatedTerminatedComponent.EventType;

public class CreatedTerminatedComponentParser {

	public static CreatedTerminatedComponent parse(String[] parts, String type) {

		CreatedTerminatedComponent ctc = new CreatedTerminatedComponent();
		
		if (type == Parser.CREATED_COMPONENT) {
			int componentReference = Integer.parseInt(removeLastCharacter(parts[10]));
			String componentType = removeLastCharacter(parts[13]);
			String testcaseName = removeLastCharacter(parts[16]);
			int processID = Integer.parseInt(removeLastCharacter(parts[19]));
			
			ctc.setComponentReference(componentReference);
			ctc.setComponentType(componentType);
			ctc.setTestcaseName(testcaseName);
			ctc.setProcessID(processID);

			ctc.setEventType(EventType.Created);
		}

		
		if (type == Parser.TERMINATED_COMPONENT) {
			String componentType = removeLastCharacter(parts[8]);

			ctc.setComponentType(componentType);
			ctc.setEventType(EventType.Terminated);
		}
		
		return ctc;
	}

	private static String removeLastCharacter(String word) {
		return word.substring(0, word.length() - 1);
	}

}

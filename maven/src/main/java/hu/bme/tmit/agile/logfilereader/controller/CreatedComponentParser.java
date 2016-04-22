package hu.bme.tmit.agile.logfilereader.controller;

import hu.bme.tmit.agile.logfilereader.model.CreatedComponent;

public class CreatedComponentParser {

	public static CreatedComponent parseCreated(String[] parts) {

		int componentReference = Integer.parseInt(removeLastCharacter(parts[10]));
		String componentType = removeLastCharacter(parts[13]);
		String testcaseName = removeLastCharacter(parts[16]);
		int processID = Integer.parseInt(removeLastCharacter(parts[19]));

		CreatedComponent cc = new CreatedComponent();

		cc.setComponentReference(componentReference);
		cc.setComponentType(componentType);
		cc.setTestcaseName(testcaseName);
		cc.setProcessID(processID);

		return cc;

	}

	private static String removeLastCharacter(String word) {
		return word.substring(0, word.length() - 1);
	}

}

package hu.bme.tmit.agile.logfilereader.controller;

import hu.bme.tmit.agile.logfilereader.model.TerminatingComponent;;

public class TerminatingComponentParser {

	public static TerminatingComponent parseTerminating(String[] parts) {

		String componentType = removeLastCharacter(parts[8]);

		TerminatingComponent tc = new TerminatingComponent();

		tc.setComponentType(componentType);

		return tc;

	}

	private static String removeLastCharacter(String word) {
		return word.substring(0, word.length() - 1);
	}

}

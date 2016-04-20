package hu.bme.tmit.agile.logfilereader.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import util.RegexpPatterns;

public class VerdictParser {

	// ez így szar pl.
	// 2014/Oct/24 19:53:07.322255 2464 VERDICTOP - Final
	// verdict of PTC: pass reason: "Life is beautiful!"
	// miatt
	public static VerdictOperation parseVerdict(String[] parts) {
		String operationType = parts[3];
		String miscText = parts[5];
		String verdict = parts[7];
		// parseComponentAndPort(parts[4]); bugos
		return new VerdictOperation();
	}

	private static void parseComponentAndPort(String word) {
		String componentName = null, portNumber = null;
		Pattern p = Pattern.compile(RegexpPatterns.componentAndPortPattern);
		Matcher m = p.matcher(word);
		if (m.matches()) {
			componentName = m.group(0);
			portNumber = m.group(1);
		}
	}

}

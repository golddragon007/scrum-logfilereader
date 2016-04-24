package hu.bme.tmit.agile.logfilereader.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import util.RegexpPatterns;

public class VerdictParser {


	public static VerdictOperation parseVerdict(String parts) {
		String componentName=null, miscText=null, verdict=null;
		int portNumber;

		Pattern p = Pattern.compile(RegexpPatterns.verdictData);
		
		Matcher m = p.matcher(parts);
		VerdictOperation vo = new VerdictOperation();
		
		if (m.matches()) {
			componentName = m.group(1);
			portNumber = Integer.parseInt(m.group(2));
			miscText = m.group(3);
			verdict = m.group(4);
			
			vo.setComponentName(componentName);
			vo.setPortNumber(portNumber);
			vo.setMiscText(miscText);
			vo.setVerdict(verdict);
		}
		
		return (vo);
	}

}

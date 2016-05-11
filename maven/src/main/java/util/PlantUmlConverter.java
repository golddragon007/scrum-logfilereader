package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation.VerdictType;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUmlConverter {

	private static final String ENCODING = "UTF-8";

	private static final String PLANTUML_STRING_START = "@startuml\n";
	private static final String PLANTUML_STRING_END = "@enduml\n";

	private static final String RNOTE_START = "rnote over ";
	private static final String RNOTE_END = "endrnote";

	private static final String HNOTE_START = "hnote over ";

	private static final String RGB_YELLOW = "#FFFF00";
	private static final String RGB_RED = "#FF0000";
	private static final String RGB_GREEN = "#00FF00";
	
	private static Map<String, String> matching = null;

	public static void convert(TreeSet<TtcnEvent> eventSet) throws UnsupportedEncodingException, IOException {
		matching = new HashMap<String, String>();
		String plantUmlString = getPlantUmlString(eventSet);
		getSvgDocument(plantUmlString);
	}

	private static String getPlantUmlString(TreeSet<TtcnEvent> eventSet) {
		String plantUmlString = PLANTUML_STRING_START;
		int limit = 50;
		int i = 0, cycle_count = 0;
		plantUmlString += "participant mtc\nparticipant hc\nparticipant system\n";

		for (TtcnEvent event : eventSet) {
			if (event instanceof Message) {
				if (!event.getSender().contains(":")) {
					plantUmlString += getMessageString(event, cycle_count);
					i++;
				}
			}
			if (event instanceof VerdictOperation) {
				plantUmlString += getVerdictString(event);
				i++;
			}
			if (event instanceof TimerOperation) {
				plantUmlString += getTimerString(event);
				i++;
			}
			if (event instanceof ComponentEvent) {
				plantUmlString += getComponentString(event);
				i++;
			}
			if (i > limit) {
				break;
			}
			cycle_count++;
		}
		plantUmlString += PLANTUML_STRING_END;

		return plantUmlString;
	}
	
	private static String getComponentWithType(String componentName) {
		String fromMatching = matching.get(componentName);
		if (fromMatching != null && fromMatching != "") {
			return "\"" + fromMatching + "\"";
		}
		
		return "\"" + componentName + "\"";
	}

	private static String getMessageString(TtcnEvent event, int rowArrayId) {
		return (getComponentWithType(event.getSender()) + " -> " + getComponentWithType(((Message) event).getDestination()) + " : " + rowArrayId
				+ ((Message) event).getName() + "\n");
	}
	
	private static String getComponentString(TtcnEvent event) {
		ComponentEvent ce = (ComponentEvent) event;
		matching.put(Integer.toString(ce.getComponentReference()), ce.getComponentReference() + " : " + ce.getComponentType());
		return ("Create " + getComponentWithType(Integer.toString(ce.getComponentReference())) + "\n" + ce.getSender() + " -> " + getComponentWithType(Integer.toString(ce.getComponentReference())) + " : " + ce.getComponentEventType() + "\n");
	}

	private static String getVerdictString(TtcnEvent event) {
		if (((VerdictOperation) event).getVerdictType() == VerdictType.Pass) {
			return getVerdictStringByType(event, RGB_GREEN);
		} else if (((VerdictOperation) event).getVerdictType() == VerdictType.Fail) {
			return getVerdictStringByType(event, RGB_RED);
		} else {
			return getVerdictStringByType(event, RGB_YELLOW);
		}
	}

	private static String getVerdictStringByType(TtcnEvent event, String color) {
		return HNOTE_START + getComponentWithType(event.getSender()) + color + ": " + ((VerdictOperation) event).getVerdictType().toString()
				+ "\n";
	}

	private static String getTimerString(TtcnEvent event) {
		if (((TimerOperation) event).getEventType() == TimerOperation.EventType.Start) {
			return getTimerStringByType(event, RGB_GREEN);
		} else if (((TimerOperation) event).getEventType() == TimerOperation.EventType.Stop) {
			return getTimerStringByType(event, RGB_RED);
		} else {
			return getTimerStringByType(event, RGB_YELLOW);
		}
	}

	private static String getTimerStringByType(TtcnEvent event, String color) {
		return (RNOTE_START + getComponentWithType(((TimerOperation) event).getSender()) + color + "\n" + ((TimerOperation) event).getName()
				+ "\n" + ((TimerOperation) event).getEventType() + " " + ((TimerOperation) event).getDuration() + " s\n"
				+ RNOTE_END + "\n");
	}

	private static void getSvgDocument(String plantUmlString) throws IOException, UnsupportedEncodingException {
		SourceStringReader reader = new SourceStringReader(plantUmlString);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
		final String svg = new String(os.toByteArray(), Charset.forName(ENCODING));

		String svgTemp = svg;
		List<String> matches = new ArrayList<String>();
		Matcher m = Pattern.compile(">[0-9]+@").matcher(svgTemp);
		while (m.find()) {
			matches.add(m.group());
		}
		for (String string : matches) {
			String id = string.substring(1, string.length() - 1);
			svgTemp = svgTemp.replaceAll(string, " onclick='alert(\"" + id + "\")'" + string);
		}
		os.close();

		PrintWriter out = new PrintWriter("temp_sequence_svg.txt");
		out.print(svgTemp);
		out.close();
	}
}

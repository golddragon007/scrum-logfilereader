package util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation.VerdictType;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUmlConverter {

	private static final String ONCLICK_END = "\")'";
	private static final String ONCLICK_BEGIN = " onclick='alert(\"";

	private static final String PARTICIPANT_SYSTEM = "participant system";
	private static final String PARTICIPANT_HC = "participant hc";
	private static final String PARTICIPANT_MTC = "participant mtc";

	private static final String NEW_LINE = "\n";

	private static final String ENCODING = "UTF-8";

	private static final String PLANTUML_STRING_START = "@startuml\n";
	private static final String PLANTUML_STRING_END = "@enduml\n";

	private static final String RNOTE_START = "rnote over ";
	private static final String RNOTE_END = "endrnote";

	private static final String HNOTE_START = "hnote over ";

	private static final String RGB_YELLOW = "#FFFF00";
	private static final String RGB_RED = "#FF0000";
	private static final String RGB_GREEN = "#00FF00";

	private static final String TEMP_SEQUENCE_SVG = "temp_sequence_svg.txt";

	private static Map<String, String> matching = null;

	public static void convertToFile(TreeSet<TtcnEvent> eventSet) throws UnsupportedEncodingException, IOException {
		matching = new HashMap<String, String>();
		String plantUmlString = getPlantUmlString(eventSet);
		writeSvgToFile(plantUmlString);
	}

	private static String getPlantUmlString(TreeSet<TtcnEvent> eventSet) {
		String plantUmlString = PLANTUML_STRING_START;
		int cycleCount = 0;
		plantUmlString += PARTICIPANT_MTC + NEW_LINE + PARTICIPANT_HC + NEW_LINE + PARTICIPANT_SYSTEM + NEW_LINE;

		for (TtcnEvent event : eventSet) {
			if (event instanceof Message) {
				if (!event.getSender().contains(":")) {
					plantUmlString += getMessageString(event, cycleCount);
				}
			}
			if (event instanceof VerdictOperation) {
				plantUmlString += getVerdictString(event);
			}
			if (event instanceof TimerOperation) {
				plantUmlString += getTimerString(event);
			}
			if (event instanceof ComponentEvent) {
				plantUmlString += getComponentString(event);
			}

			cycleCount++;
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
		return (getComponentWithType(event.getSender()) + " -> "
				+ getComponentWithType(((Message) event).getDestination()) + " : " + rowArrayId
				+ ((Message) event).getName() + NEW_LINE);
	}

	private static String getComponentString(TtcnEvent event) {
		ComponentEvent ce = (ComponentEvent) event;
		if (ce.getComponentEventType() == ComponentEventType.Create) {
			matching.put(Integer.toString(ce.getComponentReference()),
					ce.getComponentReference() + " : " + ce.getComponentType());
			return ("Create " + getComponentWithType(Integer.toString(ce.getComponentReference())) + NEW_LINE
					+ ce.getSender() + " -> " + getComponentWithType(Integer.toString(ce.getComponentReference()))
					+ " : " + ce.getComponentEventType() + NEW_LINE);
		} else {
			return "destroy " + getComponentWithType(ce.getSender()) + NEW_LINE;
		}
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
		return HNOTE_START + getComponentWithType(event.getSender()) + color + ": "
				+ ((VerdictOperation) event).getVerdictType().toString() + NEW_LINE;
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
		return (RNOTE_START + getComponentWithType(((TimerOperation) event).getSender()) + color + NEW_LINE
				+ ((TimerOperation) event).getName() + NEW_LINE + ((TimerOperation) event).getEventType() + " "
				+ ((TimerOperation) event).getDuration() + " s" + NEW_LINE + RNOTE_END + NEW_LINE);
	}

	private static void writeSvgToFile(String plantUmlString) throws IOException, UnsupportedEncodingException {
		String svg = getSvgString(plantUmlString);
		svg = addOnClickToSvg(svg);
		writeToFile(svg);
	}

	private static String getSvgString(String plantUmlString) throws IOException {
		SourceStringReader reader = new SourceStringReader(plantUmlString);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
		String svg = new String(os.toByteArray(), Charset.forName(ENCODING));
		os.close();
		return svg;
	}

	private static String addOnClickToSvg(String svg) {
		List<String> matches = new ArrayList<String>();
		Matcher m = Pattern.compile(">[0-9]+@").matcher(svg);
		while (m.find()) {
			matches.add(m.group());
		}
		for (String string : matches) {
			String id = string.substring(1, string.length() - 1);
			svg = svg.replaceAll(string, ONCLICK_BEGIN + id + ONCLICK_END + string);
		}
		return svg;
	}

	private static void writeToFile(String svg) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(TEMP_SEQUENCE_SVG);
		out.print(svg);
		out.close();
	}
}

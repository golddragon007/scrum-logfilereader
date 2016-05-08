package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.TreeSet;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

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

	public static SVGDocument convert(TreeSet<TtcnEvent> eventSet) throws UnsupportedEncodingException, IOException {
		String plantUmlString = getPlantUmlString(eventSet);
		return getSvgDocument(plantUmlString);
	}

	private static String getPlantUmlString(TreeSet<TtcnEvent> eventSet) {
		String plantUmlString = PLANTUML_STRING_START;
		int limit = 50;
		int i = 0;

		for (TtcnEvent event : eventSet) {
			if (event instanceof Message) {
				if (!event.getSender().contains(":")) {
					plantUmlString += getMessageString(event);
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
			if (i > limit) {
				break;
			}
		}
		plantUmlString += PLANTUML_STRING_END;

		return plantUmlString;
	}

	private static String getMessageString(TtcnEvent event) {
		return ("\"" + event.getSender() + "\" -> \"" + ((Message) event).getDestination() + "\" : "
				+ "Simple message \n");
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
		return HNOTE_START + event.getSender() + color + ": " + ((VerdictOperation) event).getVerdictType().toString()
				+ "\n";
	}

	private static String getTimerString(TtcnEvent event) {
		return (RNOTE_START + ((TimerOperation) event).getSender() + "\n" + ((TimerOperation) event).getName() + "\n"
				+ ((TimerOperation) event).getEventType() + " " + ((TimerOperation) event).getDuration() + " s\n"
				+ RNOTE_END + "\n");
	}

	private static SVGDocument getSvgDocument(String plantUmlString) throws IOException, UnsupportedEncodingException {
		SourceStringReader reader = new SourceStringReader(plantUmlString);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
		final String svg = new String(os.toByteArray(), Charset.forName(ENCODING));
		os.close();
		String svgToCanvas = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(svgToCanvas);
		SVGDocument document = factory.createSVGDocument("", new ByteArrayInputStream(svg.getBytes(ENCODING)));
		return document;
	}

}

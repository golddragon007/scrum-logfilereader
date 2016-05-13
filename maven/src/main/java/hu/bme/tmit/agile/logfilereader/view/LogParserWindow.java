package hu.bme.tmit.agile.logfilereader.view;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.batik.swing.JSVGCanvas;

import hu.bme.tmit.agile.logfilereader.controller.Parser;
import hu.bme.tmit.agile.logfilereader.dao.ParserDAO;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import util.PlantUmlConverter;
import util.StatusPanelMessage;
import util.Utils;

public class LogParserWindow extends LogParserWindowSkeleton {

	private static final int OVERWRITE = 0;
	private static final String USER_DIRECTORY_PROPERTY = "user.dir";
	private static final String SEQUENCE_SVG_FILENAME = "temp_sequence_svg.txt";

	private TreeSet<TtcnEvent> eventSet = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogParserWindow window = new LogParserWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private LogParserWindow() {
		initialize();
	}

	@Override
	protected void drawSequenceFromFile() {
		File selectedFile;
		if ((selectedFile = getSelectedFile()) != null) {
			fileName = selectedFile.getName();
			try {
				Parser parser = new Parser();
				parser.parse(selectedFile.getAbsolutePath());
				eventSet = parser.getEventSet();
				drawSequence();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			statusLabel.setText(StatusPanelMessage.CANCELLED_BY_USER);
		}
	}

	private File getSelectedFile() {
		final JFileChooser jFileChooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty(USER_DIRECTORY_PROPERTY));
		jFileChooser.setCurrentDirectory(workingDirectory);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooser.setFileFilter(new FileNameExtensionFilter("log files", "txt"));
		if (jFileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return jFileChooser.getSelectedFile();
		}
		return null;
	}

	private void drawSequence() throws UnsupportedEncodingException, IOException {
		eventArray = eventSet.toArray(new TtcnEvent[eventSet.size()]);
		PlantUmlConverter.convertToFile(eventSet);
		svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		tempSequenceSvg = new File(SEQUENCE_SVG_FILENAME);
		if (tempSequenceSvg != null) {
			svgCanvas.setURI(tempSequenceSvg.toURI().toString());
		}
	}

	@Override
	protected void drawSequenceFromDatabase() {
		ParserDAO pdao = new ParserDAO();
		String selectedFile = getSelectedFileFromDb(pdao);
		if ((selectedFile != null) && (selectedFile.length() > 0)) {
			try {
				fileName = selectedFile;
				eventSet = pdao.loadTtcnEvent(selectedFile);
				drawSequence();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private String getSelectedFileFromDb(ParserDAO pdao) {
		Object[] storedFiles = pdao.getSavedFileNames();
		if (storedFiles.length > 0) {
			String selectedFile = (String) JOptionPane.showInputDialog(frame, "File name:", LOAD_FROM_DATABASE_LABEL,
					JOptionPane.PLAIN_MESSAGE, null, storedFiles, storedFiles[0]);
			return selectedFile;
		} else {
			JOptionPane.showMessageDialog(frame, "There are no saved files found!", LOAD_FROM_DATABASE_LABEL,
					JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	@Override
	protected void saveToDatabase() {
		if (fileName != null && !fileName.equals("")) {
			try {
				ParserDAO pdao = new ParserDAO();
				boolean doesFileExist = pdao.existTtcnEvent(fileName);

				if (doesFileExist) {
					int selectedFacility = getOverwritePermission();
					if (selectedFacility == OVERWRITE) {
						pdao.removeTtcnEvent(fileName);
						pdao.saveTtcnEventMulti(eventSet);
						showSaveToDbCompletedMessage();
					}
				} else {
					pdao.saveTtcnEventMulti(eventSet);
					showSaveToDbCompletedMessage();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(frame,
					"First you need to open a file or load it from database to use this function!",
					SAVE_TO_DATABASE_LABEL, JOptionPane.ERROR_MESSAGE);
		}
	}

	private int getOverwritePermission() {
		Object[] facilities = { "Overwrite it", "Cancel" };
		int selectedFacility = JOptionPane.showOptionDialog(frame,
				"The current filename exist in the database, what would you like to do?", SAVE_TO_DATABASE_LABEL,
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, facilities, facilities[0]);
		return selectedFacility;
	}

	private void showSaveToDbCompletedMessage() {
		JOptionPane.showMessageDialog(frame, "Save completed!", SAVE_TO_DATABASE_LABEL,
				JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	protected void generateTree(String messageParameter) {
		String[] lines = messageParameter.split("\r\n|\n|\r");
		Deque<DefaultMutableTreeNode> stack = new ArrayDeque<DefaultMutableTreeNode>();

		root.removeAllChildren();

		DefaultMutableTreeNode actualElement = root;
		int rowCount = 0;

		for (String line : lines) {

			if (line.contains(" := ")) {
				String[] groups = line.split(" := ");
				String group1 = groups[0].trim();
				String group2 = groups[1].trim();

				if (group2.substring(group2.length() - 1).equals(",")) {
					group2 = Utils.removeLastCharacter(group2);
				}

				if (group2.equals("{")) {
					if (group1 != null && !group1.equals("")) {
						if (actualElement != null) {
							stack.push(actualElement);
						}
						actualElement = new DefaultMutableTreeNode(group1);
					}
				}

				else if (group2.equals("{ }")) {
					DefaultMutableTreeNode base = new DefaultMutableTreeNode(group1);
					base.add(new DefaultMutableTreeNode("[Empty Object]"));
					actualElement.add(base);
				}

				else {
					actualElement.add(new DefaultMutableTreeNode(group1 + " = " + group2));
				}
			}

			else if ((line.trim().equals("}") || line.trim().equals("},")) && lines.length - 1 > rowCount) {
				DefaultMutableTreeNode prev = stack.pop();
				prev.add(actualElement);
				actualElement = prev;
			}

			else if (line.trim().equals("{")) {
				stack.push(actualElement);
				actualElement = new DefaultMutableTreeNode("[ArrayElement]");
			}

			else if (line.length() > 1) {
				actualElement.add(new DefaultMutableTreeNode(line.replaceAll("} ", "").trim()));
			}
			rowCount++;
		}

		((DefaultTreeModel) jTree.getModel()).reload();

		DefaultMutableTreeNode currentNode = root.getNextNode();
		do {
			if (currentNode.getLevel() == 0) {
				jTree.expandPath(new TreePath(currentNode.getPath()));
			}
			currentNode = currentNode.getNextNode();
		} while (currentNode != null);
	}

}

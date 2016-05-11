package hu.bme.tmit.agile.logfilereader.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.SVGUserAgent;
import org.apache.batik.swing.svg.SVGUserAgentAdapter;

import hu.bme.tmit.agile.logfilereader.controller.Parser;
import hu.bme.tmit.agile.logfilereader.dao.ParserDAO;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import util.PlantUmlConverter;
import util.StatusPanelMessage;

public class LogParserWindow {

	private static final String MAIN_WINDOW_TITLE = "Parser and sequence drawer";
	private static final String LOAD_FROM_DATABASE_LABEL = "Load from database...";
	private static final String SAVE_TO_DATABASE_LABEL = "Save to database...";

	private static final int WINDOW_VERTICAL = 50;
	private static final int WINDOW_HORIZONTAL = 50;
	private static final int WINDOW_HEIGHT = 900;
	private static final int WINDOW_WIDTH = 1200;

	private static final int STATUS_PANEL_HEIGHT = 16;

	private static final String USER_DIRECTORY_PROPERTY = "user.dir";

	private JFrame frame = new JFrame(MAIN_WINDOW_TITLE);

	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem openFileMenuItem = new JMenuItem("Open file...");
	private JMenuItem loadFromDatabaseMenuItem = new JMenuItem(LOAD_FROM_DATABASE_LABEL);
	private JMenuItem saveToDatabaseMenuItem = new JMenuItem(SAVE_TO_DATABASE_LABEL);
	private JMenuItem exitMenuItem = new JMenuItem("Exit");

	private JPanel statusPanel = new JPanel();
	private JLabel statusLabel = new JLabel();

	private JPanel treePanel = new JPanel();
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parameters");
	private JTree jTree = new JTree(root);
	private JScrollPane jScrollPane = new JScrollPane(jTree);
	private JLabel paramsLabel = new JLabel();

	private String fileName;
	private TreeSet<TtcnEvent> eventSet = null;
	private TtcnEvent[] eventArray = null;

	private static final String SEQUENCE_SVG_FILENAME = "temp_sequence_svg.txt";
	private File tempSequenceSvg;
	
	private void generateTree(String text) {
		String[] lines = text.split("\r\n|\n|\r");
		Deque<DefaultMutableTreeNode> stack = new ArrayDeque<DefaultMutableTreeNode>();
		
		// Delete actual treeView items.
		root.removeAllChildren();
		
		// Set actual working element to root element.
		DefaultMutableTreeNode actualElement = root;
		int rowCount = 0;
		
		for (String line : lines) {
			// If it is a key := value pair.
			if (line.contains(" := ")) {
				String[] groups = line.split(" := ");
				String group1 = groups[0].trim(); // key
				String group2 = groups[1].trim(); // value
				
				// Remove "," from the end of the value.
				if (group2.substring(group2.length() -1).equals(",")) {
					group2 = group2.substring(0, group2.length() - 1);
				}
				
				// If the value is an Object or an Array.
				if (group2.equals("{")) {
					if (group1 != null && group1 != "") {
						if (actualElement != null) {
							stack.push(actualElement);							
						}
						actualElement = new DefaultMutableTreeNode(group1);
					}
				}
				// If the value is an empty Object or an Array.
				else if (group2.equals("{ }")) {
					DefaultMutableTreeNode base = new DefaultMutableTreeNode(group1);
					base.add(new DefaultMutableTreeNode("[Empty Object]"));
					actualElement.add(base);
				}
				// If it's a simple type value.
				else {
					actualElement.add(new DefaultMutableTreeNode(group1 + " = " + group2));
				}
			}
			// If it's fall back a level.
			else if ((line.trim().equals("}") || line.trim().equals("},")) && lines.length - 1 > rowCount) {
				DefaultMutableTreeNode prev = stack.pop();
				prev.add(actualElement);
				actualElement = prev;
			}
			// If it's an ArrayElement, open a new level (increment level).
			else if (line.trim().equals("{")) {
				stack.push(actualElement);
				actualElement = new DefaultMutableTreeNode("[ArrayElement]");
			}
			// Last line "only".
			else if (line.length() > 1) {
				actualElement.add(new DefaultMutableTreeNode(line.replaceAll("} ", "").trim()));
			}
			rowCount++;
		}
		
		// Refresh treeView view.
		((DefaultTreeModel)jTree.getModel()).reload();
		
		// Open 0 level element. (this is the root), set 1 to open the level 1 elements too, ect.
	    DefaultMutableTreeNode currentNode = root.getNextNode();
	    do {
	       if (currentNode.getLevel()==0) 
	    	   jTree.expandPath(new TreePath(currentNode.getPath()));
	       currentNode = currentNode.getNextNode();
	       }
	    while (currentNode != null);
	}
	
	private SVGUserAgent ua = new SVGUserAgentAdapter() {
		public void showAlert(String id) {
			System.out.println(id);
			System.out.println(eventArray[Integer.parseInt(id)]);
			Message m = (Message) eventArray[Integer.parseInt(id)];
			//paramsLabel.setText(m.getParam());

			generateTree(m.getParam());
		}
	};

	private JSVGCanvas svgCanvas = new JSVGCanvas(ua, true, true);

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

	private void initialize() {
		setFrame();
		setMenuBar();
		setFileMenu();
		addMenuItemListeners();
		setStatusPanel();
		addCanvasListeners();
		setTreePanel();
	}

	private void setFrame() {
		frame.setBounds(WINDOW_HORIZONTAL, WINDOW_VERTICAL, WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		frame.add(svgCanvas, BorderLayout.CENTER);
		frame.add(treePanel, BorderLayout.EAST);
	}

	private void setMenuBar() {
		menuBar.add(fileMenu);
	}

	private void setFileMenu() {
		fileMenu.add(openFileMenuItem);
		fileMenu.add(loadFromDatabaseMenuItem);
		fileMenu.add(saveToDatabaseMenuItem);
		fileMenu.add(exitMenuItem);
	}

	private void addMenuItemListeners() {
		addActionListenerToOpenFileMenuItem();
		addActionListenerToLoadFromDatabaseMenuItem();
		addActionListenerToSaveToDatabaseMenuItem();
		addActionListenerToExitMenuItem();
	}

	private void addActionListenerToOpenFileMenuItem() {
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawSequenceFromFile();
			}
		});
	}

	private void drawSequenceFromFile() {
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

	private void addActionListenerToLoadFromDatabaseMenuItem() {
		loadFromDatabaseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawSequenceFromDatabase();
			}
		});
	}

	private void drawSequenceFromDatabase() {
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

	private void addActionListenerToSaveToDatabaseMenuItem() {
		saveToDatabaseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveToDatabase();
			}
		});
	}

	private void saveToDatabase() {
		if (fileName != null && fileName != "") {
			try {
				ParserDAO pdao = new ParserDAO();
				boolean doesFileExist = pdao.existTtcnEvent(fileName);

				if (doesFileExist) {
					Object[] facilities = { "Overwrite it", "Cancel" };
					int selectedFacility = JOptionPane.showOptionDialog(frame,
							"The current filename exist in the database, what would you like to do?",
							SAVE_TO_DATABASE_LABEL, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
							facilities, facilities[0]);

					if (selectedFacility == 0) {
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

	private void showSaveToDbCompletedMessage() {
		JOptionPane.showMessageDialog(frame, "Save completed!", SAVE_TO_DATABASE_LABEL,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void addActionListenerToExitMenuItem() {
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
	}

	private void setStatusPanel() {
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setPreferredSize(new Dimension(frame.getWidth(), STATUS_PANEL_HEIGHT));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
	}

	private void setTreePanel() {
		treePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		treePanel.setPreferredSize(new Dimension(frame.getWidth() / 3, frame.getHeight()));
		treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.X_AXIS));
		treePanel.add(paramsLabel);
		treePanel.add(jScrollPane);
	}

	private void addCanvasListeners() {
		addCanvasLoaderListener();
		addCanvasBuilderListener();
		addCanvasRendererListener();
	}

	private void addCanvasLoaderListener() {
		svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
			@Override
			public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
				statusLabel.setText(StatusPanelMessage.DOCUMENT_LOADING);
			}

			@Override
			public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
				statusLabel.setText(StatusPanelMessage.DOCUMENT_LOADED);
				tempSequenceSvg.delete();
			}
		});
	}

	private void addCanvasBuilderListener() {
		svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
			@Override
			public void gvtBuildStarted(GVTTreeBuilderEvent e) {
				statusLabel.setText(StatusPanelMessage.BUILDING);
			}

			@Override
			public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
				statusLabel.setText(StatusPanelMessage.BUILD_DONE);
				// addTextClickListeners();
				// frame.pack();
			}
		});
	}

	private void addCanvasRendererListener() {
		svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
			@Override
			public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
				statusLabel.setText(StatusPanelMessage.RENDERING);
			}

			@Override
			public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
				statusLabel.setText(fileName + StatusPanelMessage.PARSE_DONE + StatusPanelMessage.HINT);
			}
		});
	}
}

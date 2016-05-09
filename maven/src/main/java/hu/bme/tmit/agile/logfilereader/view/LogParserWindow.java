package hu.bme.tmit.agile.logfilereader.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.w3c.dom.svg.SVGDocument;

import hu.bme.tmit.agile.logfilereader.controller.Parser;
import hu.bme.tmit.agile.logfilereader.dao.ParserDAO;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import util.PlantUmlConverter;
import util.StatusPanelMessage;

public class LogParserWindow {

	private static final int WINDOW_VERTICAL = 50;
	private static final int WINDOW_HORIZONTAL = 50;
	private static final int WINDOW_HEIGHT = 900;
	private static final int WINDOW_WIDTH = 1200;

	private static final int STATUS_PANEL_HEIGHT = 16;

	private static final String USER_DIRECTORY_PROPERTY = "user.dir";

	private JFrame frame = new JFrame("Parser and sequence drawer");;
	private JMenu fileMenu = new JMenu("File");
	private JMenuBar menuBar = new JMenuBar();
	private JMenuItem openFileMenuItem = new JMenuItem("Open file...");
	private JMenuItem loadFromDatabaseMenuItem = new JMenuItem("Load from database...");
	private JMenuItem saveToDatabaseMenuItem = new JMenuItem("Save to database...");
	private JMenuItem exitMenuItem = new JMenuItem("Exit");
	private JPanel statusPanel = new JPanel();
	private JLabel statusLabel = new JLabel();

	private String fileName;
	private TreeSet<TtcnEvent> eventSet = null;

	private JSVGCanvas svgCanvas = new JSVGCanvas();

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
	}

	private void setFrame() {
		frame.setBounds(WINDOW_HORIZONTAL, WINDOW_VERTICAL, WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		frame.add(svgCanvas, BorderLayout.CENTER);
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
		addActionListenerToMenuItemExit();
	}

	private void addActionListenerToOpenFileMenuItem() {
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File selectedFile;
				if ((selectedFile = getSelectedFile()) != null) {
					fileName = selectedFile.getName();
					try {
						Parser parser = new Parser();
						parser.parse(selectedFile.getAbsolutePath());
						eventSet = parser.getEventSet();
						SVGDocument document = PlantUmlConverter.convert(eventSet);
						svgCanvas.setSVGDocument(document);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					statusLabel.setText(StatusPanelMessage.CANCELLED_BY_USER);
				}
			}
		});
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

	private void addActionListenerToLoadFromDatabaseMenuItem() {
		loadFromDatabaseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParserDAO pdao = new ParserDAO();
				Object[] possibilities = pdao.getSavedFileNames();

				if (possibilities.length > 0) {
					String s = (String) JOptionPane.showInputDialog(frame, "File name:", "Load from DB",
							JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);

					// If a string was returned, say so.
					if ((s != null) && (s.length() > 0)) {
						try {
							fileName = s;
							eventSet = pdao.loadTtcnEvent(s);
							SVGDocument document = PlantUmlConverter.convert(eventSet);
							svgCanvas.setSVGDocument(document);
						} catch (IOException ex) {
							ex.printStackTrace();
						}

						return;
					}

				} else {
					JOptionPane.showMessageDialog(frame, "There's no saved file(s) found!", "Load from DB",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private void addActionListenerToSaveToDatabaseMenuItem() {
		saveToDatabaseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParserDAO pdao = new ParserDAO();
				
				if (fileName != null && fileName != "") {
					try {
						boolean exist = pdao.existTtcnEvent(fileName);
						
						if (exist) {
							Object[] options = {"Overwrite it",
		                    "Cancel"};
							int n = JOptionPane.showOptionDialog(frame,
							    "The current filename exist in the database, what would you like to do?",
							    "Save to database...",
							    JOptionPane.YES_NO_OPTION,
							    JOptionPane.WARNING_MESSAGE,
							    null,     //do not use a custom Icon
							    options,  //the titles of buttons
							    options[0]); //default button title
							
							if (n == 0) {
								pdao.removeTtcnEvent(fileName);

								pdao.saveTtcnEventMulti(eventSet);
								JOptionPane.showMessageDialog(frame,
									    "Save completted!",
									    "Save to database...",
									    JOptionPane.INFORMATION_MESSAGE);
							}
						}
						else {
							pdao.saveTtcnEventMulti(eventSet);
							JOptionPane.showMessageDialog(frame,
								    "Save completted!",
								    "Save to database...",
								    JOptionPane.INFORMATION_MESSAGE);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(frame,
					    "First you need to open a file or load it from database to use this function!",
					    "Save to database...",
					    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private void addActionListenerToMenuItemExit() {
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

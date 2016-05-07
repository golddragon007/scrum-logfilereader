package hu.bme.tmit.agile.logfilereader.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import hu.bme.tmit.agile.logfilereader.controller.Parser;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class LogParserWindow {

	private static final int WINDOW_VERTICAL = 100;
	private static final int WINDOW_HORIZONTAL = 100;
	private static final int WINDOW_HEIGHT = 600;
	private static final int WINDOW_WIDTH = 800;

	private static final int STATUS_PANEL_HEIGHT = 16;

	private static final String USER_DIRECTORY_PROPERTY = "user.dir";

	private JFrame frame = new JFrame("Parser and sequence drawer");;
	private JMenu fileMenu = new JMenu("File");
	private JMenuBar menuBar = new JMenuBar();
	private JMenuItem openFileMenuItem = new JMenuItem("Open file");
	private JMenuItem loadFromDatabaseMenuItem = new JMenuItem("Load from database");
	private JMenuItem exitMenuItem = new JMenuItem("Exit");
	private JPanel statusPanel = new JPanel();
	private JLabel statusLabel = new JLabel();

	private String fileName;

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
		setfileMenu();
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

	private void setfileMenu() {
		fileMenu.add(openFileMenuItem);
		fileMenu.add(loadFromDatabaseMenuItem);
		fileMenu.add(exitMenuItem);
	}

	private void addMenuItemListeners() {
		addActionListenerToOpenFileMenuItem();
		addActionListenerToLoadFromDatabaseMenuItem();
		addActionListenerToMenuItemExit();
	}

	private void addActionListenerToOpenFileMenuItem() {
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser jFileChooser = new JFileChooser();
				File workingDirectory = new File(System.getProperty(USER_DIRECTORY_PROPERTY));
				jFileChooser.setCurrentDirectory(workingDirectory);
				if (jFileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File file = jFileChooser.getSelectedFile();
					fileName = file.getName();

					try {
						Parser parser = new Parser();
						parser.parse(file.getAbsolutePath());

						/*
						 * for (TtcnEvent event : parser.getEventList()) {
						 * dao.saveTtcnEvent(event); }
						 */

						// Egyszeru pelda a kirajzolasra

						String source = "@startuml\n";

						// teszt kirajzolas miatt 20 sor
						int limit = 15;
						int i = 0;

						for (TtcnEvent event : parser.getEventList()) {
							if (event instanceof Message && i < limit) {
								if (!event.getSender().contains(":")) {
									source += event.getSender() + " -> " + ((Message) event).getDestination() + " : "
											+ "Simple message \n";
									i++;
								}
							}
						}

						source += "@enduml\n";

						SourceStringReader reader = new SourceStringReader(source);
						final ByteArrayOutputStream os = new ByteArrayOutputStream();
						// Write the first image to "os"
						String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
						os.close();

						// The XML is stored into svg
						final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));

						String svgToCanvas = XMLResourceDescriptor.getXMLParserClassName();
						SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(svgToCanvas);
						SVGDocument document = factory.createSVGDocument("",
								new ByteArrayInputStream(svg.getBytes("UTF-8")));

						svgCanvas.setSVGDocument(document);

						// svgCanvas.setDocument(svg);
						// svgCanvas.setURI(file.toURL().toString());

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					statusLabel.setText(StatusPanelMessage.CANCELLED_BY_USER);
				}
			}
		});
	}

	private void addActionListenerToLoadFromDatabaseMenuItem() {
		loadFromDatabaseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO load from DB
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
				statusLabel.setText(fileName + StatusPanelMessage.PARSE_DONE);
			}
		});
	}
}

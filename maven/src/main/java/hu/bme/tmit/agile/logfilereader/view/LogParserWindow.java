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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

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
import hu.bme.tmit.agile.logfilereader.dao.ParserDAO;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import util.PlantUmlConverter;

public class LogParserWindow {

	private static final String ENCODING = "UTF-8";
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
				File selectedFile;
				if ((selectedFile = getSelectedFile()) != null) {
					fileName = selectedFile.getName();

					try {
						Parser parser = new Parser();
						parser.parse(selectedFile.getAbsolutePath());

						String plantUmlString = PlantUmlConverter.convert(parser.getEventSet());
						SVGDocument document = getSvgDocument(plantUmlString);
						svgCanvas.setSVGDocument(document);

						// kell??
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

	private File getSelectedFile() {
		final JFileChooser jFileChooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty(USER_DIRECTORY_PROPERTY));
		jFileChooser.setCurrentDirectory(workingDirectory);
		if (jFileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return jFileChooser.getSelectedFile();
		}
		return null;
	}

	private SVGDocument getSvgDocument(String plantUmlString) throws IOException, UnsupportedEncodingException {
		SourceStringReader reader = new SourceStringReader(plantUmlString);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		// ez kell vegul???
		String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
		os.close();

		// The XML is stored into svg
		final String svg = new String(os.toByteArray(), Charset.forName(ENCODING));

		String svgToCanvas = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(svgToCanvas);
		SVGDocument document = factory.createSVGDocument("", new ByteArrayInputStream(svg.getBytes(ENCODING)));
		return document;
	}

	private void addActionListenerToLoadFromDatabaseMenuItem() {
		loadFromDatabaseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParserDAO pdao = new ParserDAO(); 
				Object[] possibilities = pdao.getSavedFileNames();
				
				if (possibilities.length > 0) {
					String s = (String)JOptionPane.showInputDialog(
					                    frame,
					                    "File name:",
					                    "Load from DB",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    possibilities,
					                    possibilities[0]);
	
					//If a string was returned, say so.
					if ((s != null) && (s.length() > 0)) {
						try {
							String plantUmlString = PlantUmlConverter.convert(pdao.loadTtcnEvent(s));
							SVGDocument document = getSvgDocument(plantUmlString);
							svgCanvas.setSVGDocument(document);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						
					    return;
					}
	
					//If you're here, the return value was null/empty.
				}
				else {
					JOptionPane.showMessageDialog(frame,
						    "There's no saved file(s) found!",
						    "Load from DB",
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
				statusLabel.setText(fileName + StatusPanelMessage.PARSE_DONE);
			}
		});
	}
}

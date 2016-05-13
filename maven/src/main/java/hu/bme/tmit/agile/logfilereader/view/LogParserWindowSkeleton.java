package hu.bme.tmit.agile.logfilereader.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.SVGUserAgent;
import org.apache.batik.swing.svg.SVGUserAgentAdapter;

import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import util.StatusPanelMessage;

public abstract class LogParserWindowSkeleton {

	private static final String MAIN_WINDOW_TITLE = "Parser and sequence drawer";
	protected static final String LOAD_FROM_DATABASE_LABEL = "Load from database...";
	protected static final String SAVE_TO_DATABASE_LABEL = "Save to database...";

	private static final int WINDOW_VERTICAL = 50;
	private static final int WINDOW_HORIZONTAL = 50;
	private static final int WINDOW_HEIGHT = 900;
	private static final int WINDOW_WIDTH = 1200;

	private static final int STATUS_PANEL_HEIGHT = 16;

	protected JFrame frame = new JFrame(MAIN_WINDOW_TITLE);

	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem openFileMenuItem = new JMenuItem("Open file...");
	private JMenuItem loadFromDatabaseMenuItem = new JMenuItem(LOAD_FROM_DATABASE_LABEL);
	private JMenuItem saveToDatabaseMenuItem = new JMenuItem(SAVE_TO_DATABASE_LABEL);
	private JMenuItem exitMenuItem = new JMenuItem("Exit");

	private JPanel statusPanel = new JPanel();
	protected JLabel statusLabel = new JLabel();

	private JPanel treePanel = new JPanel();
	protected DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parameters");
	protected JTree jTree = new JTree(root);
	private JScrollPane jScrollPane = new JScrollPane(jTree);
	private JLabel paramsLabel = new JLabel();

	protected String fileName;

	protected TtcnEvent[] eventArray = null;

	protected File tempSequenceSvg;

	private SVGUserAgent ua = new SVGUserAgentAdapter() {
		@Override
		public void showAlert(String id) {
			Message m = (Message) eventArray[Integer.parseInt(id)];
			generateTree(m.getParam());
		}
	};

	protected abstract void generateTree(String messageParameter);

	protected JSVGCanvas svgCanvas = new JSVGCanvas(ua, true, true);

	protected void initialize() {
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

	protected abstract void drawSequenceFromFile();

	private void addActionListenerToLoadFromDatabaseMenuItem() {
		loadFromDatabaseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawSequenceFromDatabase();
			}
		});
	}

	protected abstract void drawSequenceFromDatabase();

	private void addActionListenerToSaveToDatabaseMenuItem() {
		saveToDatabaseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveToDatabase();
			}
		});
	}

	protected abstract void saveToDatabase();

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

package com.plancrawler.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.plancrawler.controller.Controller;
import com.plancrawler.controller.Paintable;
import com.plancrawler.model.utilities.MyPoint;
import com.plancrawler.view.dialogs.EntryModifyDialog;
import com.plancrawler.view.support.EntryFormEvent;
import com.plancrawler.view.support.ItemTableModel;
import com.plancrawler.view.support.PrefsListener;
import com.plancrawler.view.toolbars.FocusToolbar;
import com.plancrawler.view.toolbars.MeasureToolbar;
import com.plancrawler.view.toolbars.NavToolbar;
import com.plancrawler.view.toolbars.RotateToolbar;
import com.plancrawler.view.toolbars.SaveLoadToolbar;
import com.plancrawler.view.toolbars.SelectionNotifierToolbar;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	// Menu
	private PCMenuBar pcMenubar = new PCMenuBar();

	// Toolbars
	private SaveLoadToolbar fileToolbar;
	private NavToolbar navToolbar;
	private RotateToolbar rotToolbar;
	private FocusToolbar focusToolbar;
	private MeasureToolbar measToolbar;
	private SelectionNotifierToolbar selNotifyToolbar;

	// Panes
	private EntryFormPanel entryFormPanel = new EntryFormPanel();
	private TablePanel tablePanel = new TablePanel("Take Off: all items");
	private CrateTablePane cratePanel = new CrateTablePane();
	private TablePanel itemsInCratePanel = new TablePanel("Items In Crate");
	private PDFViewPane pdfViewPanel = new PDFViewPane();
	private ItemSelectionPanel itemSelectPanel = new ItemSelectionPanel();
	private CrateSelectionPanel crateSelectPanel = new CrateSelectionPanel();

	// Panels
	private JPanel westPanel;
	private JPanel eastPanel;

	// Dialogs
	private PrefsDialog prefsDialog;
	private Preferences prefs;

	private Controller controller = new Controller();
	private final static String PCBPR_TITLE = "PlanCrawler Blueprint Reader";

	public MainFrame() {
		super(PCBPR_TITLE);

		List<Image> spiders = new ArrayList<Image>();
		spiders.add(createIcon("/com/plancrawler/view/iconImages/Spider8.gif").getImage());
		spiders.add(createIcon("/com/plancrawler/view/iconImages/Spider10.gif").getImage());
		spiders.add(createIcon("/com/plancrawler/view/iconImages/Spider16.gif").getImage());
		this.setIconImages(spiders);

		setupFrame();
		setPrefs();
		addComponents();
	}

	private ImageIcon createIcon(String string) {
		URL url = getClass().getResource(string);
		if (url == null)
			System.err.println("could not load resource " + string);

		ImageIcon icon = new ImageIcon(url);
		return icon;
	}

	private void setupFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenDim.width - 100, screenDim.height - 100);
		setMinimumSize(new Dimension(640, 480));
		setLocationRelativeTo(null);
		setJMenuBar(pcMenubar);
		setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.cleanup();
			}
		});
	}

	private void setPrefs() {
		// note this throws a root warning, even though root access is not asked
		// for.
		// no harm, but warning is annoying. considered a "feature" by the JVM.
		prefs = Preferences.userNodeForPackage(this.getClass());
		prefsDialog = new PrefsDialog(this);
		prefsDialog.addPrefsListener(new PrefsListener() {
			@Override
			public void preferencesSet(String pdfPath, String tempPath) {
				controller.setPaths(pdfPath, tempPath);
				prefs.put("pdfPath", pdfPath);
				prefs.put("tempPath", tempPath);
			}
		});
		String docPath = prefs.get("pdfPath", System.getProperty("user.home"));
		String tempPath = prefs.get("tempPath", System.getProperty("user.home"));
		controller.setPaths(docPath, tempPath);
		prefsDialog.setDefaults(docPath, tempPath);
	}

	private void addComponents() {
		addToolbarComponents();
		addWestComponents();
		addEastComponents();
		addCenterComponents();
	}

	private void addToolbarComponents() {
		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolPanel.setBorder(BorderFactory.createEtchedBorder());

		fileToolbar = new SaveLoadToolbar();
		fileToolbar.addSaveLoadToolbarListener((e) -> {
			if (e.isLoadPDFRequest())
				loadPDF();
			if (e.isLoadRequest())
				loadTO();
			if (e.isSaveRequest()) {
				if (pcMenubar.isSaveFileSet())
					reSaveTO(pcMenubar.getSaveFile());
				else
					saveTO();
			}
		});
		toolPanel.add(fileToolbar);

		rotToolbar = new RotateToolbar();
		rotToolbar.addRotToolbarListener((e) -> {
			controller.handlePageRotation(e);
			changePage(controller.getCurrentPage());
		});
		toolPanel.add(rotToolbar);

		navToolbar = new NavToolbar();
		navToolbar.addNavListener((page) -> changePage(page));
		toolPanel.add(navToolbar);

		focusToolbar = new FocusToolbar();
		focusToolbar.addFocusToolbarListener((e) -> {
			SwingWorker<Void, Void> focusworker = new SwingWorker<Void, Void>() {
				protected Void doInBackground() throws Exception {
					pdfViewPanel.focus();
					return null;
				}
			};
			if (e.isFitToScreenRequested())
				pdfViewPanel.fitImage();
			if (e.isFocusRequested())
				focusworker.execute();
		});
		toolPanel.add(focusToolbar);

		measToolbar = new MeasureToolbar(pdfViewPanel);
		measToolbar.addMeasurementListener((m) -> {
			controller.setMeasuring(m.isMeasurementActive());
			if (m.isMeasurementActive())
				refreshTables(); // clears selections
			if (m.isAddMeasurementRequest())
				controller.addMeasurement(m.getMeas());
			if (m.isDrawRequest())
				requestDraw(m.getMeas());
		});
		toolPanel.add(measToolbar);

		selNotifyToolbar = new SelectionNotifierToolbar();
		toolPanel.add(selNotifyToolbar);

		setToolbarFloat(false); // initially set all to locked
		this.add(toolPanel, BorderLayout.PAGE_START);
	}

	private void setToolbarFloat(boolean state) {
		fileToolbar.setFloatable(state);
		navToolbar.setFloatable(state);
		rotToolbar.setFloatable(state);
		focusToolbar.setFloatable(state);
		measToolbar.setFloatable(state);
		selNotifyToolbar.setFloatable(state);
	}

	private void addCenterComponents() {
		MouseHandler handler = new MouseHandler();
		JTabbedPane centerTabPane = new JTabbedPane();

		pdfViewPanel.addMouseListener(handler);
		pdfViewPanel.addMouseWheelListener(handler);
		pdfViewPanel.addMouseMotionListener(handler);

		tablePanel.setData(controller.getItems());

		// cratePanel.setData(controller.getCrates());
		// cratePanel.addTableListener((t)->{
		// controller.setCratePanelActive(t.getRow());
		// itemsInCratePanel.setTitle("Items in Crate:
		// "+controller.getActiveCrateName());
		// itemsInCratePanel.setData(controller.getItemsInCrate(controller.getCratePanelActive()));
		// itemsInCratePanel.refresh();
		// });
		//
		// itemsInCratePanel.setData(controller.getItemsInCrate(-1));
		//
		// JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
		// cratePanel, itemsInCratePanel);
		// splitPanel.setDividerLocation(0.5);
		// splitPanel.setOneTouchExpandable(true);

		centerTabPane.addTab("PDF View", pdfViewPanel);
		centerTabPane.addTab("Take-Off View", tablePanel);
		// centerTabPane.addTab("Crate View", splitPanel);

		this.add(centerTabPane, BorderLayout.CENTER);
	}

	private void addEastComponents() {
		eastPanel = new JPanel();
		// Dimension dim = eastPanel.getPreferredSize();
		// dim.width = 450;
		// eastPanel.setPreferredSize(dim);
		eastPanel.setLayout(new GridLayout(0, 1));

		cratePanel.setData(controller.getCrates());
		cratePanel.addTableListener((t) -> {
			controller.setCratePanelActive(t.getRow());
			itemsInCratePanel.setTitle("Items in Crate:  " + controller.getActiveCrateName());
			itemsInCratePanel.setData(controller.getItemsInCrate(controller.getCratePanelActive()));
			itemsInCratePanel.refresh();
		});

		itemsInCratePanel.setData(controller.getItemsInCrate(-1));

		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cratePanel, itemsInCratePanel);
		splitPanel.setDividerLocation(0.5);
		splitPanel.setOneTouchExpandable(true);

		eastPanel.add(splitPanel);
		this.add(eastPanel, BorderLayout.EAST);
	}

	private void addWestComponents() {
		westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(0, 1));

		entryFormPanel.addFormListener((e) -> {
			int action = JOptionPane.OK_OPTION;
			if (e.isAddItem()) {
				if (controller.hasItemByName(e.getEntryName())) {
					action = JOptionPane.showConfirmDialog(MainFrame.this,
							"Trying to Add a duplicate Item with Name: " + e.getEntryName()
									+ ".\n  Are you sure you want to add this entry?",
							"Confirm entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				if (action == JOptionPane.OK_OPTION) {
					controller.addItem(e);
				}
			}
			if (e.isAddCrate()) {
				if (controller.hasCrateByName(e.getEntryName())) {
					action = JOptionPane.showConfirmDialog(MainFrame.this,
							"Trying to Add a duplicate Crate with Name: " + e.getEntryName()
									+ ".\n  Are you sure you want to add this entry?",
							"Confirm entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				if (action == JOptionPane.OK_OPTION) {
					controller.addCrate(e);
				}
			}
			refreshTables();
		});
		westPanel.add(entryFormPanel);

		itemSelectPanel.setData(controller.getItems());
		itemSelectPanel.addItemSelectionListener((e) -> {
			controller.setActiveItemRow(e.getRow());
			setSelNotifyToolbar();
			if (e.isDeleteRequest()) {
				controller.deleteItemRow(e.getRow());
				refreshTables();
			} else if (e.isModifyRequest()) {
				EntryFormEvent ife = EntryModifyDialog.modifyItem(controller.getItem(e.getRow()), itemSelectPanel);
				controller.modifyItem(e.getRow(), ife);
				refreshTables();
			}
		});
		westPanel.add(itemSelectPanel);

		crateSelectPanel.setData(controller.getCrates());
		crateSelectPanel.addItemSelectionListener((e) -> {
			controller.setActiveCrateRow(e.getRow());
			setSelNotifyToolbar();
			if (e.isDeleteRequest()) {
				controller.deleteCrateRow(e.getRow());
				refreshTables();
			} else if (e.isModifyRequest()) {
				EntryFormEvent ife = EntryModifyDialog.modifyItem(controller.getCrate(e.getRow()), crateSelectPanel);
				controller.modifyCrate(e.getRow(), ife);
				refreshTables();
			}
		});
		westPanel.add(crateSelectPanel);

		this.add(westPanel, BorderLayout.WEST);

	}

	private void setSelNotifyToolbar() {
		selNotifyToolbar.changeTitle(controller.getActiveItemName(), controller.getActiveCrateName());
	}

	private void resetTableData() {
		controller.setActiveCrateRow(-1);
		controller.setActiveItemRow(-1);
		controller.setCratePanelActive(-1);
		setSelNotifyToolbar();

		itemSelectPanel.setData(controller.getItems());
		crateSelectPanel.setData(controller.getCrates());
		tablePanel.setData(controller.getItems());
		cratePanel.setData(controller.getCrates());
		refreshTables();
	}

	private void refreshTables() {
		controller.setActiveCrateRow(-1);
		controller.setActiveItemRow(-1);
		controller.setCratePanelActive(-1);
		setSelNotifyToolbar();

		tablePanel.refresh();
		itemSelectPanel.refresh();
		crateSelectPanel.refresh();
		cratePanel.refresh();
		itemsInCratePanel.setTitle("Items in Crate:  " + controller.getActiveCrateName());
		itemsInCratePanel.setData(controller.getItemsInCrate(controller.getCratePanelActive()));
		itemsInCratePanel.refresh();
		// if tables are updated, then Marks probably need to be as well
		updateMarks();
	}

	private void updateMarks() {
		pdfViewPanel.setDisplayMarks(controller.getPaintables(controller.getCurrentPage()));
		repaint();
	}

	private void requestDraw(Paintable paintable) {
		List<Paintable> paintables = controller.getPaintables(controller.getCurrentPage());
		paintables.add(paintable);
		pdfViewPanel.setDisplayMarks(paintables);
		repaint();
	}

	private void changePage(int page) {
		SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {
			@Override
			protected void done() {
				try {
					BufferedImage image = get();
					pdfViewPanel.setImage(image);
					// pdfViewPanel.fitImage();
					pdfViewPanel.focus();
					updateMarks();
					navToolbar.setCurrPage(controller.getCurrentPage());
					navToolbar.doneProgress();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected BufferedImage doInBackground() throws Exception {
				navToolbar.showProgress();
				BufferedImage image = controller.getPageImage(page);
				return image;
			}
		};
		worker.execute();
	}

	private void loadPDF() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setCurrentDirectory(controller.getCurrentPDFDirectory());
		fileChooser.resetChoosableFileFilters();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF files (.pdf)", "pdf"));
		fileChooser.setAcceptAllFileFilterUsed(true);

		if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
			controller.loadPDF(fileChooser.getSelectedFile());
			loadFirstPDFImage();
		}
	}
	
	private void loadFirstPDFImage(){
		SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {
			@Override
			protected void done() {
				try {
					BufferedImage image = get();
					pdfViewPanel.setImage(image);
					pdfViewPanel.fitImage();
					pdfViewPanel.focus();
					navToolbar.setCurrPage(0);
					navToolbar.setLastPage(controller.getNumPages());
					navToolbar.doneProgress();
					setTitlebar(controller.getAssociatedPDFName());
					updateMarks();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected BufferedImage doInBackground() throws Exception {
				navToolbar.showProgress();
				BufferedImage image = controller.getCurrentPageImage();
				controller.clearTables();
				return image;
			}
		};
		worker.execute();
	}

	private void setTitlebar(String pathName) {
		if (pathName == null)
			this.setTitle(PCBPR_TITLE);
		else
			this.setTitle(PCBPR_TITLE + ":   " + pathName);
	}

	private void loadTO() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setCurrentDirectory(controller.getCurrentPDFDirectory());
		fileChooser.resetChoosableFileFilters();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PlanCrawler TakeOff files (.pto)", "pto"));
		fileChooser.setAcceptAllFileFilterUsed(true);

		if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
			SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {
				@Override
				protected void done() {
					try {
						BufferedImage image = get();
						pdfViewPanel.setImage(image);
						pdfViewPanel.fitImage();
						pdfViewPanel.focus();
						navToolbar.setCurrPage(0);
						navToolbar.setLastPage(controller.getNumPages());
						navToolbar.doneProgress();
						pcMenubar.setSaveFileName(fileChooser.getSelectedFile().getAbsolutePath());
						setTitlebar(fileChooser.getSelectedFile().getAbsolutePath());
						resetTableData();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				protected BufferedImage doInBackground() throws Exception {
					BufferedImage image = null;
					try {
						navToolbar.showProgress();
						controller.loadFromFile(fileChooser.getSelectedFile());
						image = controller.getCurrentPageImage();
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(MainFrame.this, "Could not load data from file.",
								"Error loading file", JOptionPane.ERROR_MESSAGE);
					}
					return image;
				}
			};
			worker.execute();
		}
	}

	private void saveTO() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setCurrentDirectory(controller.getCurrentPDFDirectory());
		fileChooser.resetChoosableFileFilters();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PlanCrawler TakeOff files (.pto)", "pto"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						File file;
						if (!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".pto"))
							file = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".pto");
						else
							file = fileChooser.getSelectedFile();
						controller.saveToFile(file);
						pcMenubar.setSaveFileName(file.getAbsolutePath());
						setTitlebar(file.getAbsolutePath());
						refreshTables();
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(MainFrame.this, "Could not save data to file.",
								"Error saving file", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
	}

	private void reSaveTO(String saveName) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(saveName);
					controller.saveToFile(file);
					refreshTables();
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.this, "Could not save data to file.", "Error saving file",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private void exportToCSV() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setCurrentDirectory(controller.getCurrentPDFDirectory());
		fileChooser.resetChoosableFileFilters();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV", "csv"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						File file;
						if (!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".csv"))
							file = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".csv");
						else
							file = fileChooser.getSelectedFile();
						ItemTableModel tableModel = new ItemTableModel();
						tableModel.setData(controller.getItems());
						controller.saveTableAsCSV(tableModel, file);
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(MainFrame.this, "Could not save data to file.",
								"Error saving file", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
	}

	private void mergePDFFiles() {
		JFileChooser fileChooser = new JFileChooser();
		JFileChooser saveChooser = new JFileChooser();

		fileChooser.setCurrentDirectory(controller.getCurrentPDFDirectory());
		fileChooser.resetChoosableFileFilters();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setMultiSelectionEnabled(true);
		 
		if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					try {
						File[] files = fileChooser.getSelectedFiles();
						saveChooser.setCurrentDirectory(fileChooser.getCurrentDirectory());
						saveChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
						if (saveChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
							File saveFile;
							if (!saveChooser.getSelectedFile().getAbsolutePath().endsWith(".pdf"))
								saveFile = new File(saveChooser.getSelectedFile().getAbsolutePath() + ".pdf");
							else
								saveFile = saveChooser.getSelectedFile();
							controller.mergePDFs(files, saveFile);
							controller.loadPDF(saveFile);
							loadFirstPDFImage();
						}
						
					} catch (Exception e1){
						e1.printStackTrace();
						JOptionPane.showMessageDialog(MainFrame.this, "Could not merge PDF files.", 
								"Error merging files", JOptionPane.ERROR_MESSAGE);
					}
				}
				
			});
		}
	}

	private void exitProgram() {
		int action = JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure you want to exit the application?",
				"Confirm exit", JOptionPane.OK_CANCEL_OPTION);
		if (action == JOptionPane.OK_OPTION) {
			controller.cleanup();
			System.exit(0);
		}
	}

	private class PCMenuBar extends JMenuBar {
		private static final long serialVersionUID = 1L;
		private String saveFileName;
		private JMenuItem reSaveMenuItem = new JMenuItem();

		public PCMenuBar() {
			setupMenu();
			saveFileName = null;
		}

		public String getSaveFile() {
			return saveFileName;
		}

		private void setupMenu() {
			makeFileMenu();
			makeWindowMenu();
			makeEditMenu();
			makeAboutMenu();
		}

		public boolean isSaveFileSet() {
			return (saveFileName != null);
		}

		public void setSaveFileName(String filename) {
			if (filename == null) {
				reSaveMenuItem.setVisible(false);
				saveFileName = null;
			} else {
				saveFileName = filename;
				reSaveMenuItem.setText("Save to " + filename);
				reSaveMenuItem.setVisible(true);
			}
		}

		private void makeFileMenu() {
			JMenu fileMenu = new JMenu("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);

			JMenuItem loadPDFMenuItem = new JMenuItem("Load PDF");
			loadPDFMenuItem.addActionListener((e) -> loadPDF());

			JMenuItem loadTOMenuItem = new JMenuItem("Load TakeOff");
			loadTOMenuItem.addActionListener((e) -> loadTO());

			JMenuItem saveMenuItem = new JMenuItem("Save TakeOff");
			saveMenuItem.addActionListener((e) -> saveTO());

			reSaveMenuItem.addActionListener((e) -> {
				if (saveFileName == null)
					saveTO();
				else
					reSaveTO(saveFileName);
			});
			reSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

			JMenuItem exportTO = new JMenuItem("Export TakeOff to CSV");
			exportTO.addActionListener((e) -> exportToCSV());

			JMenuItem exportPDF = new JMenuItem("Export PDF");

			JMenuItem exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_X);
			exitMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
			exitMenuItem.addActionListener((e) -> exitProgram());

			fileMenu.add(loadPDFMenuItem);
			fileMenu.addSeparator();
			fileMenu.add(loadTOMenuItem);
			fileMenu.addSeparator();
			fileMenu.add(saveMenuItem);
			fileMenu.add(reSaveMenuItem);
			fileMenu.addSeparator();
			fileMenu.add(exportPDF);
			fileMenu.add(exportTO);
			fileMenu.addSeparator();
			fileMenu.add(exitMenuItem);
			this.add(fileMenu);
		}

		private void makeWindowMenu() {
			JMenu windowMenu = new JMenu("Window");
			windowMenu.setMnemonic(KeyEvent.VK_W);

			JCheckBoxMenuItem lockToolbar = new JCheckBoxMenuItem("Lock toolbar");
			lockToolbar.setSelected(true);
			lockToolbar.addActionListener((e) -> setToolbarFloat(!lockToolbar.isSelected()));
			windowMenu.add(lockToolbar);

			JMenu showToolbarMenu = new JMenu("Show Toolbar");

			JCheckBoxMenuItem fileBarWindow = new JCheckBoxMenuItem("File load/save Bar");
			fileBarWindow.setSelected(true);
			fileBarWindow.addActionListener((e) -> {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
				fileToolbar.setVisible(menuItem.isSelected());
			});
			showToolbarMenu.add(fileBarWindow);

			JCheckBoxMenuItem navBarWindow = new JCheckBoxMenuItem("Navigation Bar");
			navBarWindow.setSelected(true);
			navBarWindow.addActionListener((e) -> {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
				navToolbar.setVisible(menuItem.isSelected());
			});
			showToolbarMenu.add(navBarWindow);

			JCheckBoxMenuItem rotBarWindow = new JCheckBoxMenuItem("PageRotation Bar");
			rotBarWindow.setSelected(true);
			rotBarWindow.addActionListener((e) -> {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
				rotToolbar.setVisible(menuItem.isSelected());
			});
			showToolbarMenu.add(rotBarWindow);

			JCheckBoxMenuItem measBarWindow = new JCheckBoxMenuItem("Measurement Bar");
			measBarWindow.setSelected(true);
			measBarWindow.addActionListener((e) -> {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
				measToolbar.setVisible(menuItem.isSelected());
			});
			showToolbarMenu.add(measBarWindow);

			windowMenu.add(showToolbarMenu);

			JMenu showPaneMenu = new JMenu("Show Pane");

			JCheckBoxMenuItem addWestWindow = new JCheckBoxMenuItem("West Panel");
			addWestWindow.setSelected(true);
			addWestWindow.addActionListener((e) -> {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
				westPanel.setVisible(menuItem.isSelected());
			});
			showPaneMenu.add(addWestWindow);

			JCheckBoxMenuItem addEastWindow = new JCheckBoxMenuItem("East Panel");
			addEastWindow.setSelected(true);
			addEastWindow.addActionListener((e) -> {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
				eastPanel.setVisible(menuItem.isSelected());
			});
			showPaneMenu.add(addEastWindow);

			windowMenu.add(showPaneMenu);
			windowMenu.addSeparator();

			JMenuItem prefsMenu = new JMenuItem("Preferenes");
			prefsMenu.addActionListener((e) -> {
				prefsDialog.setVisible(true);
			});
			windowMenu.add(prefsMenu);

			this.add(windowMenu);
		}

		private void makeEditMenu() {
			JMenu editMenu = new JMenu("Edit");
			editMenu.setMnemonic(KeyEvent.VK_E);

			JMenuItem clearAllMenuItem = new JMenuItem("Clear takeOff");
			clearAllMenuItem.addActionListener((e) -> {
				controller.clearDatabase();
				setTitlebar(null);
				refreshTables();
			});
			editMenu.add(clearAllMenuItem);
			editMenu.addSeparator();

			JMenuItem mergePDFMenuItem = new JMenuItem("Merge multiple PDFs to one file");
			mergePDFMenuItem.addActionListener((e) -> mergePDFFiles());
			editMenu.add(mergePDFMenuItem);

			this.add(editMenu);
		}

		private void makeAboutMenu() {
			JMenu aboutMenu = new JMenu("About");
			aboutMenu.setMnemonic(KeyEvent.VK_A);

			JMenuItem aboutMenuItem = new JMenuItem("About");
			aboutMenu.add(aboutMenuItem);
			this.add(aboutMenu);
		}
	}

	private class MouseHandler implements MouseWheelListener, MouseInputListener, MouseMotionListener {
		private int mouseX, mouseY;
		private boolean needsFocus = false;
		private boolean button1Pressed = false;
		// private boolean button3Pressed = false;;

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double notches = e.getWheelRotation();
			pdfViewPanel.rescale((1 - notches / 10), e.getX(), e.getY());
			needsFocus = true;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == pdfViewPanel && !controller.isMeasuring()) {
				MyPoint point = pdfViewPanel.getImageRelativePoint(new MyPoint(e.getX(), e.getY()));
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (controller.hasActive() && !controller.isMeasuring()) {
						controller.dropToken(point);
						updateMarks();
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (controller.hasActive()) {
						controller.removeToken(point);
						updateMarks();
						// } else {
						// controller.removeMeasurement(point);
						// updateMarks();
						// }
					}
					controller.removeMeasurement(point);
					updateMarks();
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1)
				button1Pressed = true;
			// if (e.getButton() == MouseEvent.BUTTON3)
			// button3Pressed = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1)
				button1Pressed = false;
			// if (e.getButton() == MouseEvent.BUTTON3)
			// button3Pressed = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int dX = e.getX() - mouseX;
			int dY = e.getY() - mouseY;

			mouseX = e.getX();
			mouseY = e.getY();

			if (button1Pressed && !controller.isMeasuring()) {
				pdfViewPanel.move(dX, dY);
				pdfViewPanel.repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			// measRibbon.setCurrent(centerScreen.getImageRelativePoint(new
			// MyPoint(mouseX, mouseY)));
			if (needsFocus) {
				pdfViewPanel.quickFocus();
				needsFocus = false;
			}
		}
	}

}

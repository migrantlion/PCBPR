package com.plancrawler.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.plancrawler.controller.Controller;
import com.plancrawler.controller.MeasurePainter;
import com.plancrawler.controller.Paintable;
import com.plancrawler.model.utilities.MyPoint;
import com.plancrawler.view.toolbars.FocusToolbar;
import com.plancrawler.view.toolbars.MeasureToolbar;
import com.plancrawler.view.toolbars.NavToolbar;
import com.plancrawler.view.toolbars.RotateToolbar;
import com.plancrawler.view.toolbars.SaveLoadToolbar;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	// Toolbars
	private SaveLoadToolbar fileToolbar;
	private NavToolbar navToolbar;
	private RotateToolbar rotToolbar;
	private FocusToolbar focusToolbar;
	private MeasureToolbar measToolbar;

	// Panes
	private ItemFormPanel itemFormPanel = new ItemFormPanel();
	private TablePanel tablePanel = new TablePanel();
	private PDFViewPane pdfViewPanel = new PDFViewPane();
	private ItemSelectionPanel itemSelectPanel = new ItemSelectionPanel();

	private Controller controller = new Controller();

	public MainFrame() {
		super("PlanCrawler BluePrint Reader");
		setup();
		addComponents();
	}

	private void setup() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenDim.width - 100, screenDim.height - 100);
		setMinimumSize(new Dimension(640, 480));
		setLocationRelativeTo(null);
		setJMenuBar(new PCMenuBar());
		setVisible(true);
	}

	private void addComponents() {
		addToolbarComponents();
		addWestComponents();
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
			if (e.isSaveRequest())
				saveTO();
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
			if (m.isAddMeasurementRequest())
				controller.addMeasurement(m.getMeas());
			if (m.isDrawRequest())
				requestDraw(m.getMeas());
		});
		toolPanel.add(measToolbar);

		setToolbarFloat(false); // initially set all to locked
		this.add(toolPanel, BorderLayout.PAGE_START);
	}

	private void setToolbarFloat(boolean state) {
		fileToolbar.setFloatable(state);
		navToolbar.setFloatable(state);
		rotToolbar.setFloatable(state);
		focusToolbar.setFloatable(state);
		measToolbar.setFloatable(state);
	}

	private void addCenterComponents() {
		MouseHandler handler = new MouseHandler();
		JTabbedPane centerTabPane = new JTabbedPane();

		pdfViewPanel.addMouseListener(handler);
		pdfViewPanel.addMouseWheelListener(handler);
		pdfViewPanel.addMouseMotionListener(handler);

		tablePanel.setData(controller.getItems());

		centerTabPane.addTab("PDF View", pdfViewPanel);
		centerTabPane.addTab("Item View", tablePanel);

		this.add(centerTabPane, BorderLayout.CENTER);
	}

	private void addWestComponents() {
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(0, 1));

		itemFormPanel.addFormListener((e) -> {
			int action = JOptionPane.OK_OPTION;
			if (controller.hasItemByName(e.getItemName())) {
				action = JOptionPane.showConfirmDialog(MainFrame.this,
						"Trying to Add a duplicate Item with Name: " + e.getItemName()
								+ ".\n  Are you sure you want to add this entry?",
						"Confirm entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			if (action == JOptionPane.OK_OPTION) {
				controller.addItem(e);
				refreshTables();
			}
		});
		westPanel.add(itemFormPanel);

		itemSelectPanel.setData(controller.getItems());
		itemSelectPanel.addItemSelectionListener((e) -> {
			controller.setActiveItemRow(e.getRow());
			if (e.isDeleteRequest()) {
				controller.deleteItemRow(e.getRow());
				refreshTables();
			} else if (e.isModifyRequest()) {
				//TODO: needs code here
				System.out.println("Modify was requested of row " + e.getRow());
			}
		});
		westPanel.add(itemSelectPanel);

		this.add(westPanel, BorderLayout.WEST);

	}

	private void refreshTables() {
		tablePanel.refresh();
		itemSelectPanel.refresh();
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
						refreshTables();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				protected BufferedImage doInBackground() throws Exception {
					BufferedImage image = null;
					try {
						controller.loadFromFile(fileChooser.getSelectedFile());
						navToolbar.showProgress();
						image = controller.getCurrentPageImage();
					} catch (Exception e1) {
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
						controller.saveToFile(fileChooser.getSelectedFile());
						refreshTables();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(MainFrame.this, "Could save data to file.", "Error saving file",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
	}

	private class PCMenuBar extends JMenuBar {
		private static final long serialVersionUID = 1L;
		JFileChooser fileChooser = new JFileChooser();

		public PCMenuBar() {
			setupMenu();
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF files (.pdf)", "pdf"));
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PlanCrawler TakeOff files (.pto)", "pto"));
			fileChooser.setAcceptAllFileFilterUsed(true);
		}

		private void setupMenu() {
			makeFileMenu();
			makeWindowMenu();
			makeEditMenu();
			makeAboutMenu();
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

			JMenuItem exportImages = new JMenuItem("Export Images");

			JMenuItem exportPDF = new JMenuItem("Export PDF");

			JMenuItem exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_X);
			exitMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
			exitMenuItem.addActionListener((e) -> {
				int action = JOptionPane.showConfirmDialog(MainFrame.this,
						"Are you sure you want to exit the application?", "Confirm exit", JOptionPane.OK_CANCEL_OPTION);
				if (action == JOptionPane.OK_OPTION)
					System.exit(0);
			});

			fileMenu.add(loadPDFMenuItem);
			fileMenu.add(loadTOMenuItem);
			fileMenu.addSeparator();
			fileMenu.add(saveMenuItem);
			fileMenu.add(exportPDF);
			fileMenu.add(exportImages);
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

			JCheckBoxMenuItem addItemWindow = new JCheckBoxMenuItem("Add Item Form");
			addItemWindow.setSelected(true);
			addItemWindow.addActionListener((e) -> {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
				itemFormPanel.setVisible(menuItem.isSelected());
			});
			showPaneMenu.add(addItemWindow);

			windowMenu.add(showPaneMenu);

			this.add(windowMenu);
		}

		private void makeEditMenu() {
			JMenu editMenu = new JMenu("Edit");
			editMenu.setMnemonic(KeyEvent.VK_E);

			JMenuItem clearAllMenuItem = new JMenuItem("Clear takeOff");
			editMenu.add(clearAllMenuItem);
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
		private boolean button3Pressed = false;;

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
					if (controller.hasActiveItem()) {
						controller.dropToken(point);
						updateMarks();
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (controller.hasActiveItem()) {
						controller.removeToken(point);
						updateMarks();
					} else {
						controller.removeMeasurement(point);
						updateMarks();
					}
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
			if (e.getButton() == MouseEvent.BUTTON3)
				button3Pressed = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1)
				button1Pressed = false;
			if (e.getButton() == MouseEvent.BUTTON3)
				button3Pressed = false;
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

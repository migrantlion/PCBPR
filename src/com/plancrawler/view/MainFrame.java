package com.plancrawler.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
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
import javax.swing.SwingWorker;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.plancrawler.controller.Controller;
import com.plancrawler.model.utilities.MyPoint;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private NavToolbar navToolbar;
	private RotateToolbar rotToolbar;
	private FocusToolbar focusToolbar;
	private ItemFormPanel itemFormPanel;
	private TablePanel tablePanel;
	private PDFViewPane pdfViewPanel;
	private ItemSelectionPanel itemSelectPanel;

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
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		northPanel.setBorder(BorderFactory.createEtchedBorder());

		navToolbar = new NavToolbar();
		navToolbar.addNavListener((page) -> changePage(page));
		northPanel.add(navToolbar);

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
		northPanel.add(focusToolbar);

		rotToolbar = new RotateToolbar();
		rotToolbar.addRotToolbarListener((e) -> {
			controller.handlePageRotation(e);
			changePage(controller.getCurrentPage());
		});

		northPanel.add(rotToolbar);
		setToolbarFloat(false); // initially set all to locked
		this.add(northPanel, BorderLayout.PAGE_START);
	}

	private void addCenterComponents() {
		MouseHandler handler = new MouseHandler();
		JTabbedPane centerTabPane = new JTabbedPane();

		pdfViewPanel = new PDFViewPane();
		pdfViewPanel.addMouseListener(handler);
		pdfViewPanel.addMouseWheelListener(handler);
		pdfViewPanel.addMouseMotionListener(handler);

		tablePanel = new TablePanel();
		tablePanel.setData(controller.getItems());

		centerTabPane.addTab("PDF View", pdfViewPanel);
		centerTabPane.addTab("Item View", tablePanel);

		this.add(centerTabPane, BorderLayout.CENTER);
	}

	private void addWestComponents() {
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(0, 1));

		itemFormPanel = new ItemFormPanel();
		itemFormPanel.addFormListener((e) -> {
			int action = JOptionPane.OK_OPTION;
			if (controller.hasItemByName(e.getItemName())) {
				action = JOptionPane.showConfirmDialog(MainFrame.this,
						"Duplicate Item with Name: " + e.getItemName() + ".  Are you sure you want to add this entry?",
						"Confirm entry", JOptionPane.OK_CANCEL_OPTION);
			}
			if (action == JOptionPane.OK_OPTION) {
				controller.addItem(e);
				refreshTables();
			}
		});
		westPanel.add(itemFormPanel);

		itemSelectPanel = new ItemSelectionPanel();
		itemSelectPanel.setData(controller.getItems());
		itemSelectPanel.addItemSelectionListener((e) -> {
			controller.setActiveItemRow(e.getRow());
			if (e.isDeleteRequest()) {
				controller.deleteItemRow(e.getRow());
				refreshTables();
			} else if (e.isModifyRequest()) {
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

	private void setToolbarFloat(boolean state) {
		navToolbar.setFloatable(state);
		rotToolbar.setFloatable(state);
		focusToolbar.setFloatable(state);
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
			loadPDFMenuItem.addActionListener((e) -> {
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
							;
							return image;
						}
					};
					worker.execute();
				}
			});

			JMenuItem loadTOMenuItem = new JMenuItem("Load TakeOff");
			loadTOMenuItem.addActionListener((e) -> {
				fileChooser.resetChoosableFileFilters();
				fileChooser
						.addChoosableFileFilter(new FileNameExtensionFilter("PlanCrawler TakeOff files (.pto)", "pto"));
				fileChooser.setAcceptAllFileFilterUsed(true);
				if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						controller.loadFromFile(fileChooser.getSelectedFile());
						tablePanel.refresh();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(MainFrame.this, "Could not load data from file.",
								"Error loading file", JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			JMenuItem saveMenuItem = new JMenuItem("Save TakeOff");
			saveMenuItem.addActionListener((e) -> {
				fileChooser.resetChoosableFileFilters();
				fileChooser
						.addChoosableFileFilter(new FileNameExtensionFilter("PlanCrawler TakeOff files (.pto)", "pto"));
				fileChooser.setAcceptAllFileFilterUsed(true);
				if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						controller.saveToFile(fileChooser.getSelectedFile());
						tablePanel.refresh();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(MainFrame.this, "Could save data to file.", "Error saving file",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});

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
		private boolean isAlreadyOneClick = false;
		private boolean button1Pressed = false;
		private boolean button3Pressed = false;;
		MyPoint pt1;

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double notches = e.getWheelRotation();
			pdfViewPanel.rescale((1 - notches / 10), e.getX(), e.getY());
			needsFocus = true;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == pdfViewPanel) {
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
					}
				}
			}
		}
		// if (!measRibbon.isMeasuring()) {
		// pt1 = null;
		// if (e.getSource().equals(pdfViewPanel) && hasActiveItem()) {
		// if (isAlreadyOneClick) {
		// System.out.println("double click");
		// if (e.getButton() == 3)
		// changeItemInfo();
		// isAlreadyOneClick = false;
		// } else {
		// isAlreadyOneClick = true;
		// Timer t = new Timer("doubleclickTimer", false);
		// t.schedule(new TimerTask() {
		// @Override
		// public void run() {
		// // if oneClick is on, then it must have been a
		// // single click
		// if (isAlreadyOneClick) {
		// if (e.getButton() == 1)
		// addToTakeOff(new MyPoint(e.getX(), e.getY()));
		// else if (e.getButton() == 3)
		// removeFromTakeOff(new MyPoint(e.getX(), e.getY()));
		// }
		// isAlreadyOneClick = false;
		// }
		// }, 500);
		// }
		// } else if (e.getSource().equals(centerScreen) && e.getButton() == 3)
		// removeFromTakeOff(new MyPoint(e.getX(), e.getY()));
		// } else { // measuring
		// if (pt1 == null) {
		// MyPoint screenPt = new MyPoint(e.getX(), e.getY());
		// pt1 = centerScreen.getImageRelativePoint(screenPt);
		// measRibbon.setFirst(pt1);
		// } else {
		// MyPoint screenPt = new MyPoint(e.getX(), e.getY());
		// MyPoint pt2 = centerScreen.getImageRelativePoint(screenPt);
		// measRibbon.doMeasurement(pt1, pt2);
		// pt1 = null;
		// }
		// }
		// }

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

			if (button1Pressed) {
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

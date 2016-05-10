package com.plancrawler.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import com.plancrawler.controller.Controller;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private NavToolbar navToolbar;
	private RotateToolbarPanel rotToolbar;
	private ItemFormPanel itemFormPanel;
	private TablePanel tablePanel;
	private PDFViewPane pdfViewPanel;

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

	private void changePage(int page) {
		SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {
			@Override
			protected void done() {
				try {
					BufferedImage image = get();
					pdfViewPanel.setImage(image);
					// pdfViewPanel.fitImage();
					pdfViewPanel.focus();
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

	private void addComponents() {
		addToolbarComponents();
		addWestComponents();
		addCenterComponents();
	}

	private void addCenterComponents() {
		JTabbedPane centerTabPane = new JTabbedPane();

		pdfViewPanel = new PDFViewPane();

		tablePanel = new TablePanel();
		tablePanel.setData(controller.getItems());

		centerTabPane.addTab("PDF View", pdfViewPanel);
		centerTabPane.addTab("Item View", tablePanel);

		this.add(centerTabPane, BorderLayout.CENTER);
	}

	private void addToolbarComponents() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		northPanel.setBorder(BorderFactory.createEtchedBorder());

		navToolbar = new NavToolbar();
		navToolbar.addNavListener((page) -> changePage(page));
		northPanel.add(navToolbar);

		rotToolbar = new RotateToolbarPanel();
		rotToolbar.addRotToolbarListener((e) -> controller.handlePageRotation(e));

		northPanel.add(rotToolbar);

		this.add(northPanel, BorderLayout.PAGE_START);
	}

	private void addWestComponents() {
		itemFormPanel = new ItemFormPanel();
		itemFormPanel.addFormListener((e) -> {
			controller.addItem(e);
			tablePanel.refresh();
			System.out.println(e.getItemName() + ": " + e.getItemDesc() + ", " + e.getItemCat() + ".  color: "
					+ e.getItemColor().toString());
		});
		this.add(itemFormPanel, BorderLayout.WEST);
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
			lockToolbar.setSelected(false);
			lockToolbar.addActionListener((e) -> {
				if (navToolbar.isFloatable()) {
					navToolbar.setFloatable(false);
					rotToolbar.setFloatable(false);
				} else {
					navToolbar.setFloatable(true);
					rotToolbar.setFloatable(true);
				}
			});
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
}

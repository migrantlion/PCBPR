package com.plancrawler.view.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class SaveLoadToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;
	private ArrayList<SaveLoadToolbarListener> listeners = new ArrayList<SaveLoadToolbarListener>();
	private JButton loadPDFButt, loadMultiPDFButt, loadTOButt, saveTOButt;

	public SaveLoadToolbar() {
		addButtons();
	}

	private void addButtons() {
		SaveLoadButtListener sllistener = new SaveLoadButtListener();

		loadPDFButt = new JButton();
		loadPDFButt.setToolTipText("open PDF");
		loadPDFButt.setIcon(createIcon("/com/plancrawler/view/iconImages/OpenPDF16.gif"));
		loadPDFButt.addActionListener(sllistener);

		loadMultiPDFButt = new JButton();
		loadMultiPDFButt.setToolTipText("open Multiple PDFs");
		loadMultiPDFButt.setIcon(createIcon("/com/plancrawler/view/iconImages/OpenMultiPDF16.gif"));
		loadMultiPDFButt.addActionListener(sllistener);

		loadTOButt = new JButton();
		loadTOButt.setToolTipText("open TakeOFf");
		loadTOButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Open16.gif"));
		loadTOButt.addActionListener(sllistener);

		saveTOButt = new JButton();
		saveTOButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Save16.gif"));
		saveTOButt.setToolTipText("save TakeOff");
		saveTOButt.addActionListener(sllistener);

		this.add(loadPDFButt);
		this.add(loadMultiPDFButt);
		this.add(loadTOButt);
		this.add(saveTOButt);
	}

	private Icon createIcon(String string) {
		URL url = getClass().getResource(string);
		if (url == null)
			System.err.println("could not load resource " + string);

		ImageIcon icon = new ImageIcon(url);
		return icon;
	}

	private void alertListeners(SaveLoadToolbarEvent e) {
		for (SaveLoadToolbarListener r : listeners)
			r.saveLoadToolProcessed(e);
	}

	public void addSaveLoadToolbarListener(SaveLoadToolbarListener listener) {
		listeners.add(listener);
	}

	public boolean remRotToolbarListener(SaveLoadToolbarListener listener) {
		return listeners.remove(listener);
	}

	private class SaveLoadButtListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton butt = (JButton) e.getSource();
			SaveLoadToolbarEvent sle = new SaveLoadToolbarEvent(SaveLoadToolbar.this);
			if (butt == loadTOButt) {
				sle.setLoadRequest(true);
			}
			if (butt == loadPDFButt) {
				sle.setLoadPDFRequest(true);
			}
			if (butt == loadMultiPDFButt) {
				sle.setLoadMultiPDFRequest(true);
			}
			if (butt == saveTOButt) {
				sle.setSaveRequest(true);
			}
			alertListeners(sle);
		}
	}
}

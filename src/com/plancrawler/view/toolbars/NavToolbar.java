package com.plancrawler.view.toolbars;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class NavToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;
	private ArrayList<NavListener> listeners = new ArrayList<NavListener>();
	private JButton priorButt, nextButt, lastButt, firstButt;
	private JTextField jumpField;
	private JLabel pageLabel;
	private int currPage = 0;
	private int lastPage = 0;
	private JProgressBar progressBar;

	private int MINPAGE = 1;

	public NavToolbar() {
		intialize();
	}

	private void intialize() {
		NavButtListener listener = new NavButtListener();

		firstButt = new JButton();
		firstButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Rewind16.gif"));
		firstButt.setToolTipText("Goto first page");
		
		priorButt = new JButton();
		priorButt.setIcon(createIcon("/com/plancrawler/view/iconImages/StepBack16.gif"));
		priorButt.setToolTipText("prev page");
		
		nextButt = new JButton();
		nextButt.setIcon(createIcon("/com/plancrawler/view/iconImages/StepForward16.gif"));
		nextButt.setToolTipText("next page");

		lastButt = new JButton();
		lastButt.setIcon(createIcon("/com/plancrawler/view/iconImages/FastForward16.gif"));
		lastButt.setToolTipText("Goto last page");
		

		firstButt.addActionListener(listener);
		nextButt.addActionListener(listener);
		priorButt.addActionListener(listener);
		lastButt.addActionListener(listener);

		pageLabel = new JLabel("page " + display(currPage) + " of " + display(lastPage)+" ");

		jumpField = new JTextField(display(currPage), 5);
		jumpField.addActionListener(new NavTextListener());
		jumpField.setToolTipText("jump to page");

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		Dimension dim = jumpField.getPreferredSize();
		progressBar.setPreferredSize(dim);
		progressBar.setVisible(false);

		this.add(pageLabel);
		this.add(firstButt);
		this.add(priorButt);
		this.add(jumpField);
		this.add(progressBar);
		this.add(nextButt);
		this.add(lastButt);
	}

	private Icon createIcon(String string) {
		URL url = getClass().getResource(string);
		if (url == null)
			System.err.println("could not load resource "+string);
		
		ImageIcon icon = new ImageIcon(url);
		return icon;
	}

	public void showProgress() {
		jumpField.setVisible(false);
		progressBar.setVisible(true);
	}

	public void doneProgress() {
		progressBar.setVisible(false);
		jumpField.setVisible(true);
	}

	private void updateLabel() {
		pageLabel.setText("page " + display(currPage) + " of " + lastPage);
		jumpField.setText(display(currPage));
	}

	private String display(int page) {
		return Integer.toString(page + MINPAGE);
	}

	public void setCurrPage(int page) {
		this.currPage = page;
		if (lastPage < currPage)
			lastPage = currPage;
		updateLabel();
	}

	public void setLastPage(int page) {
		this.lastPage = page;
		updateLabel();
	}

	public void setMINPAGE(int firstPageNum) {
		MINPAGE = firstPageNum;
	}

	private void requestPage(int request) {
		notifyListeners(request);
	}

	public void addNavListener(NavListener listener) {
		listeners.add(listener);
	}

	public boolean remNavListener(NavListener listner) {
		return listeners.remove(listner);
	}

	private void notifyListeners(int page) {
		for (NavListener n : listeners)
			n.pageRequested(page);
	}

	private class NavButtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton butt = (JButton) e.getSource();
			if (butt == firstButt)
				requestPage(0);
			if (butt == priorButt)
				requestPage(currPage - 1);
			if (butt == nextButt)
				requestPage(currPage + 1);
			if (butt == lastButt)
				requestPage(lastPage);
		}
	}

	private class NavTextListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == jumpField) {
				try {
					int newpage = Integer.parseInt(jumpField.getText()) - MINPAGE;
					requestPage(newpage);
				} catch (NumberFormatException ne) {
					jumpField.setText(display(currPage));
				}
			}
		}
	}
}

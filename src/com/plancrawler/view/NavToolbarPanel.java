package com.plancrawler.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.plancrawler.view.support.NavListener;


public class NavToolbarPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ArrayList<NavListener> listeners = new ArrayList<NavListener>();
	private JButton priorButt, nextButt, lastButt, firstButt;
	private JTextField jumpField;
	private JLabel pageLabel;
	private int currPage = 0;
	private int lastPage = 0;
	
	private int MINPAGE = 1;
	
	public NavToolbarPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createEtchedBorder());
		intialize();
	}
	
	private void intialize() {
		NavButtListener listener = new NavButtListener();
		
		firstButt = new JButton("|<<]");
		priorButt = new JButton("[<]");
		nextButt = new JButton("[>|");
		lastButt = new JButton("[>>|");
		
		firstButt.addActionListener(listener);
		nextButt.addActionListener(listener);
		priorButt.addActionListener(listener);
		lastButt.addActionListener(listener);
		
		pageLabel = new JLabel("page "+display(currPage)+" of "+display(lastPage));
		
		jumpField = new JTextField(display(currPage),10);
		jumpField.addActionListener(new NavTextListener());
		
		this.add(pageLabel);
		this.add(firstButt);
		this.add(priorButt);
		this.add(jumpField);
		this.add(nextButt);
		this.add(lastButt);
	}
	
	private void updateLabel() {
		pageLabel.setText("page "+display(currPage)+" of "+display(lastPage));
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
		currPage = request;
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
				requestPage(currPage-1);
			if (butt == nextButt)
				requestPage(currPage+1);
			if (butt == lastButt)
				requestPage(lastPage);
		}
	}
	
	private class NavTextListener implements ActionListener{
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


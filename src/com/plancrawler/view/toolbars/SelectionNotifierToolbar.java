package com.plancrawler.view.toolbars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.Border;

public class SelectionNotifierToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;
	
//	private JLabel notifier = new JLabel();
	private JPanel panel = new JPanel();
	private JLabel start = new JLabel("Nothing selected to place ");
	private JLabel itemName = new JLabel();
	private JLabel in = new JLabel(" in ");
	private JLabel crateName = new JLabel();
	
	Border oldBorder;
	String emptyText = "                                          ";
	
	public SelectionNotifierToolbar(){
		setup();
		this.setFloatable(true);
		Dimension dim = this.getSize();
		dim.width *= 10;
		setMaximumSize(dim);
	}
	
	private void setup(){
//		JLabel sel = new JLabel("sel: ");
//		add(sel);
//		
//		notifier.setText(emptyText);	
//		notifier.setBorder(BorderFactory.createEtchedBorder());
//		oldBorder = notifier.getBorder();
//		
//		add(notifier);
		panel.setLayout(new FlowLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		panel.add(start);
		itemName.setText("");
		itemName.setBorder(BorderFactory.createLineBorder(Color.green, 2));
		itemName.setVisible(false);
		panel.add(itemName);
		in.setVisible(false);
		panel.add(in);
		crateName.setText("");
		crateName.setBorder(BorderFactory.createLineBorder(Color.magenta, 2));
		crateName.setVisible(false);
		panel.add(crateName);
		
		add(panel);
	}
	
//	public void changeTitle(String title){
//		if (title == null){
//			notifier.setText(emptyText);
//			notifier.setBorder(oldBorder);
//		} else {
//			notifier.setText(title);
//			notifier.setBorder(BorderFactory.createLineBorder(Color.green, 2));
//		}
//		repaint();
//	}
	
	public void changeTitle(String iName, String cName){
		if (iName == null && cName == null)
			start.setText("Nothing selected to place ");
		else
			start.setText("Placing ");
		
		if (iName == null) {
			itemName.setVisible(false);
			in.setVisible(false);
		} else {
			itemName.setText(iName);
			itemName.setVisible(true);
			in.setVisible(true);
		}
		
		if (cName == null) {
			in.setVisible(false);
			crateName.setVisible(false);
		} else {
			crateName.setText(cName);
			crateName.setVisible(true);
		}
		validate();
		repaint();
	}
}

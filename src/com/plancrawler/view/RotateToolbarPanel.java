package com.plancrawler.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.plancrawler.view.support.RotToolbarEvent;
import com.plancrawler.view.support.RotToolbarListener;



public class RotateToolbarPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ArrayList<RotToolbarListener> listeners = new ArrayList<RotToolbarListener>();
	private JButton ccwButt, cwButt, rotAllButt;
	
	public RotateToolbarPanel() {
		setupPanel();
		addButtons();
	}
	
	private void setupPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createEtchedBorder());
	}
	
	private void addButtons() {
		RotButtListener rblistener = new RotButtListener();
		
		ccwButt = new JButton("-90");
//		ccwButt.setIcon(createIcon("/toolbarButtonGraphics/general/Undo16.gif", "rotate counter-clockwise"));
		ccwButt.addActionListener(rblistener);
		
		cwButt = new JButton("+90");
//		cwButt.setIcon(new ImageIcon("/toolbarButtonGraphics/general/Redo16.gif"));
		cwButt.addActionListener(rblistener);
		
		rotAllButt = new JButton("Apply Rot to All");
		rotAllButt.addActionListener(rblistener);
		
		this.add(ccwButt);
		this.add(cwButt);
		this.add(rotAllButt);
	}
	
	private ImageIcon createIcon(String path, String reader) {
		URL url = getClass().getResource(path);
		if (url == null)
			System.out.println("could not load resource: "+path);
		ImageIcon icon = new ImageIcon(path, reader);
		return icon;
	}
	
	private void alertListeners(RotToolbarEvent e) {
		for (RotToolbarListener r : listeners)
			r.rotToolProcessed(e);
	}
	
	public void addRotToolbarListener(RotToolbarListener listener) {
		listeners.add(listener);
	}

	public boolean remRotToolbarListener(RotToolbarListener listener) {
		return listeners.remove(listener);
	}

	private class RotButtListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton butt = (JButton) e.getSource();
			RotToolbarEvent rte = new RotToolbarEvent(RotateToolbarPanel.this);
			if (butt == cwButt) {
				rte.setRotAll(false);
				rte.setRotation(Math.PI/2);
			}
			if (butt == ccwButt) {
				rte.setRotAll(false);
				rte.setRotation(-Math.PI/2);
			}
			if (butt == rotAllButt) {
				rte.setRotAll(true);
				rte.setRotation(0d);
			}
			alertListeners(rte);
		}	
	}
}


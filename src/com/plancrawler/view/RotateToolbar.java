package com.plancrawler.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.plancrawler.view.support.RotToolbarEvent;
import com.plancrawler.view.support.RotToolbarListener;

public class RotateToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;
	private ArrayList<RotToolbarListener> listeners = new ArrayList<RotToolbarListener>();
	private JButton ccwButt, cwButt, rotAllButt;
	
	public RotateToolbar() {
		addButtons();
	}
	
	private void addButtons() {
		RotButtListener rblistener = new RotButtListener();
		
		ccwButt = new JButton();
		ccwButt.setToolTipText("rotate counter-clockwise");
		ccwButt.setIcon(createIcon("/com/plancrawler/view/iconImages/RotCCW16.gif"));
		ccwButt.addActionListener(rblistener);
		
		cwButt = new JButton();
		cwButt.setToolTipText("rotate clockwise");
		cwButt.setIcon(createIcon("/com/plancrawler/view/iconImages/RotCW16.gif"));
		cwButt.addActionListener(rblistener);
		
		rotAllButt = new JButton();
		rotAllButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Add16.gif"));
		rotAllButt.setToolTipText("Apply rotation to all pages");
		rotAllButt.addActionListener(rblistener);
		
		this.add(ccwButt);
		this.add(cwButt);
		this.add(rotAllButt);
	}
	
	private Icon createIcon(String string) {
		URL url = getClass().getResource(string);
		if (url == null)
			System.err.println("could not load resource "+string);
		
		ImageIcon icon = new ImageIcon(url);
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
			RotToolbarEvent rte = new RotToolbarEvent(RotateToolbar.this);
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


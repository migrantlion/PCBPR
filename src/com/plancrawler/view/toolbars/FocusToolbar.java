package com.plancrawler.view.toolbars;

import java.net.URL;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class FocusToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;
	private ArrayList<FocusToolbarListener> listeners = new ArrayList<FocusToolbarListener>();
	private JButton focusButt, fitButt;

	public FocusToolbar() {
		setup();
	}

	private void setup() {
		focusButt = new JButton();
		focusButt.setToolTipText("focus image");
		focusButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Focus16.gif"));
		focusButt.addActionListener((e) -> alertListeners(new FocusToolbarEvent(focusButt, true, false)));
		
		this.add(focusButt);
		
		fitButt = new JButton();
		fitButt.setToolTipText("fit image to screen");
		fitButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Fit16.gif"));
		fitButt.addActionListener((e) -> alertListeners(new FocusToolbarEvent(fitButt, true, true)));
		
		this.add(fitButt);
	}

	private Icon createIcon(String string) {
		URL url = getClass().getResource(string);
		if (url == null)
			System.err.println("could not load resource " + string);

		ImageIcon icon = new ImageIcon(url);
		return icon;
	}

	private void alertListeners(FocusToolbarEvent e) {
		for (FocusToolbarListener f : listeners)
			f.focusEventRequest(e);
	}

	public void addFocusToolbarListener(FocusToolbarListener listener) {
		listeners.add(listener);
	}

	public boolean remFocusToolbarListener(FocusToolbarListener listener) {
		return listeners.remove(listener);
	}
}

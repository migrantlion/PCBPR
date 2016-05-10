package com.plancrawler.mainEngine;

import javax.swing.SwingUtilities;

import com.plancrawler.view.MainFrame;

public class Boot {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new MainFrame();
			}

		});
	}

}

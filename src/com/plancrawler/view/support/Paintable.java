package com.plancrawler.view.support;

import java.awt.Graphics;

import com.plancrawler.model.utilities.MyPoint;

public interface Paintable {
	public void paint(Graphics g, double scale, MyPoint origin);
}

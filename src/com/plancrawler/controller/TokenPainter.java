package com.plancrawler.controller;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.plancrawler.model.Location;
import com.plancrawler.model.Tokens;
import com.plancrawler.model.utilities.MyPoint;
import com.plancrawler.view.support.Paintable;

public class TokenPainter extends Tokens implements Paintable {

	private static final long serialVersionUID = 1L;

	// public TokenPainter(Location location, Color color) {
	// super(location, color);
	// }
	//
	public TokenPainter(Tokens token) {
		super(token.getLocation(), token.getColor());
	}

	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		Graphics2D g2 = (Graphics2D) g;

		Location loc = getLocation();
		MyPoint pt = loc.getPoint();
		pt.scale(scale);
		pt.translate(origin);
		// move so the center of the un-scaled oval is at the point of interest
		int diameter = (int) Math.max(50 * scale, 20.);
		pt.translate(new MyPoint(-diameter / 2, -diameter / 2));

		g2.setColor(getColor());
		g2.fillOval((int) pt.getX(), (int) pt.getY(), diameter, diameter);

		// add border color
		g2.setColor(getBorderColor());
		g2.drawOval((int) pt.getX(), (int) pt.getY(), diameter, diameter);
	}

}

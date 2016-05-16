package com.plancrawler.controller;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.plancrawler.model.Location;
import com.plancrawler.model.Token;
import com.plancrawler.model.utilities.MyPoint;

public class TokenPainter implements Paintable {
	private Token token;

	// public TokenPainter(Location location, Color color) {
	// super(location, color);
	// }
	//
	public TokenPainter(Token token) {
		this.token = token;
	}

	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		if (token.isVisible()) {
			Graphics2D g2 = (Graphics2D) g;

			int borderWidth = (int) (10 * scale);

			Location loc = token.getLocation();
			MyPoint pt = loc.getPoint();
			pt.scale(scale);
			pt.translate(origin);
			// move so the center of the un-scaled oval is at the point of
			// interest
			int diameter = (int) Math.max(50 * scale, 20.);
			pt.translate(new MyPoint(-diameter / 2, -diameter / 2));

			g2.setColor(token.getColor());
			if (token.isInCrate())
				g2.fillRect((int) pt.getX(), (int) pt.getY(), diameter, diameter);
			else
				g2.fillOval((int) pt.getX(), (int) pt.getY(), diameter, diameter);

			// add border color
			g2.setColor(token.getBorderColor());
			if (token.isInCrate()) { 
				for (int n = 0; n < borderWidth; n++)
					g2.drawRect((int) (pt.getX() + n), (int) (pt.getY() + n), diameter - 2 * n, diameter - 2 * n);
			}
			// else
			// g2.drawOval((int) pt.getX(), (int) pt.getY(), diameter,
			// diameter);
		}
	}

}

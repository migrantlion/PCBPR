package com.plancrawler.controller;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.plancrawler.model.Location;
import com.plancrawler.model.Tokens;
import com.plancrawler.model.utilities.MyPoint;
import com.plancrawler.view.support.Paintable;


public class TokenPainter extends Tokens implements Paintable {

	private static final long serialVersionUID = 1L;

//	public TokenPainter(Location location, Color color) {
//		super(location, color);
//	}
//	
	public TokenPainter(Tokens token) {
		super(token.getLocation(), token.getColor());
	}

	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		Graphics2D g2 = (Graphics2D) g;

		Location loc = getLocation();
		MyPoint pt = loc.getLoc();
		pt.scale(scale);
		pt.translate(origin);

		g2.setColor(getColor());
		g2.fillOval((int) pt.getX(), (int) pt.getY(), (int) Math.max(50 * scale, 20.),
				(int) Math.max(50 * scale, 20.));

		// add border color
		g2.setColor(getBorderColor());
		g2.drawOval((int) pt.getX(), (int) pt.getY(), (int) Math.max(50 * scale, 20.),
				(int) Math.max(50 * scale, 20.));
	}

}


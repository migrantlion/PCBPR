package com.plancrawler.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.plancrawler.model.Measurement;
import com.plancrawler.model.utilities.MyPoint;


public class MeasurePainter extends Measurement implements Paintable {
	private static final long serialVersionUID = 1L;
	Color drawColor = Color.magenta;

	public MeasurePainter(Measurement meas){
		super(meas.getStartPt(), meas.getEndPt(), meas.getPage(), meas.getScale());
	}
	
	public MeasurePainter(MyPoint startPt, MyPoint endPt, int page, double scale){
		super(startPt, endPt, page, scale);
	}
	
	@Override
	public void paint(Graphics g, double scale, MyPoint origin) {
		Graphics2D g2 = (Graphics2D) g;
		int xmark = (int) Math.floor(scale * 10);

		MyPoint loc1 = new MyPoint(getStartPt());
		loc1.scale(scale);
		loc1.translate(origin);

		MyPoint loc2 = new MyPoint(getEndPt());
		loc2.scale(scale);
		loc2.translate(origin);

		g2.setColor(drawColor);
		// draw first X
		g2.drawLine((int) (loc1.getX() - xmark), (int) (loc1.getY() - xmark), (int) (loc1.getX() + xmark),
				(int) (loc1.getY() + xmark));
		g2.drawLine((int) (loc1.getX() - xmark), (int) (loc1.getY() + xmark), (int) (loc1.getX() + xmark),
				(int) (loc1.getY() - xmark));
		// draw second X
		g2.drawLine((int) (loc2.getX() - xmark), (int) (loc2.getY() - xmark), (int) (loc2.getX() + xmark),
				(int) (loc2.getY() + xmark));
		g2.drawLine((int) (loc2.getX() - xmark), (int) (loc2.getY() + xmark), (int) (loc2.getX() + xmark),
				(int) (loc2.getY() - xmark));
		
		// draw connecting line
		g2.drawLine((int)loc1.getX(), (int) loc1.getY(), (int)loc2.getX(), (int)loc2.getY());

		// scale the font size
		Font currentFont = g2.getFont();
		Font newFont = currentFont.deriveFont((float) (currentFont.getSize() * 5.6F * scale));
		g2.setFont(newFont);
		
		// find out how big the text will be
		FontMetrics metrics = g2.getFontMetrics(newFont);
		int hgt = metrics.getHeight();
		int adv = metrics.stringWidth(getMeasureString());
		Dimension textSize = new Dimension(adv+2, hgt+2);
		
		// find the center of the line and adjust back by the text width
		MyPoint centerPt = MyPoint.middle(loc1, loc2);
		centerPt.add(new MyPoint(-textSize.getWidth()/2d,0));
		
		// draw in a background box
		g2.setColor(Color.white);
		g2.fillRect((int) centerPt.getX(), (int) (centerPt.getY() - textSize.getHeight()), (int)textSize.getWidth(), (int)textSize.getHeight());
		
		g2.setColor(drawColor);
		g2.drawString(getMeasureString(), (int) centerPt.getX(), (int) centerPt.getY());

		g2.setFont(currentFont);
	}
}

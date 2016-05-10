package com.plancrawler.model.utilities;

import java.awt.Color;

public class ColorUtility {
	public static Color randColor() {
		int r = (int)(Math.random()*255);
		int g = (int)(Math.random()*255);
		int b = (int)(Math.random()*255);
		return new Color(r,g,b);
	}
	
	public static Color invert(Color color) {
		return new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
	public static Color makeTransparent(Color color, float alpha) {
		return new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, alpha);
	}

	public static String colorName(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		return "R: "+r+" G: "+g+" B: "+b;
	}
}

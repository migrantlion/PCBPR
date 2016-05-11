package com.plancrawler.model;

import java.io.Serializable;

import com.plancrawler.model.utilities.MyPoint;

public class Measurement implements Serializable {
	public static final long serialVersionUID = 1L;
	
	private int page;
	private MyPoint startPt, endPt;
	private double scale = 1.0;
	
	public Measurement(MyPoint startPt, MyPoint endPt, int page){
		this.startPt = startPt;
		this.endPt = endPt;
		this.page = page;
	}
	
	public void calibrate(int DPI, double pageInch, double realFoot) {
		scale = (realFoot / ((double) DPI * pageInch));
	}
	
	public boolean isVerticalOrientation() {
		MyPoint line = new MyPoint(endPt).add(MyPoint.neg(startPt));
		double vert = Math.abs(MyPoint.dot(new MyPoint(0, 1), line));
		double horiz = Math.abs(MyPoint.dot(new MyPoint(1, 0), line));
		
		// if dot product == 0, then line is perp to choice
		if (vert > horiz)
			return true;
		else
			return false;
	}

	public boolean isAtLocation(MyPoint testPoint, int page) {
		if (page != this.page)
			return false;
		
		boolean answer;
		if (isVerticalOrientation()) {
			MyPoint[] border = { new MyPoint(startPt.getX() - 25, startPt.getY()),
					new MyPoint(startPt.getX() + 25, startPt.getY()), new MyPoint(endPt.getX() + 25, endPt.getY()),
					new MyPoint(endPt.getX() - 25, endPt.getY()) };

			answer = MyPoint.isInsideRegion(testPoint, border);
		} else {
			MyPoint[] border = { new MyPoint(startPt.getX(), startPt.getY() - 25),
					new MyPoint(startPt.getX(), startPt.getY() + 25), new MyPoint(endPt.getX(), endPt.getY() + 25),
					new MyPoint(endPt.getX(), endPt.getY() - 25) };

			answer = MyPoint.isInsideRegion(testPoint, border);
		}
		return answer;
	}
	
	public String getMeasureString() {
		return toFeetInches(MyPoint.dist(startPt, endPt) * scale);
	}

	private String toFeetInches(double meas) {
		int feet, inch;

		feet = (int) (Math.floor(meas));
		// look at inches
		meas -= feet;
		meas *= 12;
		inch = (int) (Math.floor(meas));
		// look at the fraction
		meas -= inch;

		int[] frac = simplify(meas);

		// correct for fraction = 1/1.
		if (frac[0] == 1 && frac[1] == 1) {
			inch += 1;
			frac[0] = 0;
		}

		String answer = "";
		if (feet > 0)
			answer = Integer.toString(feet) + "' ";

		answer += Integer.toString(inch);
		if (frac[0] > 0)
			answer += "-" + frac[0] + "/" + frac[1] + "\"";
		else
			answer += "\"";

		return answer;
	}

	private int[] simplify(double fraction) {
		int[] frac = new int[2];

		long a = Math.round(fraction * 8);
		long b = 8L;

		long gcd = gcmdenom(a, b);
		frac[0] = (int) (a / gcd);
		frac[1] = (int) (b / gcd);

		return frac;
	}

	private static long gcmdenom(long a, long b) {
		return b == 0 ? a : gcmdenom(b, a % b);
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public MyPoint getStartPt() {
		return startPt;
	}

	public void setStartPt(MyPoint startPt) {
		this.startPt = startPt;
	}

	public MyPoint getEndPt() {
		return endPt;
	}

	public void setEndPt(MyPoint endPt) {
		this.endPt = endPt;
	}

	public double getScale() {
		return scale;
	}
}

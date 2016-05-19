package com.plancrawler.model.utilities;

import java.io.Serializable;

public class MyPoint implements Serializable {

	private static final long serialVersionUID = 1L;
	private double x;
	private double y;

	public MyPoint() {
		this.x = 0;
		this.y = 0;
	}

	public MyPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public MyPoint(MyPoint copyPoint) {
		this.x = copyPoint.getX();
		this.y = copyPoint.getY();
	}

	public MyPoint add(MyPoint other) {
		return new MyPoint(this.x + other.x, this.y + other.y);
	}

	public void translate(MyPoint other) {
		this.x += other.getX();
		this.y += other.getY();
	}

	public void scale(double scalar) {
		x *= scalar;
		y *= scalar;
	}

	public double mag() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
	}

	public MyPoint normalize() {
		if (mag() == 0)
			return new MyPoint(0, 0);
		else {
			MyPoint newPoint = new MyPoint(this.x, this.y);
			newPoint.scale(1 / mag());
			return newPoint;
		}
	}

	public MyPoint neg() {
		return new MyPoint(-this.x, -this.y);
	}

	public double distTo(MyPoint other) {
		return (this.add(other.neg())).mag();
	}

	public double dot(MyPoint other) {
		return (this.x * other.getX() + this.y * other.getY());
	}

	public static double dist(MyPoint pt1, MyPoint pt2) {
		return pt1.distTo(pt2);
	}

	public static double dot(MyPoint pt1, MyPoint pt2) {
		return pt1.dot(pt2);
	}

	public static double angleRad(MyPoint pt1, MyPoint pt2) {
		return pt1.angleRad(pt2);
	}

	public double angleRad(MyPoint other) {
		return Math.acos(this.dot(other) / (this.mag() * other.mag()));
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setTo(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static MyPoint neg(MyPoint point) {
		return new MyPoint(-point.getX(), -point.getY());
	}

	public static MyPoint middle(MyPoint pt1, MyPoint pt2) {
		double cx, cy;
		
		cx = 0.5*(Math.max(pt1.x, pt2.x) + Math.min(pt1.x, pt2.x));
		cy = 0.5*(Math.max(pt1.y, pt2.y) + Math.min(pt1.y, pt2.y));
		
		return new MyPoint(cx,cy);
	}
	
	public static boolean isInsideRegion(MyPoint p, MyPoint[] border) {
		double angle = 0;
		MyPoint p1, p2;
		int numPts = border.length;

		for (int i = 0; i < numPts; i++) {
			p1 = new MyPoint(border[i]).add(MyPoint.neg(p));
			p2 = new MyPoint(border[(i+1)%numPts]).add(MyPoint.neg(p));
			angle += Angle2D(p1, p2);
		}

		if (Math.abs(angle) < Math.PI)
			return false;
		else
			return true;
	}

	/*
	 * Return the angle between two vectors on a plane The angle is from vector
	 * 1 to vector 2, positive anticlockwise The result is between -pi -> pi
	 */
	public static double Angle2D(MyPoint p1, MyPoint p2) {
		double dtheta, theta1, theta2;

		theta1 = Math.atan2(p1.getY(), p1.getX());
		theta2 = Math.atan2(p2.getY(), p2.getX());
		dtheta = theta2 - theta1;
		while (dtheta > Math.PI)
			dtheta -= 2*Math.PI;
		while (dtheta < -Math.PI)
			dtheta += 2*Math.PI;

		return dtheta;
	}
}
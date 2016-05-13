package com.plancrawler.model;

import java.io.Serializable;

import com.plancrawler.model.utilities.MyPoint;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;

	private int page;
	private MyPoint point;
	

	public Location(int page, MyPoint point) {
		this.page = page;
		this.point = new MyPoint(point);
	}

	public boolean isSameLocation(Location other) {
		boolean samePage = (this.page == other.page);
		boolean sameSpot = MyPoint.dist(this.point,  other.point) < 50;
		
		return (samePage && sameSpot);
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public MyPoint getPoint() {
		return new MyPoint(point);
	}

	public void setPoint(MyPoint point) {
		this.point = new MyPoint(point);
	}

}

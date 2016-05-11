package com.plancrawler.model;

import java.io.Serializable;

import com.plancrawler.model.utilities.MyPoint;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;

	private int page;
	private MyPoint point;
	private ItemLocations whereAt;

	public Location(int page, MyPoint loc, ItemLocations whereAt) {
		this.page = page;
		this.point = loc;
		this.whereAt = whereAt;
	}

	public boolean isSameLocation(Location other) {
		return (this.page == other.page && this.whereAt == other.whereAt && MyPoint.dist(this.point, other.point) < 50);
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

	public void setPoint(MyPoint loc) {
		this.point = loc;
	}

	public ItemLocations getWhereAt() {
		return whereAt;
	}

	public void setWhereAt(ItemLocations whereAt) {
		this.whereAt = whereAt;
	}
}

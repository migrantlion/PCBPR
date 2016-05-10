package com.plancrawler.model;

import java.io.Serializable;

import com.plancrawler.model.utilities.MyPoint;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;

	private int page;
	private MyPoint loc;
	private ItemLocations whereAt;

	public Location(int page, MyPoint loc, ItemLocations whereAt) {
		this.page = page;
		this.loc = loc;
		this.whereAt = whereAt;
	}

	public boolean isSameLocation(Location other) {
		return (this.page == other.page && this.whereAt == other.whereAt && MyPoint.dist(this.loc, other.loc) < 20);
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public MyPoint getLoc() {
		return loc;
	}

	public void setLoc(MyPoint loc) {
		this.loc = loc;
	}

	public ItemLocations getWhereAt() {
		return whereAt;
	}

	public void setWhereAt(ItemLocations whereAt) {
		this.whereAt = whereAt;
	}
}

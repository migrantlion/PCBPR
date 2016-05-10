package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;


public class Tokens implements Serializable {
	private static final long serialVersionUID = 1L;

	private Location location;
	private boolean visible;
	private Color color;
	private Color borderColor;
	
	public Tokens(Location location, Color color) {
		this.location = location;
		this.color = color;
		this.borderColor = color;
		visible = true;
	}

	public boolean isOnPage(int page) {
		return (location.getWhereAt() == ItemLocations.ON_PAGE && location.getPage() == page);
	}
	
	public boolean isCountable() {
		return (location.getWhereAt() != ItemLocations.IN_WAREHOUSE);
	}
	
	public boolean isAtLocation(Location loc) {
		return location.isSameLocation(loc);
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
	
}

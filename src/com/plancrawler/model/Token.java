package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;

public class Token implements Serializable, Countable {
	private static final long serialVersionUID = 1L;

	private Location location;
	private TokenLocations whereAt;
	private Crate crate = null;
	private boolean visible;
	private Color color;
	private Color borderColor;

	public Token(Location location, Color color, TokenLocations whereAt) {
		this.location = location;
		this.color = color;
		this.borderColor = color;
		this.whereAt = whereAt;
		visible = true;
	}

	public Token(Location location, Color color, TokenLocations whereAt, Crate crate) {
		this(location, color, whereAt);
		this.crate = crate;
	}

	public void setCrate(Crate crate) {
		this.crate = crate;
	}

	public boolean isOnPage(int page) {
		return (location.getPage() == page);
	}

	public boolean isInCrate() {
		return whereAt == TokenLocations.IN_CRATE;
	}

	public boolean isInCrate(Crate crate) {
		return (isInCrate() && this.crate.equals(crate));
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

	@Override
	public int count() {
		if (isInCrate())
			return 0;
		else
			return 1;
	}

	public TokenLocations getWhereAt() {
		return whereAt;
	}

}

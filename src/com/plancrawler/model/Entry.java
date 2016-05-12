package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;

public class Entry implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String name;
	protected String description;
	protected String category;
	protected Color color;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
}

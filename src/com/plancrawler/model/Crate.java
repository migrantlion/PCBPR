package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crate implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<Item> items = new ArrayList<Item>();
	private String name;
	private String description;
	private String category;
	private Color color;
	
	public Crate(String name, String description, String category, Color color) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.color = color;
	}
	
	public List<Item> getItems(){
		return Collections.unmodifiableList(items);
	}
	
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


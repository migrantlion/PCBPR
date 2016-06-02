package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;

public class Entry implements Serializable, Comparable<Entry> {
	private static final long serialVersionUID = 1L;
	protected String name;
	protected String description;
	protected String category;
	protected Color color;
	protected String manufacturer;
	protected String partNumber;

	public void copyProperties(Entry e){
		this.name = e.name;
		this.description = e.description;
		this.category = e.category;
		this.color = e.color;
		this.manufacturer = e.manufacturer;
		this.partNumber = e.partNumber;
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

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public boolean sameAs(Entry other) {
		return (this.compareTo(other) == 0 && this.description.equals(other.description)
				&& this.color.equals(other.color));
	}

	@Override
	public int compareTo(Entry other) {
		if (this.category.equals(other.category))
			return this.name.compareTo(other.name);
		else
			return this.category.compareTo(other.category);
	}

}

package com.plancrawler.view.support;

import java.awt.Color;
import java.util.EventObject;

public class EntryFormEvent extends EventObject {
	static final long serialVersionUID = 1L;
	private String entryName, entryDesc, entryCat, entryManufacturer, entryPartNumber;
	private Color entryColor;
	private boolean addItem, addCrate;

	public EntryFormEvent(Object source) {
		super(source);
	}

	public EntryFormEvent(Object source, String name, String desc, String cat, Color color) {
		this(source);
		this.entryName = name;
		this.entryDesc = desc;
		this.entryCat = cat;
		this.entryColor = color;
		this.addItem = false;
		this.addCrate = false;
	}

	public EntryFormEvent(Object source, String name, String desc, String cat, Color color, String manuf,
			String part) {
		this(source, name, desc, cat, color);
		this.entryManufacturer = manuf;
		this.entryPartNumber = part;
	}

	public String getEntryName() {
		return entryName;
	}

	public void setEntryName(String name) {
		this.entryName = name;
	}

	public String getEntryDesc() {
		return entryDesc;
	}

	public void setEntryDesc(String entryDesc) {
		this.entryDesc = entryDesc;
	}

	public String getEntryCat() {
		return entryCat;
	}

	public void setEntryCat(String cat) {
		this.entryCat = cat;
	}

	public Color getEntryColor() {
		return entryColor;
	}

	public void setEntryColor(Color entryColor) {
		this.entryColor = entryColor;
	}

	public boolean isAddItem() {
		return addItem;
	}

	public void setAddItem(boolean addItem) {
		this.addItem = addItem;
	}

	public boolean isAddCrate() {
		return addCrate;
	}

	public void setAddCrate(boolean addCrate) {
		this.addCrate = addCrate;
	}

	public String getEntryManufacturer() {
		return entryManufacturer;
	}

	public void setEntryManufacturer(String entryManufacturer) {
		this.entryManufacturer = entryManufacturer;
	}

	public String getEntryPartNumber() {
		return entryPartNumber;
	}

	public void setEntryPartNumber(String entryPartNumber) {
		this.entryPartNumber = entryPartNumber;
	}

}

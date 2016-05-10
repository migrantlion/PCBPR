package com.plancrawler.view.support;

import java.awt.Color;
import java.util.EventObject;

public class ItemFormEvent extends EventObject{
	static final long serialVersionUID = 1L;
	private String itemName, itemDesc, itemCat;
	private Color itemColor;
	
	public ItemFormEvent(Object source) {
		super(source);
	}
	
	public ItemFormEvent(Object source, String name, String desc, String cat, Color color) {
		this(source);
		this.itemName = name;
		this.itemDesc = desc;
		this.itemCat = cat;
		this.itemColor = color;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public String getItemCat() {
		return itemCat;
	}

	public void setItemCat(String itemCat) {
		this.itemCat = itemCat;
	}

	public Color getItemColor() {
		return itemColor;
	}

	public void setItemColor(Color itemColor) {
		this.itemColor = itemColor;
	}
}


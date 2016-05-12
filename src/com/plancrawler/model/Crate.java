package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crate implements Serializable, Comparable<Crate> {
	private static final long serialVersionUID = 1L;

	private List<Item> items = new ArrayList<Item>();
	private List<Tokens> crateTokens = new ArrayList<Tokens>();

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

	public List<Item> getItems() {
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

	public int getItemCount() {
		return items.size();
	}

	public List<Integer> contentsOnWhatPage() {
		List<Integer> pages = new ArrayList<Integer>();

		for (Item i : items)
			pages.addAll(i.foundOnWhatPage());

		List<Integer> uniquePages = new ArrayList<Integer>();
		Collections.sort(pages);
		for (int i : pages)
			if (!uniquePages.contains(i))
				uniquePages.add(i);

		return uniquePages;
	}
	
	public List<Integer> tokensOnWhatPage(){
		List<Integer> pages = new ArrayList<Integer>();

		for (Tokens t : crateTokens)
			pages.add(t.getLocation().getPage());

		List<Integer> uniquePages = new ArrayList<Integer>();
		Collections.sort(pages);
		for (int i : pages)
			if (!uniquePages.contains(i))
				uniquePages.add(i);

		return uniquePages;
	}

	@Override
	public int compareTo(Crate other) {
		if (this.category == other.category)
			return this.name.compareTo(other.name);
		else
			return this.category.compareTo(other.category);
	}

}

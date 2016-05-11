package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Item implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 1L;
	private static int counter = 0;
	private int id;
	private String name;
	private String description;
	private String category;
	private Color color;

	private CopyOnWriteArrayList<Tokens> tokens = new CopyOnWriteArrayList<Tokens>();

	public Item(String name, String description, String category, Color color) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.color = color;
		this.id = counter;
		counter++;
	}

	public void addToken(Location loc) {
		Tokens token = new Tokens(loc, color);
		tokens.add(token);
	}

	public boolean remToken(Location loc) {
		Tokens token = null;
		for (Tokens t : tokens)
			if (t.isAtLocation(loc))
				token = t;

		if (token == null)
			return false;
		else
			return tokens.remove(token);
	}

	public int getTokenCount() {
		int count = 0;
		for (Tokens t : tokens) {
			if (t.isCountable())
				count++;
		}
		return count;
	}
	
	public List<Tokens> getTokensOnPage(int page) {
		List<Tokens> tokensOnPage = new ArrayList<Tokens>();
		for (Tokens t : tokens)
			if (t.isOnPage(page))
				tokensOnPage.add(t);
		return tokensOnPage;
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

	public CopyOnWriteArrayList<Tokens> getTokens() {
		return tokens;
	}

	public void setTokens(CopyOnWriteArrayList<Tokens> tokens) {
		this.tokens = tokens;
	}

	public int getId() {
		return id;
	}

	@Override
	public int compareTo(Item other) {
		if (this.category == other.category)
			return this.name.compareTo(other.name);
		else
			return this.category.compareTo(other.category);
	}
}

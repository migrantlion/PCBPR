package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Crate extends Entry implements Serializable {
	private static final long serialVersionUID = 1L;

	private HashMap<Location, Item> itemsInCrate = new HashMap<Location, Item>();
	private HashMap<Location, ArrayList<Token>> registeredTokens = new HashMap<Location, ArrayList<Token>>();
	private List<Token> crateTokens = new ArrayList<Token>();

	public Crate(String name, String description, String category, Color color) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.color = color;
	}
	
	public void clearCrateContents() {
		itemsInCrate.clear();
	}

	public List<Item> getItemsInCrate() {
		List<Item> items = new ArrayList<Item>();

		for (Location loc : itemsInCrate.keySet()) {
			Item crateItem = itemsInCrate.get(loc);
			Item passItem = new Item(crateItem);
			for (Token t : crateItem.getTokens()){
				if (t.isInCrate(this)){
					Token passT = new Token(t.getLocation(), t.getColor(), TokenLocations.ON_PAGE, this);
					passItem.addToken(passT);
				}
			}
			boolean addThis = true;
			for (Item i : items)
				if (i.sameAs(passItem))
					addThis = false;
			if (addThis)
				items.add(passItem);
		}

		return Collections.unmodifiableList(items);
	}

	public void addItemToCrateAtLocation(Item item, Location location) {
		itemsInCrate.put(location, item);
		registeredTokens.put(location, new ArrayList<Token>());
		// add a token to the item indicating it is in this crate
		item.addToken(location, TokenLocations.IN_CRATE, this);
		
		// now update item tokens for places where crate has been placed
		for (Token t : crateTokens){
			Token token = new Token(t.getLocation(), t.getColor(), TokenLocations.ON_PAGE, this);
			token.setVisible(false);
			item.addToken(token);
			registeredTokens.get(location).add(token);
		}
	}

	public boolean remItemInCrateAtLocation(Location location) {
		Location keyLoc = null;
		for (Location loc : itemsInCrate.keySet())
			if (loc.isSameLocation(location))
				keyLoc = loc;

		if (keyLoc == null)
			return false;
		else {
			Item i = itemsInCrate.get(keyLoc);
			for (Token t : registeredTokens.get(keyLoc))
				i.remToken(t);
			itemsInCrate.remove(keyLoc);
			registeredTokens.remove(keyLoc);
		}
		return true;
	}
	
	public void setColor(Color color) {
		this.color = color;
		for (Token t: crateTokens)
			t.setColor(color);
		for (Location key : registeredTokens.keySet())
			for (Token t : registeredTokens.get(key))
				t.setBorderColor(color);
	}

	public int getItemCount() {
		return itemsInCrate.size();
	}

	public List<Integer> contentsOnWhatPage() {
		List<Integer> pages = new ArrayList<Integer>();

		for (Location loc : itemsInCrate.keySet()) {
			// get first item in the crate
			Item i = itemsInCrate.get(loc);
			// look through tokens to find the ones inside this crate
			for (Token t : i.getTokens())
				// if find a token in this crate, add it to the pages list
				if (t.isInCrate(this))
					pages.add(t.getLocation().getPage());
		}

		List<Integer> uniquePages = new ArrayList<Integer>();
		Collections.sort(pages);
		for (int i : pages)
			if (!uniquePages.contains(i))
				uniquePages.add(i);

		return uniquePages;
	}

	public List<Integer> tokensOnWhatPage() {
		List<Integer> pages = new ArrayList<Integer>();

		for (Token t : crateTokens)
			pages.add(t.getLocation().getPage());

		List<Integer> uniquePages = new ArrayList<Integer>();
		Collections.sort(pages);
		for (int i : pages)
			if (!uniquePages.contains(i))
				uniquePages.add(i);

		return uniquePages;
	}

	public boolean contains(Entry itemEntry) {
		boolean answer = false;
		for (Location loc : itemsInCrate.keySet())
			if (itemsInCrate.get(loc).sameAs(itemEntry))
				answer = true;
		return answer;
	}

	public Item getItem(Entry itemEntry) {
		Item returnItem = null;
		for (Location loc : itemsInCrate.keySet())
			if (itemsInCrate.get(loc).sameAs(itemEntry))
				returnItem = itemsInCrate.get(loc);
		return returnItem;
	}

	public void addToken(Location loc) {
		crateTokens.add(new Token(loc, getColor(), TokenLocations.ON_PAGE));
		// add this token to every item in the crate
		for (Location key : itemsInCrate.keySet()) {
			Item i = itemsInCrate.get(key);
			Token token = new Token(loc, this.color, TokenLocations.ON_PAGE, this);
			token.setVisible(false);
			i.addToken(token);
			registeredTokens.get(key).add(token);
		}
	}

	public List<Token> getTokensOnPage(int page) {
		List<Token> retTokens = new ArrayList<Token>();
		for (Token t : crateTokens)
			if (t.isOnPage(page))
				retTokens.add(t);
		return Collections.unmodifiableList(retTokens);
	}

	public int numPlacements() {
		int count = 0;
		for (Token t : crateTokens)
			count += t.count();
		return count;
	}

}

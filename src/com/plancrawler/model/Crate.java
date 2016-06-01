package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crate extends Entry implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Token> crateTokens = new ArrayList<Token>();
	private class TokenDB implements Serializable {
		private static final long serialVersionUID = 1L;
		Location index;
		Item item;
		List<Token> placedTokens = new ArrayList<Token>();
		Token locInCrateToken;
	}
	private List<TokenDB> tokenDB = new ArrayList<TokenDB>();

	public Crate(String name, String description, String category, Color color) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.color = color;
	}

	public List<Item> getItemsOnlyInCrate() {
		List<Item> items = new ArrayList<Item>();

		for (TokenDB entry : tokenDB) {
			// create a new item, since we only want to put in tokens which are
			// in this crate
			Item item = new Item(entry.item);
			// create a new token which is not "in crate" so that it can be
			// counted by the tables
			Token tToken = new Token(entry.locInCrateToken.getLocation(), entry.locInCrateToken.getColor(),
					TokenLocations.ON_PAGE, this);
			item.addToken(tToken);
			items.add(item);
		}
		return Collections.unmodifiableList(items);
	}

	public void addItemToCrateAtLocation(Item item, Location location) {
		TokenDB newEntry = new TokenDB();

		newEntry.item = item;
		newEntry.index = location;
		newEntry.locInCrateToken = new Token(location, item.getColor(), TokenLocations.IN_CRATE, this);
		newEntry.locInCrateToken.setBorderColor(this.color);
		item.addToken(newEntry.locInCrateToken);

		// now update item tokens for places where crate has been placed
		for (Token t : crateTokens) {
			Token token = new Token(t.getLocation(), item.getColor(), TokenLocations.ON_PAGE, this);
			token.setBorderColor(color);
			token.setVisible(false);
			item.addToken(token);
			newEntry.placedTokens.add(token);
		}

		tokenDB.add(newEntry);
	}

	private TokenDB getTokenDBAtLocation(Location location) {
		TokenDB tdb = null;
		for (TokenDB t : tokenDB)
			if (t.index.isSameLocation(location))
				tdb = t;
		return tdb;
	}

	public boolean remItemInCrateAtLocation(Location location) {
		TokenDB tdb = getTokenDBAtLocation(location);
		if (tdb == null)
			return false;

		// need to remove all the placed tokens from this item
		for (Token t : tdb.placedTokens)
			tdb.item.remToken(t);

		// remove the item token showing in the crate
		tdb.item.remToken(tdb.locInCrateToken);

		// finally remove the tdb entry from the database
		return tokenDB.remove(tdb);
	}

	public void setColor(Color color) {
		this.color = color;
		// need to go through every item token and change the border color

		for (TokenDB tdb : tokenDB) {
			tdb.locInCrateToken.setBorderColor(color);
			for (Token t : tdb.placedTokens)
				t.setBorderColor(color);
		}
		
		// need to change all the crate Token colos also
		for (Token t : crateTokens){
			t.setColor(color);
			t.setBorderColor(color);
		}
	}

	public int getItemsInCrateCount() {
		return tokenDB.size();
	}

	public List<Integer> contentsOnWhatPage() {
		List<Integer> pages = new ArrayList<Integer>();

		for (TokenDB tdb : tokenDB)
			pages.add(tdb.locInCrateToken.getLocation().getPage());

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

	public void addToken(Location loc) {
		crateTokens.add(new Token(loc, getColor(), TokenLocations.ON_PAGE));
		// add this token to every item in the crate
		for (TokenDB tdb : tokenDB) {
			Token token = new Token(loc, tdb.item.getColor(), TokenLocations.ON_PAGE, this);
			token.setBorderColor(color);
			token.setVisible(false);
			tdb.item.addToken(token);
			tdb.placedTokens.add(token);
		}
	}

	public void remToken(Location loc) {
		// first ensure the crate is at this location
		Token remToken = null;
		for (Token t : crateTokens)
			if (t.isAtLocation(loc))
				remToken = t;

		// didn't find it
		if (remToken == null)
			return;

		// since it exists, remove all placements from the database
		// use remToken as location identifier, since this is the token we will
		// remove
		for (TokenDB tdb : tokenDB) {
			Token pToken = null;
			for (Token t : tdb.placedTokens)
				if (t.isAtLocation(remToken.getLocation()))
					pToken = t;
			if (pToken != null) {
				tdb.item.remToken(pToken);
				tdb.placedTokens.remove(pToken);
			}
		}

		// now remove the token from the crate
		crateTokens.remove(remToken);
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

	public boolean remItemInCrateAtLocation(Location testLocation, Item item) {
		// find database entry for this item at this location
		TokenDB tdbEntry = null;
		for (TokenDB tdb : tokenDB) {
			if (tdb.item.equals(item) && tdb.locInCrateToken.isAtLocation(testLocation))
				tdbEntry = tdb;
		}

		if (tdbEntry == null)
			return false;
		else {
			// need to remove all the placed tokens from this item
			for (Token t : tdbEntry.placedTokens)
				tdbEntry.item.remToken(t);

			// remove the item token showing in the crate
			tdbEntry.item.remToken(tdbEntry.locInCrateToken);

			// finally remove the tdb entry from the database
			return tokenDB.remove(tdbEntry);
		}
	}

	public void removeItemInstancesFromCrate(Item item) {
		// need to remove all tokenDB entries with this item in it
		List<TokenDB> remList = new ArrayList<TokenDB>();
		for (TokenDB tdb : tokenDB)
			if (tdb.item.equals(item))
				remList.add(tdb);

		for (TokenDB tdb : remList)
			tokenDB.remove(tdb);
	}

	public void clearCrate() {
		// need to empty all tokens from the items
		for (TokenDB tdb : tokenDB) {
			// need to remove all the placed tokens from this item
			for (Token t : tdb.placedTokens)
				tdb.item.remToken(t);

			// remove the item token showing in the crate
			tdb.item.remToken(tdb.locInCrateToken);
		}

		// now empty the token database
		tokenDB.clear();
	}
}

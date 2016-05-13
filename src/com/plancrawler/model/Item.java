package com.plancrawler.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Item extends Entry implements Serializable {

	private static final long serialVersionUID = 1L;
	private static int counter = 0;
	private int id;
//	private String name;
//	private String description;
//	private String category;
//	private Color color;

	private CopyOnWriteArrayList<Token> tokens = new CopyOnWriteArrayList<Token>();

	public Item(String name, String description, String category, Color color) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.color = color;
		this.id = counter;
		counter++;
	}
	
	public Item(Entry entry){
		this(entry.name, entry.description, entry.category, entry.color);
	}

	public void addToken(Location loc, TokenLocations whereAt) {
		Token token = new Token(loc, color, whereAt);
		tokens.add(token);
	}
	
	public void addToken(Location loc, TokenLocations whereAt, Crate crate) {
		Token token = new Token(loc, color, whereAt, crate);
		tokens.add(token);
	}

	public boolean remToken(Location loc) {
		Token token = null;
		for (Token t : tokens)
			if (t.isAtLocation(loc))
				token = t;

		if (token == null)
			return false;
		else
			return tokens.remove(token);
	}

	public int getTokenCount() {
		int count = 0;
		for (Token t : tokens) {
			count += t.count();
		}
		return count;
	}
	
	public List<Token> getTokensOnPage(int page) {
		List<Token> tokensOnPage = new ArrayList<Token>();
		for (Token t : tokens)
			if (t.isOnPage(page))
				tokensOnPage.add(t);
		return tokensOnPage;
	}
	
	public List<Integer> foundOnWhatPage(){
		List<Integer> pages = new ArrayList<Integer>();
		List<Integer> uniquePages = new ArrayList<Integer>();
		
		for (Token t : tokens) {
			if (t.getWhereAt() == TokenLocations.ON_PAGE)
				pages.add(t.getLocation().getPage());
		}
		
		Collections.sort(pages);
		for (int i : pages)
			if (!uniquePages.contains(i))
				uniquePages.add(i);
		
		return uniquePages;
	}

	public void setColor(Color color) {
		this.color = color;
		for (Token t: tokens)
			t.setColor(color);
	}

	public CopyOnWriteArrayList<Token> getTokens() {
		return tokens;
	}

	public void setTokens(CopyOnWriteArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	public int getId() {
		return id;
	}

	public void wipeTokens() {
		tokens.clear();
	}

	public void addToken(Token token) {
		token.setColor(color);
		tokens.add(token);
	}
	
	public boolean remToken(Token token){
		return tokens.remove(token);
	}
}

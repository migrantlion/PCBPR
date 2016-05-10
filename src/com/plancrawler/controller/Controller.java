package com.plancrawler.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.plancrawler.model.DocumentHandler;
import com.plancrawler.model.Item;
import com.plancrawler.model.ItemDatabase;
import com.plancrawler.model.Tokens;
import com.plancrawler.view.support.ItemFormEvent;
import com.plancrawler.view.support.Paintable;
import com.plancrawler.view.support.RotToolbarEvent;

public class Controller {

	private ItemDatabase db;
	private DocumentHandler pdfDoc;

	public Controller() {
		this.db = ItemDatabase.getInstance();
		this.pdfDoc = new DocumentHandler();
	}

	public String loadPDF(File file) {
		pdfDoc.clearDocProperties();
		if (file != null) {
			pdfDoc.setCurrentFile(file.getAbsolutePath());
			pdfDoc.setDocProperties();
		}
		return pdfDoc.getCurrentFile();
	}
	
	public List<Item> getItems() {
		return db.getItems();
	}

	public void addItem(ItemFormEvent e) {
		// takes in an itemFormEvent and populates a new item to add to the
		// database
		String name = e.getItemName();
		String desc = e.getItemDesc();
		String cat = e.getItemCat();
		Color color = e.getItemColor();

		Item item = new Item(name, desc, cat, color);
		db.addItem(item);
	}

	public void handlePageRotation(RotToolbarEvent e) {
		System.out.println("rotation : " + e.getRotation() + "  all: " + e.isRotAll());
	}

	public void saveToFile(File file) throws IOException {
		db.saveToFile(file);
	}

	public void loadFromFile(File file) throws IOException {
		db.loadFromFile(file);
	}

	public List<Paintable> getPaintables(int page, List<String> displayList) {
		List<Item> items = getItems();
		List<TokenPainter> tokenPainter = new ArrayList<TokenPainter>();
		List<Paintable> paintable = new ArrayList<Paintable>();

		for (Item i : items) {
			if (isInDisplayList(i, displayList)) {
				List<Tokens> tokens = i.getTokensOnPage(page);
				for (Tokens t : tokens)
					tokenPainter.add(new TokenPainter(t));
			}
		}
		paintable.addAll(tokenPainter);
		return paintable;
	}

	private boolean isInDisplayList(Item i, List<String> list) {
		boolean answer = false;
		for (String s : list)
			if (s.equals(i.getName()))
				answer = true;
		return answer;
	}

	public File getCurrentPDFDirectory() {
		return new File(pdfDoc.getCurrentPath());
	}

	public BufferedImage getCurrentPageImage() {
		return pdfDoc.getCurrentPageImage();
	}
}

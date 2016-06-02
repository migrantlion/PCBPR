package com.plancrawler.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.plancrawler.controller.fileOps.DocumentHandler;
import com.plancrawler.controller.fileOps.PDFMerge;
import com.plancrawler.controller.fileOps.TableExporter;
import com.plancrawler.model.Crate;
import com.plancrawler.model.Database;
import com.plancrawler.model.Item;
import com.plancrawler.model.Location;
import com.plancrawler.model.Measurement;
import com.plancrawler.model.Token;
import com.plancrawler.model.utilities.MyPoint;
import com.plancrawler.view.support.EntryFormEvent;
import com.plancrawler.view.support.ItemTableModel;
import com.plancrawler.view.toolbars.RotToolbarEvent;

public class Controller {

	public final static String VERSION = "1.4.1";

	private Database db;
	private DocumentHandler pdfDoc;
	private int activeItemRow = -1;
	private boolean isMeasuring = false;
	private int activeCrateRow = -1;
	private int cratePanelActive = -1;;

	public Controller() {
		this.db = Database.getInstance();
		this.pdfDoc = new DocumentHandler();
	}

	public String loadPDF(File file) {
		pdfDoc.clearDocProperties();
		if (file != null) {
			pdfDoc.setCurrentFile(file.getAbsolutePath());
			pdfDoc.setDocProperties();
		}
		db.setAssociatedPDFName(pdfDoc.getCurrentFile());
		return pdfDoc.getCurrentFile();
	}

	public void cleanup() {
		pdfDoc.cleanUp();
	}

	public List<Item> getItems() {
		return db.getItems();
	}

	public void addItem(EntryFormEvent e) {
		// takes in an itemFormEvent and populates a new item to add to the
		// database
		String name = e.getEntryName();
		String desc = e.getEntryDesc();
		String cat = e.getEntryCat();
		Color color = e.getEntryColor();
		String manufacturer = e.getEntryManufacturer();
		String partNumber = e.getEntryPartNumber();

		Item item = new Item(name, desc, cat, color, manufacturer, partNumber);
		db.addItem(item);
	}

	public boolean hasItemByName(String name) {
		List<Item> items = db.getItems();
		boolean answer = false;
		for (Item i : items)
			if (i.getName().equals(name))
				answer = true;
		return answer;
	}

	public void handlePageRotation(RotToolbarEvent e) {
		if (e.getRotation() != 0)
			pdfDoc.registerRotation(e.getRotation());
		if (e.isRotAll())
			pdfDoc.registerRotationToAllPages();
	}

	public void saveToFile(File file) throws IOException {
		db.saveToFile(file, pdfDoc);
	}

	public void loadFromFile(File file) throws IOException {
		pdfDoc = db.loadFromFile(file);
		if (pdfDoc == null)
			pdfDoc = new DocumentHandler();
		pdfDoc.setDocProperties();
	}

	public List<Paintable> getPaintables(int page) {
		List<Item> items = getItems();
		List<TokenPainter> tokenPainter = new ArrayList<TokenPainter>();
		List<Paintable> paintable = new ArrayList<Paintable>();
		List<Token> tokens;

		// add item tokens
		for (Item i : items) {
			tokens = i.getTokensOnPage(page);
			for (Token t : tokens)
				tokenPainter.add(new TokenPainter(t));
			tokens.clear();
		}
		paintable.addAll(tokenPainter);

		// add crate info
		List<Crate> crates = getCrates();
		List<CratePainter> ctokens = new ArrayList<CratePainter>();
		for (Crate c : crates) {
			tokens = c.getTokensOnPage(page);
			for (Token t : tokens)
				ctokens.add(new CratePainter(t));
		}
		paintable.addAll(ctokens);
		paintable.addAll(tokenPainter);

		// add measurement marks
		List<Measurement> measurements = db.getMeasurements();
		for (Measurement m : measurements) {
			if (m.getPage() == getCurrentPage())
				paintable.add(new MeasurePainter(m));
		}

		return paintable;
	}

	public File getCurrentPDFDirectory() {
		return new File(pdfDoc.getCurrentPath());
	}

	public BufferedImage getCurrentPageImage() {
		return pdfDoc.getCurrentPageImage();
	}

	public BufferedImage getPageImage(int page) {
		return pdfDoc.getPageImage(page);
	}

	public int getCurrentPage() {
		return pdfDoc.getCurrentPage();
	}

	public int getNumPages() {
		return pdfDoc.getNumPages();
	}

	public void deleteItemRow(int row) {
		db.deleteRow(row);
		activeItemRow = -1;
	}

	public int getActiveItemRow() {
		return activeItemRow;
	}

	public void setActiveItemRow(int activeItemRow) {
		this.activeItemRow = activeItemRow;
	}

	public boolean hasActiveItem() {
		return activeItemRow >= 0;
	}

	public void dropToken(MyPoint point) {
		if (isMeasuring)
			return;

		Location loc = new Location(getCurrentPage(), point);
		;
		if (hasActiveItem() && hasActiveCrate()) {
			// place item in crate
			db.addItemToCrate(loc, activeItemRow, activeCrateRow);
		} else if (hasActiveCrate()) {
			// place crate on page
			db.addTokenToCrate(loc, activeCrateRow);
		} else if (hasActiveItem()) {
			db.addTokenToItem(loc, activeItemRow);
		}
	}

	public void removeToken(MyPoint point) {
		Location loc = new Location(getCurrentPage(), point);
		if (hasActiveItem() && hasActiveCrate()) {
			db.remItemFromCrate(loc, activeItemRow, activeCrateRow);
		} else if (hasActiveCrate()) {
			db.remTokenFromCrate(loc, activeCrateRow);
		} else if (hasActiveItem()) {
			db.remTokenFromItem(loc, activeItemRow);
		}
	}

	public void clearTables() {
		db.wipeTokens();
	}

	public void clearDatabase() {
		db.clearAll();
	}

	public String getAssociatedPDFName() {
		return db.getAssociatedPDFName();
	}

	public void addMeasurement(Measurement meas) {
		db.addMeasurement(new Measurement(meas.getStartPt(), meas.getEndPt(), getCurrentPage(), meas.getScale()));
	}

	public void removeMeasurement(MyPoint point) {
		db.remMeasurement(point, getCurrentPage());
	}

	public void setMeasuring(boolean state) {
		isMeasuring = state;
	}

	public boolean isMeasuring() {
		return isMeasuring;
	}

	public Item getItem(int row) {
		return db.getItem(row);
	}

	public void modifyItem(int row, EntryFormEvent ife) {
		Item item = new Item(ife.getEntryName(), ife.getEntryDesc(), ife.getEntryCat(), ife.getEntryColor(), ife.getEntryManufacturer(), ife.getEntryPartNumber());
		db.modifyEntry(row, item);
	}

	public List<Crate> getCrates() {
		return db.getCrates();
	}

	public List<Item> getItemsInCrate(int crateIndex) {
		return db.getItemsInCrate(crateIndex);
	}

	public boolean hasCrateByName(String name) {
		List<Crate> crates = db.getCrates();
		boolean answer = false;
		for (Crate c : crates)
			if (c.getName().equals(name))
				answer = true;
		return answer;
	}

	public void addCrate(EntryFormEvent e) {
		String name = e.getEntryName();
		String desc = e.getEntryDesc();
		String cat = e.getEntryCat();
		Color color = e.getEntryColor();

		Crate crate = new Crate(name, desc, cat, color);
		db.addCrate(crate);

	}

	public void setActiveCrateRow(int row) {
		this.activeCrateRow = row;
	}

	public int getActiveCrateRow() {
		return activeCrateRow;
	}

	public boolean hasActiveCrate() {
		return (activeCrateRow >= 0);
	}

	public void modifyCrate(int row, EntryFormEvent ife) {
		Crate crate = new Crate(ife.getEntryName(), ife.getEntryDesc(), ife.getEntryCat(), ife.getEntryColor());
		db.modifyCrateEntry(row, crate);
	}

	public Crate getCrate(int row) {
		return db.getCrate(row);
	}

	public boolean deleteCrateRow(int row) {
		return db.remCrate(row);
	}

	public boolean hasActive() {
		return hasActiveCrate() || hasActiveItem();
	}

	public void setCratePanelActive(int row) {
		this.cratePanelActive = row;
	}

	public int getCratePanelActive() {
		return cratePanelActive;
	}

	public String getActiveCrateName() {
		if (hasActiveCrate())
			return db.getCrate(activeCrateRow).getName();
		else
			return null;
	}

	public String getActiveItemName() {
		if (hasActiveItem())
			return db.getItem(activeItemRow).getName();
		else
			return null;
	}

	public void setPaths(String pdfPath, String tempPath) {
		pdfDoc.setPaths(pdfPath, tempPath);
	}

	public void saveTableAsCSV(ItemTableModel tableModel, File file) {
		TableExporter.saveTableAsCSV(tableModel, file);

	}

	public void mergePDFs(File[] files, File saveFile) throws IOException {
		PDFMerge.mergePDFs(files, saveFile);
	}
}

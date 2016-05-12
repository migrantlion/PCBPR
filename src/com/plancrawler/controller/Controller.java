package com.plancrawler.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.plancrawler.model.Database;
import com.plancrawler.model.DocumentHandler;
import com.plancrawler.model.Item;
import com.plancrawler.model.ItemLocations;
import com.plancrawler.model.Location;
import com.plancrawler.model.Measurement;
import com.plancrawler.model.Tokens;
import com.plancrawler.model.utilities.MyPoint;
import com.plancrawler.view.support.ItemFormEvent;
import com.plancrawler.view.support.Paintable;
import com.plancrawler.view.support.RotToolbarEvent;

public class Controller {

	private Database db;
	private DocumentHandler pdfDoc;
	private int activeItemRow = -1;
	private boolean isMeasuring = false;
	private boolean isCalibrating = false;
	private double activeScale = 1;

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
	
	public boolean hasItemByName(String name){
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
		db.saveToFile(file);
	}

	public void loadFromFile(File file) throws IOException {
		db.loadFromFile(file);
		loadPDF(new File(db.getAssociatedPDFName()));
	}

	public List<Paintable> getPaintables(int page) {
		List<Item> items = getItems();
		List<TokenPainter> tokenPainter = new ArrayList<TokenPainter>();
		List<Paintable> paintable = new ArrayList<Paintable>();

		// add item tokens
		for (Item i : items) {
			List<Tokens> tokens = i.getTokensOnPage(page);
			for (Tokens t : tokens)
				tokenPainter.add(new TokenPainter(t));
			tokens.clear();
		}
		paintable.addAll(tokenPainter);
		
		// add measurement marks
		List<Measurement> measurements = db.getMeasurements();
		for (Measurement m : measurements){
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
		if (hasActiveItem()) {
			Location loc = new Location(getCurrentPage(), point, ItemLocations.ON_PAGE);
			db.addTokenToItem(loc, activeItemRow);
		}
	}

	public void removeToken(MyPoint point) {
		if (hasActiveItem()) {
			Location loc = new Location(getCurrentPage(), point, ItemLocations.ON_PAGE);
			db.remTokenFromItem(loc, activeItemRow);
		}	
	}

	public void clearTables() {
		db.wipeTokens();
	}

	public String getAssociatedPDFName() {
		return db.getAssociatedPDFName();
	}
	
	public boolean isMeasuring() {
		return isMeasuring;
	}

	public void setMeasuring(boolean isMeasuring) {
		this.isMeasuring = isMeasuring;
	}
	
	public double getActiveScale(){
		return activeScale;
	}

	public void setCalibration(int calibrationIndex) {
		switch (calibrationIndex){
		case 0: 
			activeScale = 1; break;
		case 1: // 1/4" = 1'
			activeScale = Measurement.getCalibration(0.25, 1.0); break;
		case 2: // 1/2" = 1'
			activeScale = Measurement.getCalibration(0.50, 1.0); break;
		case 3: // 1/3" = 1'
			activeScale = Measurement.getCalibration(0.333, 1.0); break;
		case 4: // 1/8" = 1'
			activeScale = Measurement.getCalibration(0.025, 1.0); break;
		case 5: // this is custom, so we will change this with a calibration measurement.
			activeScale = 1; isCalibrating = true; isMeasuring = true; break;
		}
	}
	
	public void doMeasurement(Measurement meas) {
		if (isCalibrating){
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					double dist = MyPoint.dist(meas.getStartPt(), meas.getEndPt());
					activeScale = CalibrationDialog.calibrate(null, dist, Measurement.toFeetInches(dist));
					isCalibrating = false;
					isMeasuring = false;
				}
			});
		} else if (isMeasuring) {
			db.addMeasurement(new Measurement(meas.getStartPt(), meas.getEndPt(), getCurrentPage(), getActiveScale()));
			isMeasuring = false;
		}
	}

	public void removeMeasurement(MyPoint point) {
		db.remMeasurement(point, getCurrentPage());
	}
}

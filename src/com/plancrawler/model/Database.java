package com.plancrawler.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Database {
	private static Database uniqueInstance = new Database();
	
	private String associatedPDFName = null;
	private List<Item> items; 
	private List<Measurement> measurements;
	
	private Database() {
		items = new LinkedList<Item>();
		measurements = new LinkedList<Measurement>();
	}
	
	public static Database getInstance() {
		return uniqueInstance;
	}
	
	public void addMeasurement(Measurement measure){
		measurements.add(measure);
	}
	
	public boolean remMeasurement(Measurement measure){
		return measurements.remove(measure);
	}
	
	public List<Measurement> getMeasurements(){
		return Collections.unmodifiableList(measurements);
	}
	
	public void addItem(Item item) {
		items.add(item);
		Collections.sort(items);
	}
	
	public boolean remItem(Item item) {
		return items.remove(item);
	}
	
	public void addTokenToItem(Location loc, int itemIndex){
		Item i = getItem(itemIndex);
		if (i == null){
			System.err.println("Unknown row ("+itemIndex+") requested");
		} else
			i.addToken(loc);
	}
	
	public boolean remTokenFromItem(Location loc, int itemIndex){
		Item i = getItem(itemIndex);
		if (i == null)
			return false;
		else
			return i.remToken(loc);
	}
	
	private Item getItem(int index){
		if (index < 0 || index > items.size())
			return null;
		else
			return items.get(index);
	}
	
	public List<Item> getItems(){
		return Collections.unmodifiableList(items);
	}
	
	public void saveToFile(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		Item[] itemArray = items.toArray(new Item[items.size()]);
		Measurement[] mArray = measurements.toArray(new Measurement[measurements.size()]);
		
		oos.writeObject(itemArray);
		oos.writeObject(mArray);
		oos.writeObject(associatedPDFName);
		
		oos.close();
	}
	
	public void loadFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		try {
			Item[] itemArray = (Item[]) ois.readObject();
			Measurement[] mArray = (Measurement[]) ois.readObject();
			associatedPDFName = (String) ois.readObject();
			
			items.clear();
			items.addAll(Arrays.asList(itemArray));
			
			measurements.clear();
			measurements.addAll(Arrays.asList(mArray));
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		ois.close();
	}

	public void deleteRow(int row) {
		if (row < 0 || row > items.size())
			return;
		items.remove(row);		
	}

	public void wipeTokens() {
		for (Item item : items){
			item.wipeTokens();
		}
	}

	public String getAssociatedPDFName() {
		return associatedPDFName;
	}

	public void setAssociatedPDFName(String associatedPDFName) {
		this.associatedPDFName = associatedPDFName;
	}
	
}


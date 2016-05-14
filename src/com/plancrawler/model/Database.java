package com.plancrawler.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.plancrawler.model.utilities.MyPoint;

public class Database {
	private static Database uniqueInstance = new Database();
	
	private String associatedPDFName = null;
	private List<Item> items; 
	private List<Measurement> measurements;
	private CrateDatabase warehouse;
	
	private Database() {
		items = new LinkedList<Item>();
		measurements = new LinkedList<Measurement>();
		warehouse = CrateDatabase.getInstance();
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
		warehouse.removeItemInstances(item);
		return items.remove(item);
	}
	
	public void addTokenToItem(Location loc, int itemIndex){
		Item i = getItem(itemIndex);
		if (i == null){
			System.err.println("Unknown row ("+itemIndex+") requested");
		} else
			i.addToken(loc, TokenLocations.ON_PAGE);
	}
	
	public boolean remTokenFromItem(Location loc, int itemIndex){
		Item i = getItem(itemIndex);
		if (i == null)
			return false;
		else
			return i.remVisibleToken(loc);
	}
	
	public Item getItem(int index){
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
		oos.writeObject(warehouse);
		
		oos.close();
	}
	
	public void loadFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		try {
			Item[] itemArray = (Item[]) ois.readObject();
			Measurement[] mArray = (Measurement[]) ois.readObject();
			associatedPDFName = (String) ois.readObject();
			this.warehouse = (CrateDatabase) ois.readObject();
			
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

	public void remMeasurement(MyPoint point, int currentPage) {
		List<Measurement> remList = new ArrayList<Measurement>();
		
		for (Measurement m : measurements){
			if (m.isAtLocation(point, currentPage))
				remList.add(m);
		}
		for (Measurement m : remList)
			measurements.remove(m);
	}

	public void modifyEntry(int index, Item item) {
		Item entry = getItem(index);
		if (entry == null)
			addItem(item);
		else {
			entry.setName(item.getName());
			entry.setDescription(item.getDescription());
			entry.setCategory(item.getCategory());
			entry.setColor(item.getColor());
		}
	}

	public List<Crate> getCrates() {
		return warehouse.getCrates();
	}

	public List<Item> getItemsInCrate(int crateIndex) {
		return warehouse.getItemsInCrate(crateIndex);
	}

	public void addCrate(Crate crate) {
		warehouse.addCrate(crate);
	}
	
	public Crate getCrate(int index){
		return warehouse.getCrate(index);
	}

	public void modifyCrateEntry(int index, Crate crate) {
		Crate entry = getCrate(index);
		if (entry == null)
			addCrate(entry);
		else {
			entry.setName(crate.getName());
			entry.setDescription(crate.getDescription());
			entry.setCategory(crate.getCategory());
			entry.setColor(crate.getColor());
		}
	}

	public boolean remCrate(int row) {
		return warehouse.remCrate(getCrate(row));
	}

	public void addItemToCrate(Location loc, int itemIndex, int crateIndex) {
		Item item = getItem(itemIndex);
		warehouse.addItemToCrate(loc, item, crateIndex);
	}

	public void addTokenToCrate(Location loc, int crateIndex) {
		warehouse.addTokenToCrate(loc, crateIndex);
	}

	public void remItemFromCrate(Location loc, int itemIndex, int crateIndex) {
		Item item = getItem(itemIndex);
		warehouse.remItemFromCrate(loc, item, crateIndex);
	}

	public void remTokenFromCrate(Location loc, int crateIndex) {
		warehouse.remTokenFromCrate(loc, crateIndex);
	}
	
}


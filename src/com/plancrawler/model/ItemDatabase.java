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

public class ItemDatabase {

	private List<Item> items; 
	private static ItemDatabase uniqueInstance = new ItemDatabase();
	
	private ItemDatabase() {
		items = new LinkedList<Item>();
	}
	
	public static ItemDatabase getInstance() {
		return uniqueInstance;
	}
	
	public void addItem(Item item) {
		items.add(item);
		Collections.sort(items);
	}
	
	public boolean remItem(Item item) {
		return items.remove(item);
	}
	
	public void addTokenToItem(Location loc, int activeItemRow){
		Item i = getItem(activeItemRow);
		if (i == null){
			System.err.println("Unknown row ("+activeItemRow+") requested");
		} else
			i.addToken(loc);
	}
	
	public boolean remTokenFromItem(Location loc, Item item){
		Item i = getItem(item);
		if (i == null)
			return false;
		else
			return i.remToken(loc);
	}
	
	private Item getItem(Item item){
		Item returnItem = null;
		for (Item i : items)
			if (i.equals(item))
				returnItem = i;
		return returnItem;
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
		oos.writeObject(itemArray);
		oos.close();
	}
	
	public void loadFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		try {
			Item[] itemArray = (Item[]) ois.readObject();
			
			items.clear();
			items.addAll(Arrays.asList(itemArray));
			
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
}


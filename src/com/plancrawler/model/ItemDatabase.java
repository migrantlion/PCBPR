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
	}
	
	public boolean remItem(Item item) {
		return items.remove(item);
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
}


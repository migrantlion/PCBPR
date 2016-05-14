package com.plancrawler.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CrateDatabase implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Crate> crates;
	private static CrateDatabase uniqueInstance = new CrateDatabase();

	private CrateDatabase() {
		crates = new LinkedList<Crate>();
	}

	public static CrateDatabase getInstance() {
		return uniqueInstance;
	}

	public void addCrate(Crate crate) {
		crates.add(crate);
		Collections.sort(crates);
	}

	public boolean remCrate(Crate crate) {
		crate.clearCrate();
		return crates.remove(crate);
	}

	public List<Item> getItemsInCrate(Crate crate) {
		return Collections.unmodifiableList(crate.getItemsOnlyInCrate());
	}

	public List<Crate> getCrates() {
		return Collections.unmodifiableList(crates);
	}

	public void saveToFile(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		Crate[] crateArray = crates.toArray(new Crate[crates.size()]);
		oos.writeObject(crateArray);
		oos.close();
	}

	public void loadFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);

		try {
			Crate[] crateArray = (Crate[]) ois.readObject();

			crates.clear();
			crates.addAll(Arrays.asList(crateArray));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ois.close();
	}

	public List<Item> getItemsInCrate(int crateIndex) {
		List<Item> returnList = new ArrayList<Item>();
		if (crateIndex < 0)
			return Collections.unmodifiableList(returnList);
		else
			return getItemsInCrate(crates.get(crateIndex));
	}
	
	public Crate getCrate(int index){
		if (index < 0 || index > crates.size())
			return null;
		else
			return crates.get(index);
	}
	

	public void addItemToCrate(Location loc, Item item, int crateIndex) {
		Crate crate = getCrate(crateIndex);
		if (crate == null)
			return;
		
		crate.addItemToCrateAtLocation(item, loc);;
	}

	public void addTokenToCrate(Location loc, int crateIndex) {
		Crate crate = getCrate(crateIndex);
		if (crate == null)
			return;
		
		crate.addToken(loc);
	}

	public void remItemFromCrate(Location loc, Item item, int crateIndex) {
		Crate crate = getCrate(crateIndex);
		crate.remItemInCrateAtLocation(loc, item);
	}

	public void remTokenFromCrate(Location loc, int crateIndex) {
		Crate crate = getCrate(crateIndex);
		crate.remToken(loc); 
	}

	public void removeItemInstances(Item item) {
		for (Crate crate : crates)
			crate.removeItemInstancesFromCrate(item);
	}
}

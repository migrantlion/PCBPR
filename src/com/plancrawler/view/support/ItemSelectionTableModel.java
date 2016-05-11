package com.plancrawler.view.support;

import java.awt.Color;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.plancrawler.model.Item;

public class ItemSelectionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private List<Item> db;
	private String[] colNames = {"Color","Name","Description","Quantity"};

	public void setData(List<Item> db) {
		this.db = db;
	}

	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}


	@Override
	public int getRowCount() {
		return db.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Item item = db.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return " ";
		case 1:
			return item.getName();
		case 2:
			return item.getDescription();
		case 3:
			return item.getTokenCount();
		}
		return null;
	}

	public Color getRowColor(int row) {
		Item item = db.get(row);
		return item.getColor();
	}

}
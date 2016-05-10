package com.plancrawler.view.support;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.plancrawler.model.Item;
import com.plancrawler.model.utilities.ColorUtility;

public class ItemTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private List<Item> db;
	private String[] colNames = {"Name","Quantity","Description","Category","Color"};

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
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Item item = db.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return item.getName();
		case 1:
			return item.getTokenCount();
		case 2:
			return item.getDescription();
		case 3:
			return item.getCategory();
		case 4:
			return ColorUtility.colorName(item.getColor());
		}
		return null;
	}

}

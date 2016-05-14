package com.plancrawler.view.support;

import java.awt.Color;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.plancrawler.model.Crate;

public class CrateSelectionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private List<Crate> db;
	private String[] colNames = {"Color","Name","Description","Quantity"};

	public void setData(List<Crate> db) {
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
		Crate crate = db.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return " ";
		case 1:
			return crate.getName();
		case 2:
			return crate.getDescription();
		case 3:
			return crate.getItemsInCrateCount();
		}
		return null;
	}

	public Color getRowColor(int row) {
		Crate crate = db.get(row);
		return crate.getColor();
	}


}

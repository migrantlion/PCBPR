package com.plancrawler.view;

import java.util.List;

import javax.swing.JTable;

import com.plancrawler.model.Item;
import com.plancrawler.view.support.ItemSelectionTableModel;
import com.plancrawler.view.support.ItemSelectionTableRenderer;

public class ItemSelectionPanel extends SelectionPanel {

	private static final long serialVersionUID = 1L;

	public ItemSelectionPanel() {
		super();
	}
	
	protected void setTableModel(){
		title = "Item Selection";
		tableModel = new ItemSelectionTableModel();
		this.table = new JTable(tableModel);
		ItemSelectionTableRenderer renderer = new ItemSelectionTableRenderer();
		table.setDefaultRenderer(table.getColumnClass(0), renderer);
	}

	public void setData(List<Item> db) {
		((ItemSelectionTableModel) tableModel).setData(db);
	}

}

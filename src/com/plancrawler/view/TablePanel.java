package com.plancrawler.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;

import com.plancrawler.model.Item;
import com.plancrawler.view.support.ItemTableModel;



public class TablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;
	private ItemTableModel tableModel = new ItemTableModel();
	
	public TablePanel() {
		table = new JTable(tableModel);
		
		setLayout(new BorderLayout());
		Border innerBorder = BorderFactory.createTitledBorder("Item View");
		Border outerBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	public void setData(List<Item> db) {
		tableModel.setData(db);
	}
	
	public void refresh() {
		tableModel.fireTableDataChanged();
	}
}
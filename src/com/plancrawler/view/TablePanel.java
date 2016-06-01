package com.plancrawler.view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;

import com.plancrawler.model.Item;
import com.plancrawler.view.support.ItemTableModel;
import com.plancrawler.view.support.ItemTableRenderer;



public class TablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;
	private ItemTableModel tableModel = new ItemTableModel();
	private int selectedRow = -1;
	private String title;

	public TablePanel(String title) {
		this.title = title;
		setup();
	}
	
	public TablePanel(){
		this("Item View");
	}
	
	private void setup(){
		table = new JTable(tableModel);
		ItemTableRenderer renderer = new ItemTableRenderer();
		table.setDefaultRenderer(table.getColumnClass(0), renderer);
				
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				if (selectedRow == row) {
					selectedRow = -1;
					row = -1;
					table.clearSelection();
				} else {
					selectedRow = row;
					table.getSelectionModel().setSelectionInterval(row, row);
				}
			}
		});
		
		setLayout(new BorderLayout());
		Border innerBorder = BorderFactory.createTitledBorder(title);
		Border outerBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	public void setTitle(String title){
		this.title = title;
		Border innerBorder = BorderFactory.createTitledBorder(title);
		Border outerBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
	}
	
	public void setData(List<Item> db) {
		tableModel.setData(db);
	}
	
	public void refresh() {
		tableModel.fireTableDataChanged();
		selectedRow = -1;
	}
}
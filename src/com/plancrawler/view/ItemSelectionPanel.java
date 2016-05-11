package com.plancrawler.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;

import com.plancrawler.model.Item;
import com.plancrawler.view.support.ItemSelectionEvent;
import com.plancrawler.view.support.ItemSelectionListener;
import com.plancrawler.view.support.ItemSelectionTableModel;
import com.plancrawler.view.support.ItemSelectionTableRenderer;

public class ItemSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table; 
	private ItemSelectionTableModel tableModel = new ItemSelectionTableModel();
	private JPopupMenu popup = new JPopupMenu();
	private List<ItemSelectionListener> listeners = new ArrayList<ItemSelectionListener>();
	
	public ItemSelectionPanel(){
		this.table = new JTable(tableModel);
		ItemSelectionTableRenderer renderer = new ItemSelectionTableRenderer();
		table.setDefaultRenderer(table.getColumnClass(0), renderer);
		setupPanel();
		addButtons();
	}
	
	private void setupPanel(){
		Dimension dim = getPreferredSize();
		dim.width = 250;
		setPreferredSize(dim);

		Border innerBorder = BorderFactory.createTitledBorder("Item Select");
		Border outerBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		setLayout(new BorderLayout());
		
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				table.getSelectionModel().setSelectionInterval(row, row);
				if (e.getButton() == MouseEvent.BUTTON3){
					popup.show(table, e.getX(), e.getY());
				}
				alertListeners(new ItemSelectionEvent(ItemSelectionPanel.this, row, false, false));
			}
			
		});
		
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void addButtons(){
		JMenuItem clearSelectItem = new JMenuItem("de-select");
		clearSelectItem.addActionListener((e)->{
			table.clearSelection();
			alertListeners(new ItemSelectionEvent(ItemSelectionPanel.this, -1, false, false));
		});
		popup.add(clearSelectItem);
		
		JMenuItem remItem = new JMenuItem("delte row");
		remItem.addActionListener((e)->{
			int row = table.getSelectedRow();
			alertListeners(new ItemSelectionEvent(ItemSelectionPanel.this, row, false, true));
		});
		popup.add(remItem);
		
		JMenuItem changeItem = new JMenuItem("modify row");
		changeItem.addActionListener((e)->{
			int row = table.getSelectedRow();
			alertListeners(new ItemSelectionEvent(ItemSelectionPanel.this, row, true, false));
		});
		popup.add(changeItem);
	}
		
	private void alertListeners(ItemSelectionEvent itemSelectionEvent) {
		for (ItemSelectionListener l : listeners)
			l.itemSelectionProcessed(itemSelectionEvent);
	}
	
	public void addItemSelectionListener(ItemSelectionListener l){
		listeners.add(l);
	}

	public boolean remItemSelectionListener(ItemSelectionListener l){
		return listeners.remove(l);
	}

	public void setData(List<Item> db) {
		tableModel.setData(db);
	}
	
	public void refresh() {
		tableModel.fireTableDataChanged();
	}
}


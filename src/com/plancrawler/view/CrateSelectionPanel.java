package com.plancrawler.view;

import java.util.List;

import javax.swing.JTable;

import com.plancrawler.model.Crate;
import com.plancrawler.view.support.CrateSelectionTableModel;
import com.plancrawler.view.support.CrateSelectionTableRenderer;

public class CrateSelectionPanel extends SelectionPanel {

	private static final long serialVersionUID = 1L;

	public CrateSelectionPanel() {
		super();
	}

	@Override
	protected void setTableModel() {
		title = "Crate Selection";
		tableModel = new CrateSelectionTableModel();
		this.table = new JTable(tableModel);
		CrateSelectionTableRenderer renderer = new CrateSelectionTableRenderer();
		table.setDefaultRenderer(table.getColumnClass(0), renderer);
	}

	public void setData(List<Crate> db) {
		((CrateSelectionTableModel) tableModel).setData(db);
	}

}

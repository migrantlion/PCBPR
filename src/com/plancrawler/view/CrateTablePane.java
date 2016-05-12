package com.plancrawler.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.plancrawler.model.Crate;
import com.plancrawler.view.support.TableItemSelectionEvent;
import com.plancrawler.view.support.TableItemSelectionListener;

public class CrateTablePane extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;
	private CrateTableModel tableModel = new CrateTableModel();
	private List<TableItemSelectionListener> listeners = new ArrayList<TableItemSelectionListener>();

	public CrateTablePane() {
		table = new JTable(tableModel);
		CrateTableRenderer renderer = new CrateTableRenderer();
		table.setDefaultRenderer(table.getColumnClass(0), renderer);

		setLayout(new BorderLayout());
		Border innerBorder = BorderFactory.createTitledBorder("Package List");
		Border outerBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		add(new JScrollPane(table), BorderLayout.CENTER);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				table.getSelectionModel().setSelectionInterval(row, row);
				if (e.getButton() == MouseEvent.BUTTON3) {
					table.clearSelection();
					row = -1;
				}
				alertListeners(new TableItemSelectionEvent(CrateTablePane.this, row, false, false));
			}
		});
	}

	public void setData(List<Crate> db) {
		tableModel.setData(db);
	}

	public void refresh() {
		tableModel.fireTableDataChanged();
	}

	private void alertListeners(TableItemSelectionEvent e) {
		for (TableItemSelectionListener t : listeners)
			t.itemSelectionProcessed(e);
	}

	public void addTableListener(TableItemSelectionListener t) {
		listeners.add(t);
	}

	public boolean remTableListener(TableItemSelectionListener t) {
		return listeners.remove(t);
	}

	private class CrateTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private List<Crate> db;
		private String[] colNames = { "Color", "Crate Name", "Crate Desc", "Num Items in Crate", "Crate category",
				"Contents on Pages", "Placed on Pages" };

		public void setData(List<Crate> db) {
			this.db = db;
		}

		@Override
		public String getColumnName(int column) {
			return colNames[column];
		}

		@Override
		public int getColumnCount() {
			return 7;
		}

		@Override
		public int getRowCount() {
			return db.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Crate crate = db.get(row);
			List<Integer> pages;
			String pageString = "";
			switch (col) {
			case 0:
				return " ";
			case 1:
				return crate.getName();
			case 2:
				return crate.getDescription();
			case 3:
				return crate.getItemCount();
			case 4:
				return crate.getCategory();
			case 5:
				pages = crate.contentsOnWhatPage();
				for (int p : pages)
					pageString += Integer.toString(p + 1) + ", ";
				if (pageString.endsWith(", "))
					pageString = pageString.substring(0, pageString.length() - 2);
				return pageString;
			case 6:
				pages = crate.tokensOnWhatPage();
				for (int p : pages)
					pageString += Integer.toString(p + 1) + ", ";
				if (pageString.endsWith(", "))
					pageString = pageString.substring(0, pageString.length() - 2);
				return pageString;
			}
			return null;
		}

		public Color getRowColor(int row) {
			return db.get(row).getColor();
		}

	}

	private class CrateTableRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int col) {

			// Cells are by default rendered as a JLabel.
			JLabel jl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

			// Get the color for the current row.
			CrateTableModel tableModel = (CrateTableModel) table.getModel();

			Color color = tableModel.getRowColor(row);
			if (col == 0) {
				jl.setBackground(color);
			} else {
				jl.setBackground(Color.white);
			}
			if (col == 3) {
				// quantity value
				jl.setHorizontalAlignment(SwingConstants.CENTER);
			} else
				jl.setHorizontalAlignment(SwingConstants.LEFT);

			if (table.isRowSelected(row)) {
				jl.setBorder(BorderFactory.createLineBorder(Color.magenta, 2));
			}

			// Return the JLabel which renders the cell.
			return jl;

		}
	}
}

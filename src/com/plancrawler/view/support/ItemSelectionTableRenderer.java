package com.plancrawler.view.support;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ItemSelectionTableRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int col) {

		// Cells are by default rendered as a JLabel.
		JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

		// Get the color for the current row.
		ItemSelectionTableModel tableModel = (ItemSelectionTableModel) table.getModel();
		Color color = tableModel.getRowColor(row);
		if (col == 0) {
			l.setBackground(color);
		} else {
			l.setBackground(Color.white);
		}
		
		if (table.isRowSelected(row)){
			l.setBorder(BorderFactory.createLineBorder(Color.green, 2));
		}

		// Return the JLabel which renders the cell.
		return l;

	}

}

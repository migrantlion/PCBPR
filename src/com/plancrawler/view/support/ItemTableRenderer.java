package com.plancrawler.view.support;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ItemTableRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int col) {

		// Cells are by default rendered as a JLabel.
		JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

		// Get the color for the current row.
		ItemTableModel tableModel = (ItemTableModel) table.getModel();
		Color color = tableModel.getRowColor(row);
		if (col == 0) {
			lbl.setBackground(color);
		} else {
			lbl.setBackground(Color.white);
		}
		if (col == 4) {
			// quantity
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
		} else
			lbl.setHorizontalAlignment(SwingConstants.LEFT);

		if (table.isRowSelected(row)) {
			lbl.setBorder(BorderFactory.createLineBorder(Color.magenta, 2));
		}

		// Return the JLabel which renders the cell.
		return lbl;

	}

}
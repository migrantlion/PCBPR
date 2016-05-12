package com.plancrawler.view.dialogs;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.plancrawler.model.Item;
import com.plancrawler.model.utilities.ColorUtility;
import com.plancrawler.view.support.ItemFormEvent;

public class ItemModifyDialog {

	public static ItemFormEvent modifyItem(Item item, JComponent parent) {
		ItemFormEvent ife;

		// setup labels
		JLabel headerLabel = new JLabel("Modify properties of Item Entry");
		
		JLabel icolorLabel = new JLabel("old color: ");
		JLabel oldcolorLabel = new JLabel("     ");
		oldcolorLabel.setOpaque(true);
		oldcolorLabel.setBackground(item.getColor());
		oldcolorLabel.setForeground(item.getColor());

		JTextField newNameField = new JTextField(item.getName(), 10);
		JTextField newDescField = new JTextField(item.getDescription(), 10);
		JTextField newCatField = new JTextField(item.getCategory(), 10);
		JButton colorButt = new JButton("Pick New Color");
		colorButt.setBackground(item.getColor());
		colorButt.setForeground(ColorUtility.invert(item.getColor()));
		colorButt.addActionListener((ae) -> {
			Color color = JColorChooser.showDialog(colorButt, "Pick a Color", item.getColor());
			if (color != null) {
				colorButt.setBackground(color);
				colorButt.setForeground(ColorUtility.invert(color));
			}
		});

		// layout the dialog
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;
		Insets rightPad = new Insets(0, 0, 0, 5);
		Insets noPad = new Insets(0, 0, 0, 0);

		// first row
		gc.gridy = 0;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = rightPad;
		panel.add(new JLabel("new name: "), gc);
		gc.gridx++;
		panel.add(newNameField, gc);

		// next Row
		gc.gridy++;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = rightPad;
		panel.add(new JLabel("new desc: "), gc);
		gc.gridx++;
		panel.add(newDescField, gc);

		// next Row
		gc.gridy++;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = rightPad;
		panel.add(new JLabel("new category: "), gc);
		gc.gridx++;
		panel.add(newCatField, gc);

		// next Row
		gc.gridy++;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = rightPad;
		panel.add(icolorLabel, gc);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = noPad;
		panel.add(oldcolorLabel, gc);
		gc.gridx = 2;
		panel.add(colorButt, gc);

		// show dialog
		JLabel instruction = new JLabel("Enter new values for Fields you wish to change");
		JComponent[] inputs = new JComponent[] { headerLabel, new JLabel(" "), instruction, panel, new JLabel(" ") };
		int result = JOptionPane.showConfirmDialog(parent, inputs, "Modify Item Dialog", JOptionPane.OK_CANCEL_OPTION);

		// populate event and pass back
		if (result == JOptionPane.OK_OPTION)
			ife = new ItemFormEvent(result, newNameField.getText(), newDescField.getText(), newCatField.getText(),
					colorButt.getBackground());
		else
			ife = new ItemFormEvent(result, item.getName(), item.getDescription(), item.getCategory(), item.getColor());

		return ife;
	}
}

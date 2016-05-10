package com.plancrawler.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.plancrawler.model.utilities.ColorUtility;
import com.plancrawler.view.support.ItemFormEvent;
import com.plancrawler.view.support.ItemFormListener;



public class ItemFormPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ArrayList<ItemFormListener> listeners = new ArrayList<ItemFormListener>();

	private JLabel nameLabel, descLabel, catLabel;
	private JTextField nameField, descField;
	private JComboBox<String> catComboBox;
	private DefaultComboBoxModel<String> catModel = new DefaultComboBoxModel<String>();
	private JButton colorButt;
	private JButton addButt;
	private Color nocolor;

	public ItemFormPanel() {
		setupPanel();
		attachComponents();
	}

	private void setupPanel() {
		Dimension dim = getPreferredSize();
		dim.width = 250;
		setPreferredSize(dim);

		Border innerBorder = BorderFactory.createTitledBorder("Add Item");
		Border outerBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		setLayout(new GridBagLayout());
	}

	private void attachComponents() {
		nameLabel = new JLabel("item name:");
		descLabel = new JLabel("description:");
		catLabel = new JLabel("category:");
		nameField = new JTextField(10);
		descField = new JTextField(10);

		catModel.addElement("none");
		catModel.addElement("toilet");
		catModel.addElement("fire");
		catComboBox = new JComboBox<String>(catModel);
		catComboBox.setBorder(BorderFactory.createEtchedBorder());
		catComboBox.setEditable(true);
		catComboBox.setSelectedIndex(0);

		colorButt = new JButton("Color");
		nocolor = colorButt.getBackground();
		colorButt.addActionListener((e) -> {
			Color color = JColorChooser.showDialog(colorButt, "Pick a Color", nocolor);
			if (color != null)
				setColorButtColor(color);
		});

		addButt = new JButton("ADD");
		addButt.addActionListener(new ItemFormButtListener());

		nameLabel.setLabelFor(nameField);
		descLabel.setLabelFor(descField);
		catLabel.setLabelFor(catComboBox);

		nameLabel.setDisplayedMnemonic(KeyEvent.VK_N);
		descLabel.setDisplayedMnemonic(KeyEvent.VK_D);
		catLabel.setDisplayedMnemonic(KeyEvent.VK_C);

		addButt.setMnemonic(KeyEvent.VK_A);
		colorButt.setMnemonic(KeyEvent.VK_O);

		layoutComponents();
	}

	private void layoutComponents() {
		GridBagConstraints gc = new GridBagConstraints();

		Insets rightPad = new Insets(0, 0, 0, 5);
		Insets noPad = new Insets(0, 0, 0, 0);

		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;

		// first row
		gc.gridy = 0;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = rightPad;
		add(nameLabel, gc);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = noPad;
		add(nameField, gc);

		// next Row
		gc.gridy++;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = rightPad;
		add(descLabel, gc);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = noPad;
		add(descField, gc);

		// next Row
		gc.gridy++;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = rightPad;
		add(catLabel, gc);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = noPad;
		add(catComboBox, gc);

		// last Row
		gc.gridy++;
		gc.anchor = GridBagConstraints.NORTH;
		gc.weighty = 10.0;
		gc.gridx = 0;
		add(colorButt, gc);
		gc.gridx = 1;
		add(addButt, gc);
	}

	public void clearForm() {
		nameField.setText("");
		descField.setText("");
		setColorButtColor(nocolor);
	}

	private void setColorButtColor(Color color) {
		colorButt.setBackground(color);
		colorButt.setForeground(ColorUtility.invert(color));
	}

	private void alertListeners(ItemFormEvent e) {
		for (ItemFormListener i : listeners)
			i.itemFormSubmitted(e);
	}

	public void addFormListener(ItemFormListener i) {
		listeners.add(i);
	}

	public boolean remFormListener(ItemFormListener i) {
		return listeners.remove(i);
	}

	private class ItemFormButtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addButt) {
				ItemFormEvent ie = new ItemFormEvent(ItemFormPanel.this);
				ie.setItemName(nameField.getText());
				ie.setItemDesc(descField.getText());
				ie.setItemCat((String) catComboBox.getSelectedItem());
				if (colorButt.getBackground() == nocolor)
					ie.setItemColor(ColorUtility.randColor());
				else
					ie.setItemColor(colorButt.getBackground());
				alertListeners(ie);

				String catSelected = (String) catComboBox.getSelectedItem();
				if (!catSelected.equals((String) catModel.getElementAt(catComboBox.getSelectedIndex()))) {
					catModel.addElement(catSelected);
					catComboBox.setModel(catModel);
				}
				clearForm();
			}
		}
	}

}

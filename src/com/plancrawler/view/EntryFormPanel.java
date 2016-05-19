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
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.plancrawler.model.utilities.ColorUtility;
import com.plancrawler.view.support.EntryFormEvent;
import com.plancrawler.view.support.EntryFormListener;

public class EntryFormPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ArrayList<EntryFormListener> listeners = new ArrayList<EntryFormListener>();

	private JLabel nameLabel, descLabel, catLabel, colorLabel;
	private JTextField nameField, descField;
	private JComboBox<String> catComboBox;
	private DefaultComboBoxModel<String> catModel = new DefaultComboBoxModel<String>();
	private JButton colorButt;
	private JButton addItemButt, addCrateButt;
	private Color nocolor;
	private Border origBorder;

	public EntryFormPanel() {
		setupPanel();
		attachComponents();
	}

	private void setupPanel() {
		Dimension dim = getPreferredSize();
		dim.width = 250;
		setPreferredSize(dim);

		Border innerBorder = BorderFactory.createTitledBorder("Add Entry");
		Border outerBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		setLayout(new GridBagLayout());
	}

	private void attachComponents() {
		nameLabel = new JLabel("entry name:");
		descLabel = new JLabel("description:");
		catLabel = new JLabel("category:");
		nameField = new JTextField(10);
		origBorder = nameField.getBorder();
		descField = new JTextField(10);
		colorLabel = new JLabel("color");
		colorLabel.setOpaque(true);
		setColorLabelColor(ColorUtility.randColor());

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
			if (color != null) {
				setColorLabelColor(color);
			}
		});
		Dimension dim = colorButt.getPreferredSize();
		colorLabel.setPreferredSize(dim);

		addItemButt = new JButton("ADD Item");
		addItemButt.addActionListener(new ItemFormButtListener());

		addCrateButt = new JButton("ADD Crate");
		addCrateButt.addActionListener(new ItemFormButtListener());

		nameLabel.setLabelFor(nameField);
		descLabel.setLabelFor(descField);
		catLabel.setLabelFor(catComboBox);

		nameLabel.setDisplayedMnemonic(KeyEvent.VK_N);
		descLabel.setDisplayedMnemonic(KeyEvent.VK_D);

		addItemButt.setMnemonic(KeyEvent.VK_ENTER);
		addItemButt.setToolTipText("Add entry as item.  alt-RET quick-key");
		addCrateButt.setMnemonic(KeyEvent.VK_C);
		addCrateButt.setToolTipText("Add entry as crate.  alt-C quick-key");
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

		// next Row
		gc.gridy++;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.CENTER;
		gc.insets = rightPad;
		add(colorLabel, gc);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = noPad;
		add(colorButt, gc);

		// blank
		gc.gridy++;
		gc.gridx = 0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("  "), gc);
		gc.gridx = 1;
		add(new JLabel("  "), gc);
		
		// separator
		gc.gridy++;
		gc.gridx = 0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		add(new JSeparator(JSeparator.HORIZONTAL), gc);

		// last Row
		gc.gridy++;
		gc.anchor = GridBagConstraints.NORTH;
		gc.fill = GridBagConstraints.NONE;
		gc.gridwidth = GridBagConstraints.RELATIVE;
		gc.weighty = 10.0;
		gc.gridx = 0;
		add(addCrateButt, gc);
		gc.gridx = 1;
		add(addItemButt, gc);
	}

	public void clearForm() {
		nameField.setText("");
		nameField.setBorder(origBorder);
		descField.setText("");
		setColorLabelColor(ColorUtility.randColor());
	}

	private void setColorLabelColor(Color color) {
		colorLabel.setBackground(color);
		colorLabel.setForeground(color);
	}

	private void alertListeners(EntryFormEvent e) {
		for (EntryFormListener i : listeners)
			i.itemFormSubmitted(e);
	}

	public void addFormListener(EntryFormListener i) {
		listeners.add(i);
	}

	public boolean remFormListener(EntryFormListener i) {
		return listeners.remove(i);
	}

	private class ItemFormButtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (nameField.getText().isEmpty()) {
				nameField.setBorder(BorderFactory.createLineBorder(Color.red, 1));
				return;
			}

			EntryFormEvent ie = new EntryFormEvent(EntryFormPanel.this);
			ie.setEntryName(nameField.getText());
			ie.setEntryDesc(descField.getText());
			ie.setEntryCat((String) catComboBox.getSelectedItem());
			if (colorLabel.getBackground() == nocolor)
				ie.setEntryColor(ColorUtility.randColor());
			else
				ie.setEntryColor(colorLabel.getBackground());

			if (e.getSource() == addItemButt) {
				ie.setAddItem(true);
				alertListeners(ie);
			} else if (e.getSource() == addCrateButt) {
				ie.setAddCrate(true);
				alertListeners(ie);
			}

			String catSelected = (String) catComboBox.getSelectedItem();
			if (!catSelected.equals((String) catModel.getElementAt(catComboBox.getSelectedIndex()))) {
				catModel.addElement(catSelected);
				catComboBox.setModel(catModel);
			}
			clearForm();
		}
	}
}

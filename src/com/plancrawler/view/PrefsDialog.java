package com.plancrawler.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.plancrawler.view.support.PrefsListener;

public class PrefsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField docPathField;
	private JTextField tempPathField;
	private String lastDoc;
	private String lastTemp;
	JButton okButt, cancelButt;
	JButton chooseDocButt;
	JButton chooseTempButt;
	private List<PrefsListener> listeners = new ArrayList<PrefsListener>();

	JFileChooser chooser = new JFileChooser();

	public PrefsDialog(JFrame parent) {
		super(parent, "Preferences", false);
		setSize(600, 400);
		setLocationRelativeTo(parent);
		setupDialog();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	private void setupDialog() {

		docPathField = new JTextField();
		tempPathField = new JTextField();
		
		okButt = new JButton("OK");
		okButt.addActionListener((e) -> {
			String docPath = docPathField.getText();
			String tempPath = tempPathField.getText();
			if (!docPath.endsWith("\\"))
				docPath += "\\";
			if (!tempPath.endsWith("\\"))
				tempPath += "\\";
			
			setDefaults(docPath, tempPath);
			alertListeners(docPath, tempPath);
			setVisible(false);
		});

		cancelButt = new JButton("Cancel");
		cancelButt.addActionListener((e) -> {
			docPathField.setText(lastDoc);
			tempPathField.setText(lastTemp);
			setVisible(false);
		});

		chooseDocButt = new JButton("Choose");
		chooseDocButt.addActionListener((e)->{
			chooser.setCurrentDirectory(new File(lastDoc));
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				docPathField.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});

		chooseTempButt = new JButton("Choose");
		chooseTempButt.addActionListener((e)->{
			chooser.setCurrentDirectory(new File(lastTemp));
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				tempPathField.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});

		layoutDialog();
	}

	private void layoutDialog() {
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		Insets rightPad = new Insets(0, 0, 0, 5);
		Insets noPad = new Insets(0, 0, 0, 0);

		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;

		// first line
		gc.gridy = 0;
		gc.gridx = 0;
		add(new JLabel("Preferences:"), gc);

		/// next line
		gc.gridy++;
		gc.gridx = 0;
		gc.weighty = 0.1;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("PDF file default path: "), gc);
		gc.gridx++;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(docPathField, gc);
		gc.gridx++;
		add(chooseDocButt, gc);

		// next line
		gc.gridy++;
		gc.gridx = 0;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("PlanCrawler temp file folder: "), gc);
		gc.gridx++;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(tempPathField, gc);
		gc.gridx++;
		add(chooseTempButt, gc);

		// next line
		gc.gridy++;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = rightPad;
		add(okButt, gc);
		gc.gridx++;
		gc.insets = noPad;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(cancelButt, gc);

	}

	
	public void setDefaults(String docPath, String tempPath) {
		lastDoc = docPath;
		docPathField.setText(docPath);
		lastTemp = tempPath;
		tempPathField.setText(tempPath);
	}

	private void alertListeners(String docPath, String tempPath){
		for (PrefsListener pfl : listeners)
			pfl.preferencesSet(docPath, tempPath);
	}
	
	public void addPrefsListener(PrefsListener prefsListener) {
		listeners.add(prefsListener);
	}
	
	public boolean remPrefsListener(PrefsListener prefsListener){
		return listeners.remove(prefsListener);
	}
}

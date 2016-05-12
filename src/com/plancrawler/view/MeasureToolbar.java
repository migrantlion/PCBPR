package com.plancrawler.view;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import com.plancrawler.view.support.MeasListener;
import com.plancrawler.view.support.MeasureEvent;

public class MeasureToolbar extends JToolBar {
	
	private static final long serialVersionUID = 1L;
	private List<MeasListener> listeners = new ArrayList<MeasListener>();
	private JButton measButt;
	private JButton calibButt;
	private JComboBox<String> scaleComboBox;
	private DefaultComboBoxModel<String> scaleModel = new DefaultComboBoxModel<String>();
	private Color noColor;
	private Color selectedColor = Color.cyan;
	
	public MeasureToolbar(){
		setupButtons();
		setupCalibrationBox();
		addComponents();
	}
	
	public void setupButtons(){
		measButt = new JButton();
		measButt.setToolTipText("measure");
		measButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Meas16.gif"));
		measButt.addActionListener((e)->{
			measButt.setBackground(selectedColor);
			MeasureEvent me = new MeasureEvent(measButt);
			me.setMeasureRequest(true);
			alertListeners(me);
		});
		// assign background color
		noColor = measButt.getBackground();
		
		calibButt = new JButton();
		calibButt.setToolTipText("calibrate from measurement");
		calibButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Calibrate16.gif"));
		calibButt.addActionListener((e)->{
			calibButt.setBackground(selectedColor);
			MeasureEvent me = new MeasureEvent(calibButt);
			me.setCalibrationRequest(true);
			me.setCalibrationIndex(5);
			scaleComboBox.setSelectedIndex(5);
			alertListeners(me);
		});
	}
	
	public void setupCalibrationBox(){
		scaleModel.addElement("none");
		scaleModel.addElement("1/4\" = 1'");
		scaleModel.addElement("1/2\" = 1'");
		scaleModel.addElement("1/3\" = 1'");
		scaleModel.addElement("1/8\" = 1'");
		scaleModel.addElement("custom");

		scaleComboBox = new JComboBox<String>(scaleModel);
		scaleComboBox.setToolTipText("set drawing scale");
		scaleComboBox.setBorder(BorderFactory.createEtchedBorder());
		scaleComboBox.setEditable(false);
		scaleComboBox.setSelectedIndex(0);
		
		scaleComboBox.addActionListener((e)->{
			MeasureEvent me = new MeasureEvent(scaleComboBox);
			me.setCalibrationRequest(true);
			me.setCalibrationIndex(scaleComboBox.getSelectedIndex());
			alertListeners(me);
		});
	}
	
	public void resetButtons(){
		measButt.setBackground(noColor);
		calibButt.setBackground(noColor);
	}
	
	public void addComponents(){
		this.add(scaleComboBox);
		this.add(measButt);
		this.add(calibButt);
	}
	
	private Icon createIcon(String string) {
		URL url = getClass().getResource(string);
		if (url == null)
			System.err.println("could not load resource " + string);

		ImageIcon icon = new ImageIcon(url);
		return icon;
	}
	
	private void alertListeners(MeasureEvent e){
		for (MeasListener m : listeners)
			m.measureEventProcessed(e);
	}
	
	public void addMeasurementListener(MeasListener m){
		listeners.add(m);
	}
	
	public boolean remMeasurementListener(MeasListener m){
		return listeners.remove(m);
	}
}

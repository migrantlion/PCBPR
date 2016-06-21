package com.plancrawler.view.toolbars;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.plancrawler.controller.CalibrationDialog;
import com.plancrawler.controller.MeasurePainter;
import com.plancrawler.model.Measurement;
import com.plancrawler.model.utilities.MyPoint;
import com.plancrawler.view.PDFViewPane;

public class MeasureToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;
	private List<MeasListener> listeners = new ArrayList<MeasListener>();
	private JButton measButt;
	private JButton calibButt;
	private JToggleButton gridButt;
	private JComboBox<String> scaleComboBox;
	private DefaultComboBoxModel<String> scaleModel = new DefaultComboBoxModel<String>();
	private int dpi;

	private Color noColor;
	private Color selectedColor = Color.cyan;

	private PDFViewPane hostPane;
	private double activeScale = 1;
	private boolean isMeasuring = false;
	private boolean isCalibrating = false;

	public MeasureToolbar(PDFViewPane hostPane, int dpi) {
		MeasureMouseListener listener = new MeasureMouseListener();
		this.hostPane = hostPane;
		hostPane.addMouseListener(listener);
		hostPane.addMouseMotionListener(listener);

		setupButtons();
		setupCalibrationBox();
		addComponents();
	}
	
	public void setDPI(int value){
		dpi = value;
	}

	public void setupButtons() {
		measButt = new JButton();
		measButt.setToolTipText("measure");
		measButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Meas16.gif"));
		measButt.addActionListener((e) -> {
			if (measButt.getBackground() == noColor) {
				measButt.setBackground(selectedColor);
				isMeasuring = true;
				MeasureEvent measEvent = new MeasureEvent(MeasureToolbar.this, null, isMeasuring, false, false);
				alertListeners(measEvent);
			} else
				resetButtons();
		});
		// assign background color
		noColor = measButt.getBackground();

		calibButt = new JButton();
		calibButt.setToolTipText("calibrate from measurement");
		calibButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Calibrate16.gif"));
		calibButt.addActionListener((e) -> {
			if (calibButt.getBackground() == noColor) {
				calibButt.setBackground(selectedColor);
				setCalibration(5);
				scaleComboBox.setSelectedIndex(5);
			} else {
				setCalibration(0);
				scaleComboBox.setSelectedIndex(0);
				resetButtons();
			}
		});

		gridButt = new JToggleButton();
		gridButt.setToolTipText("force horiz/vert meas lines");
		gridButt.setIcon(createIcon("/com/plancrawler/view/iconImages/Grid16.gif"));
		gridButt.addActionListener((e) -> {
			if (gridButt.isSelected())
				gridButt.setBackground(selectedColor);
			else
				gridButt.setBackground(noColor);
		});
	}

	public void setupCalibrationBox() {
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

		scaleComboBox.addActionListener((e) -> {
			setCalibration(scaleComboBox.getSelectedIndex());
		});
	}

	public void resetButtons() {
		measButt.setBackground(noColor);
		calibButt.setBackground(noColor);
		isMeasuring = false;
		isCalibrating = false;
		MeasureEvent measEvent = new MeasureEvent(MeasureToolbar.this, null, isMeasuring, false, false);
		alertListeners(measEvent);
	}

	public void addComponents() {
		this.add(scaleComboBox);
		this.add(measButt);
		this.add(calibButt);
		this.add(gridButt);
	}

	private Icon createIcon(String string) {
		URL url = getClass().getResource(string);
		if (url == null)
			System.err.println("could not load resource " + string);

		ImageIcon icon = new ImageIcon(url);
		return icon;
	}

	private void alertListeners(MeasureEvent e) {
		for (MeasListener m : listeners)
			m.measureEventProcessed(e);
	}

	public void addMeasurementListener(MeasListener m) {
		listeners.add(m);
	}

	public boolean remMeasurementListener(MeasListener m) {
		return listeners.remove(m);
	}

	private void setCalibration(int calibrationIndex) {
		switch (calibrationIndex) {
		case 0:
			activeScale = 1;
			break;
		case 1: // 1/4" = 1'
			activeScale = Measurement.getCalibration(0.25, 1.0, dpi);
			break;
		case 2: // 1/2" = 1'
			activeScale = Measurement.getCalibration(0.50, 1.0, dpi);
			break;
		case 3: // 1/3" = 1'
			activeScale = Measurement.getCalibration(0.333, 1.0, dpi);
			break;
		case 4: // 1/8" = 1'
			activeScale = Measurement.getCalibration(0.025, 1.0, dpi);
			break;
		case 5: // this is custom, so we will change this with a calibration
				// measurement.
			activeScale = 1;
			isCalibrating = true;
			isMeasuring = true;
			MeasureEvent measEvent = new MeasureEvent(MeasureToolbar.this, null, isMeasuring, false, false);
			alertListeners(measEvent);
			break;
		}
	}

	class MeasureMouseListener extends MouseAdapter {
		MyPoint pt1 = null;
		MyPoint currPt;
		MeasurePainter mp = null;
		MeasureEvent measEvent;

		@Override
		public void mouseClicked(MouseEvent e) {
			if (isMeasuring) {
				if (pt1 == null) {
					pt1 = hostPane.getImageRelativePoint(new MyPoint(e.getX(), e.getY()));
					mp = new MeasurePainter(pt1, pt1, 0, activeScale);
					measEvent = new MeasureEvent(MeasureToolbar.this, mp, isMeasuring, true, false);
					alertListeners(measEvent);
				} else {
					MyPoint pt2 = hostPane.getImageRelativePoint(new MyPoint(e.getX(), e.getY()));
					pt2 = gridPoint(mp.getStartPt(), pt2);
					mp.setEndPt(pt2);
					pt1 = null;
					if (isCalibrating) {
						double dist = MyPoint.dist(mp.getStartPt(), mp.getEndPt());
						activeScale = CalibrationDialog.calibrate(hostPane, dist, mp.getMeasureString());
					} else {
						isMeasuring = false;
						measEvent = new MeasureEvent(MeasureToolbar.this, mp, isMeasuring, false, true);
						alertListeners(measEvent);
					}
					resetButtons();
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (isMeasuring) {
				currPt = hostPane.getImageRelativePoint(new MyPoint(e.getX(), e.getY()));
				if (pt1 != null) {
					currPt = gridPoint(mp.getStartPt(), currPt);
					mp.setEndPt(currPt);
					measEvent = new MeasureEvent(MeasureToolbar.this, mp, isMeasuring, true, false);
					alertListeners(measEvent);
				}
			}
		}
		
		private MyPoint gridPoint(MyPoint ref, MyPoint p){
			MyPoint point = p;
			if (gridButt.isSelected()) {
				double horizDist = (MyPoint.dist(ref,
						new MyPoint(p.getX(), ref.getY())));
				double vertDist = (MyPoint.dist(ref,
						new MyPoint(ref.getX(), p.getY())));
				if (horizDist >= vertDist)
					point = new MyPoint(p.getX(), ref.getY());
				else
					point = new MyPoint(ref.getX(), p.getY());
			}
			return point;
		}
	}
}

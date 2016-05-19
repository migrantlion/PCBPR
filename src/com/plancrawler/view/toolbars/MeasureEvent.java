package com.plancrawler.view.toolbars;

import java.util.EventObject;

import com.plancrawler.controller.MeasurePainter;

public class MeasureEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private MeasurePainter meas;
	private boolean drawRequest = false;
	private boolean addMeasurementRequest = false;
	private boolean measurementActive = false;
	
	public MeasureEvent(Object obj, MeasurePainter meas, boolean measurementActive, boolean drawRequest, boolean addMeasurementRequest) {
		super(obj);
		this.meas = meas;
		this.drawRequest = drawRequest;
		this.measurementActive = measurementActive;
		this.addMeasurementRequest = addMeasurementRequest;
	}

	public MeasurePainter getMeas() {
		return meas;
	}

	public boolean isDrawRequest() {
		return drawRequest;
	}

	public boolean isAddMeasurementRequest() {
		return addMeasurementRequest;
	}

	public boolean isMeasurementActive(){
		return measurementActive;
	}
}

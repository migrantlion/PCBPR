package com.plancrawler.view.support;

import java.util.EventObject;

public class MeasureEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private boolean measureRequest = false;
	private boolean calibrationRequest = false;
	private int calibrationIndex;
	
	public MeasureEvent(Object arg0) {
		super(arg0);
	}

	public boolean isMeasureRequest() {
		return measureRequest;
	}

	public void setMeasureRequest(boolean measureRequest) {
		this.measureRequest = measureRequest;
	}

	public boolean isCalibrationRequest() {
		return calibrationRequest;
	}

	public void setCalibrationRequest(boolean calibrationRequest) {
		this.calibrationRequest = calibrationRequest;
	}

	public int getCalibrationIndex() {
		return calibrationIndex;
	}

	public void setCalibrationIndex(int calibrationIndex) {
		this.calibrationIndex = calibrationIndex;
	}

}

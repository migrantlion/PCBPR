package com.plancrawler.model;

import java.io.Serializable;

import com.plancrawler.model.utilities.MyPoint;

public class Measurement implements Serializable {
	public static final long serialVersionUID = 1L;
	
	private MyPoint startPt, endPt;
	private double scale;
	
	public Measurement(MyPoint startPt, MyPoint endPt){
		this.startPt = startPt;
		this.endPt = endPt;
	}
	
}

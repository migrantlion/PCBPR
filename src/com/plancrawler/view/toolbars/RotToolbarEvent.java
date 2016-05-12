package com.plancrawler.view.toolbars;

import java.util.EventObject;

public class RotToolbarEvent extends EventObject {
	static final long serialVersionUID = 1L;
	double rotation;
	boolean rotAll;
	
	public RotToolbarEvent(Object source) {
		super(source);
	}
		
	public RotToolbarEvent(Object source, double rot, boolean rotAll) {
		this(source);
		this.rotation = rot;
		this.rotAll = rotAll;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public boolean isRotAll() {
		return rotAll;
	}

	public void setRotAll(boolean rotAll) {
		this.rotAll = rotAll;
	}
}

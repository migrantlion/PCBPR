package com.plancrawler.view.support;

import java.util.EventObject;

public class PrefsEvent extends EventObject{
	private static final long serialVersionUID = 1L;
	String pdfPath;
	String tempPath;
	int	DPI;
	
	public PrefsEvent(Object source) {
		super(source);
	}

	public PrefsEvent(Object source, String pdfPath, String tempPath, int DPI){
		this(source);
		this.pdfPath = pdfPath;
		this.tempPath = tempPath;
		this.DPI = DPI;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	public int getDPI() {
		return DPI;
	}

	public void setDPI(int dPI) {
		DPI = dPI;
	}
	
}

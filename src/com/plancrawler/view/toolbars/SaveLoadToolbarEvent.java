package com.plancrawler.view.toolbars;

import java.util.EventObject;

public class SaveLoadToolbarEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private boolean saveRequest = false;
	private boolean loadRequest = false;
	private boolean loadPDFRequest = false;
	
	public SaveLoadToolbarEvent(Object source) {
		super(source);
	}

	public boolean isSaveRequest() {
		return saveRequest;
	}

	public void setSaveRequest(boolean saveRequest) {
		this.saveRequest = saveRequest;
	}

	public boolean isLoadRequest() {
		return loadRequest;
	}

	public void setLoadRequest(boolean loadRequest) {
		this.loadRequest = loadRequest;
	}

	public boolean isLoadPDFRequest() {
		return loadPDFRequest;
	}

	public void setLoadPDFRequest(boolean loadPDFRequest) {
		this.loadPDFRequest = loadPDFRequest;
	}
	
}

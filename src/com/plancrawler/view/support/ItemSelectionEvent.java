package com.plancrawler.view.support;

import java.util.EventObject;

public class ItemSelectionEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private boolean modifyRequest;
	private int row;
	private boolean deleteRequest;
	
	public ItemSelectionEvent(Object source) {
		super(source);
	}
	
	public ItemSelectionEvent(Object source, int row, boolean modify, boolean delete){
		super(source);
		this.row = row;
		this.modifyRequest = modify;
		this.deleteRequest = delete;
	}

	public boolean isModifyRequest() {
		return modifyRequest;
	}

	public int getRow() {
		return row;
	}

	public boolean isDeleteRequest() {
		return deleteRequest;
	}

}

package com.plancrawler.view.toolbars;

import java.util.EventObject;

public class FocusToolbarEvent extends EventObject {
	static final long serialVersionUID = 1L;
	boolean focusRequested;
	boolean fitToScreenRequested;
	
	public FocusToolbarEvent(Object source) {
		super(source);
	}
		
	public FocusToolbarEvent(Object source, boolean focus, boolean fit) {
		this(source);
		this.focusRequested = focus;
		this.fitToScreenRequested = fit;
	}

	public boolean isFocusRequested() {
		return focusRequested;
	}
	
	public boolean isFitToScreenRequested(){
		return fitToScreenRequested;
	}

}

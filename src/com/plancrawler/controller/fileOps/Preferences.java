package com.plancrawler.controller.fileOps;

import java.util.ArrayList;
import java.util.List;

public class Preferences {
	String savePath;
	String loadPath;
	String tempPath;
	List<String> recentFiles = new ArrayList<String>();
	String prefPath = "preferences/";
	String prefFilename = "pcbpr_prefs.pcf";
	
	public Preferences(){
		loadPrefs();
	}
	
	public void loadPrefs(){
		
	}
}

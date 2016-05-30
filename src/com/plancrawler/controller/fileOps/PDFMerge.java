package com.plancrawler.controller.fileOps;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PDFMerge {

	public static void mergePDFs(File[] files, File saveFile) throws IOException {
		PDDocument saveDoc= new PDDocument();
		
		PDDocument[] inDoc = new PDDocument[files.length];
		// now iterate over each file
		for (int n = 0; n < files.length; n++){
			inDoc[n] = PDDocument.load(files[n]);
			// add all pages from this file to the saveDoc
			for (int p = 0; p < inDoc[n].getNumberOfPages(); p++){
				PDPage page = inDoc[n].getPage(p);
				saveDoc.addPage(page);
			}
		}
		
		saveDoc.save(saveFile);
		saveDoc.close();
		for (int n = 0; n < inDoc.length; n++)
			inDoc[n].close();
	}
}

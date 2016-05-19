package com.plancrawler.controller.fileOps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class TempImageBuffer {

	private static TempImageBuffer uniqueInstance = new TempImageBuffer();
	private String pdfFileName;
	private String tempPath = "C:\\Users\\steve\\Documents\\PlanCrawler\\Temp\\";
	private String fileHead = "pcbpr";
	private String randchars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890_";
	private List<String> usedNames = new ArrayList<String>();

	private class TFileDB {
		File file;
		boolean alreadyWritten = false;

		public TFileDB(String filename) {
			this.file = new File(filename);
			alreadyWritten = false;
		}
	}

	private TFileDB[] tfdb;

	private TempImageBuffer() {
	}

	public static TempImageBuffer getInstance() {
		return uniqueInstance;
	}
	
	public void cleanUp(){
		File directory = new File(tempPath);
        //get all the files from a directory
        File[] fList = directory.listFiles();

        for (File file : fList){
            if (file.isFile() && (file.getAbsolutePath().endsWith(".pcf"))){
            	file.delete();
            }
        }
	}

	public void clearBuffer() {
		if (tfdb == null)
			return;
		// delete all the files in the temp buffer
		for (int n = 0; n < tfdb.length; n++) {
			tfdb[n].file.delete();
		}
		// delete the database as it is no longer needed.
		tfdb = null;
	}

	public void startBuffer(String fileName, int numPages) {
		this.pdfFileName = fileName;
		// setup the buffer database
		tfdb = new TFileDB[numPages];
		for (int n = 0; n < numPages; n++) {
			tfdb[n] = new TFileDB(assignNewFileName());
		}

		// start the buffer going
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				writeOutTempFiles();
			}
		});
		thread.start();
	}

	public BufferedImage getImageFromBuffer(int index) {
		BufferedImage image = null;
		if (tfdb[index].alreadyWritten)
			try {
				image = readWriteImage(index, null);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		else {
			image = convertToImage(index);
			try {
				readWriteImage(index, image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return image;
	}

	private BufferedImage convertToImage(int page){
		BufferedImage image = null;
		try (PDDocument document = PDDocument.load(new File(pdfFileName))) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			image = pdfRenderer.renderImageWithDPI(page, DocumentHandler.DPI);
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	private void writeOutTempFiles() {
		BufferedImage image;
		try (PDDocument document = PDDocument.load(new File(pdfFileName))) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);

			for (int n = 0; n < tfdb.length; n++) {
				if (!tfdb[n].alreadyWritten) {
					image = pdfRenderer.renderImageWithDPI(n, DocumentHandler.DPI);
					readWriteImage(n, image);
				}
			}
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized BufferedImage readWriteImage(int index, BufferedImage image) throws IOException {
		BufferedImage retImage = null;
		// reads or writes an image to file. If image == null, assume reading
		if (image == null) {
			// we need to read an image from the temp file
			retImage = ImageIO.read(tfdb[index].file);
		} else {
			if (!tfdb[index].alreadyWritten) {
				ImageIO.write(image, "png", tfdb[index].file);
				tfdb[index].alreadyWritten = true;
			}
		}

		return retImage;
	}

	private String assignNewFileName() {
		String newName = null;
		boolean haveGoodName = false;

		while (!haveGoodName) {
			newName = getRandName(8);
			if (!usedNames.contains(newName)) {
				haveGoodName = true;
				usedNames.add(newName);
			}
		}
		return tempPath + newName;
	}

	private String getRandName(int num) {
		String randString = fileHead;
		int len = randchars.length();

		for (int n = 0; n < num; n++) {
			int c = (int) (Math.random() * len);
			randString += randchars.substring(c, c + 1);
		}
		return randString + ".pcf";
	}

	public String getPath() {
		return tempPath;
	}

	public void setPath(String path) {
		this.tempPath = path;
	}

	public String getFileHead() {
		return fileHead;
	}

	public void setFileHead(String fileHead) {
		this.fileHead = fileHead;
	}

	public List<String> getUsedNames() {
		return usedNames;
	}

	public void setUsedNames(List<String> usedNames) {
		this.usedNames = usedNames;
	}

	public void stopBuffer() {
		clearBuffer();
		cleanUp();
	}

}

package com.plancrawler.model;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class DocumentHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	private String currentFile;
	private String path = "C:\\Users\\Steve.Soss\\Documents\\PlanCrawler\\Plans\\";
	private int numPages, currentPage;
	public static final int DPI = 300;
	private HashMap<Integer, Double> pageRotations;
	private HashMap<Integer, Double> pageCalibration;

	public DocumentHandler() {
		this.numPages = 0;
		this.currentPage = 0;
		this.pageRotations = new HashMap<Integer, Double>();
		this.pageCalibration = new HashMap<Integer, Double>();
		this.currentFile = null;
	}

	public void registerCalibration(double scale) {
		pageCalibration.put(currentPage, scale);
	}

	public BufferedImage getCurrentPageImage() {
		return getPageImage(currentPage);
	}

	public BufferedImage getNextPageImage() {
		return getPageImage(currentPage + 1);
	}

	public BufferedImage getPrevPageImage() {
		return getPageImage(currentPage - 1);
	}

	public synchronized BufferedImage getPageImage(int pageNum) {
		BufferedImage image = null;

		if (currentFile == null || numPages <= 0) {
			image = null;
			return null;
		} else {
			if (pageNum >= numPages)
				pageNum = numPages - 1;
			if (pageNum <= 0)
				pageNum = 0;
			currentPage = pageNum;
		
			try (PDDocument document = PDDocument.load(new File(currentFile))) {
				PDFRenderer pdfRenderer = new PDFRenderer(document);

				image = pdfRenderer.renderImageWithDPI(currentPage, DPI);
				image = rotateImage(image);

				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return image;
	}

	private BufferedImage rotateImage(BufferedImage image) {
		if (pageRotations.containsKey(currentPage)) {
			double radians = pageRotations.get(currentPage);

			// need to get the new center of the rotated image
			int w = image.getWidth();
			int h = image.getHeight();

			// define transformation
			AffineTransform t = new AffineTransform();
			t.rotate(radians, w / 2d, h / 2d);

			// rectangle around source image
			Point[] points = { new Point(0, 0), new Point(w, 0), new Point(w, h), new Point(0, h) };
			// transform to destination rectangle
			t.transform(points, 0, points, 0, 4);

			// now get destination box center min/max x and y
			Point min = new Point(points[0]);
			Point max = new Point(points[0]);
			for (int i = 1, n = points.length; i < n; i++) {
				Point p = points[i];
				double pX = p.getX(), pY = p.getY();

				// update min/max x
				if (pX < min.getX())
					min.setLocation(pX, min.getY());
				if (pX > max.getX())
					max.setLocation(pX, max.getY());

				// update min/max y
				if (pY < min.getY())
					min.setLocation(min.getX(), pY);
				if (pY > max.getY())
					max.setLocation(max.getX(), pY);
			}

			// determine new width, height
			w = (int) (max.getX() - min.getX());
			h = (int) (max.getY() - min.getY());

			// determine required translation
			double tx = min.getX();
			double ty = min.getY();

			// append required translation
			AffineTransform translation = new AffineTransform();
			translation.translate(-tx, -ty);
			t.preConcatenate(translation);

			AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);

			BufferedImage rotImage = new BufferedImage(w, h, image.getType());
			op.filter(image, rotImage);
			return rotImage;
		} else
			return image;
	}

	public void registerRotation(double radians) {
		if (pageRotations.containsKey(currentPage))
			pageRotations.put(currentPage, pageRotations.get(currentPage) + radians);
		else
			pageRotations.put(currentPage, radians);
	}
	
	public void clearDocProperties() {
		currentFile = null;
		numPages = 0;
		currentPage = 0;
		pageCalibration.clear();
		pageRotations.clear();
	}

	public void setDocProperties() {
		if (currentFile == null)
			return;
		try (PDDocument document = PDDocument.load(new File(currentFile))) {
			numPages = document.getNumberOfPages();
			currentPage = 0;
			pageCalibration.clear();
			pageRotations.clear();

			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getNumPages() {
		return numPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public double getCalibration() {
		if (pageCalibration.containsKey(currentPage))
			return pageCalibration.get(currentPage);
		else
			return 1.0;
	}

	public void registerRotationToAllPages() {
		if (pageRotations.containsKey(currentPage)) {
			double rotation = pageRotations.get(currentPage);
			for (int i = 0; i < numPages; i++)
				pageRotations.put(i, rotation);
		}
	}

	public String getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}

	public String getCurrentPath() {
		return path;
	}

}


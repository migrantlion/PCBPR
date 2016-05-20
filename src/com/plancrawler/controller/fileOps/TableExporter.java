package com.plancrawler.controller.fileOps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.table.AbstractTableModel;

public class TableExporter {

	public static void saveTableAsCSV(AbstractTableModel tableModel, File file){
		int rows = tableModel.getRowCount();
		int cols = tableModel.getColumnCount();
		
		try{
			FileWriter writer = new FileWriter(file);
			
			String header = "";
			for (int c = 0; c < cols; c++){
				header += "\"" + tableModel.getColumnName(c) + "\"";
				if (c+1 < cols)
					header += ", ";
			}
			header += "\n";
			writer.write(header);
			
			// now write the body
			for (int r = 0; r < rows; r++) {
				String rowString = "";
				for (int c = 0; c < cols; c++){
					rowString += "\"" + tableModel.getValueAt(r, c) + "\"";
					if (c+1 < cols)
						rowString += ", ";
				}
				rowString += "\n";
				writer.write(rowString);
			}
			writer.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
}

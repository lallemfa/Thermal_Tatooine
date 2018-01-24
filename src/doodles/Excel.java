package doodles;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Excel {

	private final File file;
	private final Workbook workbook;
	private Sheet sheet;

	
	
	public Excel(String filePath) throws IOException, InvalidFormatException {
		
	    file = new File(filePath);
    	workbook = WorkbookFactory.create(file);
    	
    	sheet = workbook.getSheetAt(0);
	}
	
	public Sheet getSheetByName(String name) {
		sheet = workbook.getSheet(name);
		return sheet;
	}
	
	public void printCell(int row, int col) {
		Row selectedRow = sheet.getRow(row);
		String msg = selectedRow.getCell(col).getStringCellValue();
	}
	
	
}

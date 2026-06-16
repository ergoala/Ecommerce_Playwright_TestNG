package com.liverpool.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	private ExcelReader() {
	}

	/**
	 * Reads test data from a specified Excel sheet for a given test case.
	 * It searches for the test case name in the first column and returns
	 * all data for that row as a Map.
	 *
	 * @param excelFilePath The absolute path to the Excel file.
	 * @param sheetName The name of the sheet within the Excel file.
	 * @param testCaseName The name of the test case to retrieve data for (matches first column).
	 * @return A Map where the key is the row number and the value is a Map of column headers to cell values.
	 * @throws RuntimeException if the sheet is not found or an error occurs during file reading.
	 */
	public static Map<Integer, Map<String, String>> getTestDataFromExcel(String excelFilePath, String sheetName,
			String testCaseName) {
		Map<Integer, Map<String, String>> dataMap = new LinkedHashMap<>();

		try (FileInputStream fis = new FileInputStream(excelFilePath);
				XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new RuntimeException("Sheet '" + sheetName + "' not found in " + excelFilePath);
			}

			XSSFRow headerRow = sheet.getRow(0);
			int rowCount = sheet.getPhysicalNumberOfRows();

			for (int i = 1; i < rowCount; i++) {
				XSSFRow row = sheet.getRow(i);
				if (row == null)
					continue;

				XSSFCell testCaseCell = row.getCell(0);
				String tcName = getCellValueAsString(testCaseCell);

				if (tcName != null && tcName.equalsIgnoreCase(testCaseName)) {
					Map<String, String> rowMap = new HashMap<>();
					for (int j = 0; j < headerRow.getPhysicalNumberOfCells(); j++) {
						String header = getCellValueAsString(headerRow.getCell(j));
						String value = getCellValueAsString(row.getCell(j));
						rowMap.put(header, value);
					}
					dataMap.put(i, rowMap);
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading Excel file: " + excelFilePath, e);
		}

		return dataMap;
	}

	/**
	 * Reads master data from a specified Excel sheet based on URL.
	 * It searches for the URL in the second column and returns
	 * all data for that row as a Map.
	 *
	 * @param excelFilePath The absolute path to the Excel file.
	 * @param sheetName The name of the sheet within the Excel file.
	 * @param url The URL to match in the second column of the sheet.
	 * @param environment The environment (currently unused in logic but part of signature).
	 * @param ccNumber The credit card number (currently unused in logic but part of signature).
	 * @return A Map where the key is the row number and the value is a Map of column headers to cell values.
	 * @throws RuntimeException if the sheet is not found or an error occurs during file reading.
	 */
	public static Map<Integer, Map<String, String>> getMasterDataFromExcel(String excelFilePath, String sheetName,
			String url, String environment, String ccNumber) {
		Map<Integer, Map<String, String>> dataMap = new LinkedHashMap<>();

		try (FileInputStream fis = new FileInputStream(excelFilePath);
				XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new RuntimeException("Sheet '" + sheetName + "' not found in " + excelFilePath);
			}

			XSSFRow headerRow = sheet.getRow(0);
			int rowCount = sheet.getPhysicalNumberOfRows();

			for (int i = 1; i < rowCount; i++) {
				XSSFRow row = sheet.getRow(i);
				if (row == null)
					continue;

				String urlCell = getCellValueAsString(row.getCell(1));
				if (urlCell != null && urlCell.equalsIgnoreCase(url)) {
					Map<String, String> rowMap = new HashMap<>();
					for (int j = 0; j < headerRow.getPhysicalNumberOfCells(); j++) {
						String header = getCellValueAsString(headerRow.getCell(j));
						String value = getCellValueAsString(row.getCell(j));
						rowMap.put(header, value);
					}
					dataMap.put(i, rowMap);
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading Excel master data: " + excelFilePath, e);
		}

		return dataMap;
	}

	/**
	 * Retrieves the string representation of a cell's value, handling different cell types.
	 * Numeric values are converted to long if they are whole numbers, otherwise to their double string representation.
	 *
	 * @param cell The XSSFCell object to get the value from.
	 * @return The string representation of the cell's value, or an empty string if the cell is null or its type is not handled.
	 */
	private static String getCellValueAsString(XSSFCell cell) {
		if (cell == null)
			return "";

		CellType type = cell.getCellType();
		switch (type) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			double numericValue = cell.getNumericCellValue();
			if (numericValue == Math.floor(numericValue) && !Double.isInfinite(numericValue)) {
				return String.valueOf((long) numericValue);
			}
			return String.valueOf(numericValue);
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		default:
			return "";
		}
	}
}

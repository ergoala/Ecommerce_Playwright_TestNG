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

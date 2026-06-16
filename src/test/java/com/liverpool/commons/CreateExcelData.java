package com.liverpool.commons;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;

public class CreateExcelData {

    /**
     * Creates an Excel file named "LiverpoolTestData.xlsx" with two sheets: "MasterData" and "TestData".
     * This method is annotated with @Test and belongs to the "setup" group, indicating it's
     * part of the test setup process.
     *
     * The "MasterData" sheet contains configuration for different environments and browsers.
     * The "TestData" sheet contains specific test case data.
     *
     * @throws IOException If an error occurs during file creation or writing.
     */
    @Test(groups = {"setup"})
    public void createExcelFile() throws IOException {
        String filePath = "src/test/resources/Excel/LiverpoolTestData.xlsx";

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            // ===== HOJA 1: MasterData =====
            Sheet masterSheet = workbook.createSheet("MasterData");
            Row masterHeader = masterSheet.createRow(0);
            String[] masterHeaders = {"Id", "Url", "Environment", "Browser", "Headless", "Timeout", "NavigationTimeout", "CreditCard"};
            for (int i = 0; i < masterHeaders.length; i++) {
                Cell cell = masterHeader.createCell(i);
                cell.setCellValue(masterHeaders[i]);
                setHeaderStyle(cell, workbook);
            }

            // Row 1: QA Environment
            Row masterRow1 = masterSheet.createRow(1);
            masterRow1.createCell(0).setCellValue(1);
            masterRow1.createCell(1).setCellValue("https://www.liverpool.com.mx/tienda/home");
            masterRow1.createCell(2).setCellValue("QA");
            masterRow1.createCell(3).setCellValue("chromium");
            masterRow1.createCell(4).setCellValue("false");
            masterRow1.createCell(5).setCellValue("30000");
            masterRow1.createCell(6).setCellValue("60000");
            masterRow1.createCell(7).setCellValue("");

            // Row 2: PROD Environment (example)
            Row masterRow2 = masterSheet.createRow(2);
            masterRow2.createCell(0).setCellValue(2);
            masterRow2.createCell(1).setCellValue("https://www.liverpool.com.mx/tienda/home");
            masterRow2.createCell(2).setCellValue("PROD");
            masterRow2.createCell(3).setCellValue("chromium");
            masterRow2.createCell(4).setCellValue("true");
            masterRow2.createCell(5).setCellValue("30000");
            masterRow2.createCell(6).setCellValue("60000");
            masterRow2.createCell(7).setCellValue("");

            autoSizeColumns(masterSheet, masterHeaders.length);

            // ===== HOJA 2: TestData =====
            Sheet testSheet = workbook.createSheet("TestData");
            Row testHeader = testSheet.createRow(0);
            String[] testHeaders = {"TestCaseName", "SearchWord", "UrlFragment", "Execute", "Description", "Category", "Priority"};
            for (int i = 0; i < testHeaders.length; i++) {
                Cell cell = testHeader.createCell(i);
                cell.setCellValue(testHeaders[i]);
                setHeaderStyle(cell, workbook);
            }

            // TC_01_Search_Product
            Row testRow1 = testSheet.createRow(1);
            testRow1.createCell(0).setCellValue("TC_01_Search_Product");
            testRow1.createCell(1).setCellValue("iPhone");
            testRow1.createCell(2).setCellValue("iphone");
            testRow1.createCell(3).setCellValue("Yes");
            testRow1.createCell(4).setCellValue("Search for iPhone on Liverpool store");
            testRow1.createCell(5).setCellValue("Search");
            testRow1.createCell(6).setCellValue("High");

            // TC_02_Search_Laptop (from JSON)
            Row testRow2 = testSheet.createRow(2);
            testRow2.createCell(0).setCellValue("TC_02_Search_Laptop");
            testRow2.createCell(1).setCellValue("laptop");
            testRow2.createCell(2).setCellValue("laptop");
            testRow2.createCell(3).setCellValue("Yes");
            testRow2.createCell(4).setCellValue("Search for laptop on Liverpool store");
            testRow2.createCell(5).setCellValue("Search");
            testRow2.createCell(6).setCellValue("Medium");

            // TC_04_Navigation_Menu (from JSON - note: test uses TC_04 but class is TC_02)
            Row testRow3 = testSheet.createRow(3);
            testRow3.createCell(0).setCellValue("TC_04_Navigation_Menu");
            testRow3.createCell(1).setCellValue("");
            testRow3.createCell(2).setCellValue("");
            testRow3.createCell(3).setCellValue("Yes");
            testRow3.createCell(4).setCellValue("Verify hamburger menu and logo visibility");
            testRow3.createCell(5).setCellValue("Navigation");
            testRow3.createCell(6).setCellValue("High");

            // TC_05_Logo_Visible (from JSON)
            Row testRow4 = testSheet.createRow(4);
            testRow4.createCell(0).setCellValue("TC_05_Logo_Visible");
            testRow4.createCell(1).setCellValue("");
            testRow4.createCell(2).setCellValue("");
            testRow4.createCell(3).setCellValue("Yes");
            testRow4.createCell(4).setCellValue("Verify logo is visible on home page");
            testRow4.createCell(5).setCellValue("Navigation");
            testRow4.createCell(6).setCellValue("High");

            autoSizeColumns(testSheet, testHeaders.length);

            workbook.write(fos);
            System.out.println("Excel file created successfully at: " + filePath);
        }
    }

    /**
     * Main method to execute the creation of the Excel file.
     *
     * @param args Command line arguments (not used).
     * @throws IOException If an error occurs during file creation or writing.
     */
    public static void main(String[] args) throws IOException {
        new CreateExcelData().createExcelFile();
    }

    /**
     * Creates a CellStyle for header cells with bold, white font on a dark blue background,
     * and thin borders.
     *
     * @param workbook The XSSFWorkbook to create the CellStyle for.
     * @return A configured CellStyle object for headers.
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        return headerStyle;
    }

    /**
     * Applies the predefined header style to a given cell.
     *
     * @param cell The Cell to apply the style to.
     * @param workbook The XSSFWorkbook used to create the header style.
     */
    private static void setHeaderStyle(Cell cell, Workbook workbook) {
        cell.setCellStyle(createHeaderStyle(workbook));
    }

    /**
     * Automatically adjusts the width of columns in a given sheet to fit their content,
     * and adds a small padding.
     *
     * @param sheet The Sheet whose columns need to be auto-sized.
     * @param columnCount The number of columns to auto-size.
     */
    private static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, width + 500);
        }
    }
}

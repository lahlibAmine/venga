package com.ram.venga.util;

import com.mysema.commons.lang.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class ExcelGeneratorUtility {

    public static <DATA> void generateExcelReport(HttpServletResponse response, List<DATA> dataList, List<Pair<String, Function<DATA,String>>> pairs) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");

            // Define styles
            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setBorderTop(BorderStyle.NONE);
            textStyle.setBorderRight(BorderStyle.NONE);
            textStyle.setBorderBottom(BorderStyle.NONE);
            textStyle.setBorderLeft(BorderStyle.NONE);
            textStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setBorderTop(BorderStyle.NONE);
            numberStyle.setBorderRight(BorderStyle.NONE);
            numberStyle.setBorderBottom(BorderStyle.NONE);
            numberStyle.setBorderLeft(BorderStyle.NONE);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
           // numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            // Header
            Row headerRow = sheet.createRow(0);
            int colNum = 0;
            for (Pair<String, Function<DATA,String>> pair : pairs) {
                Cell cell = headerRow.createCell(colNum++);
                cell.setCellValue(pair.getFirst());
                cell.setCellStyle(textStyle);
            }

            // Data rows
            int rowNum = 1;
            for (DATA data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                colNum = 0;
                for (Pair<String, Function<DATA,String>> pair : pairs) {
                    Cell cell = dataRow.createCell(colNum++);
                    String value = pair.getSecond().apply(data);

                    // Check if the value is numeric and set the cell value with the appropriate style
                    // Check if the value is numeric
                    if (isNumeric(value)) {
                        try {
                            // Check if the value is a decimal number
                            if (value.contains(".")) {
                                // Parse as double if it has a decimal point
                                cell.setCellValue(Double.parseDouble(value));
                            } else {
                                // Parse as integer if no decimal point
                                cell.setCellValue(Integer.parseInt(value));
                            }
                            cell.setCellStyle(numberStyle);
                        } catch (NumberFormatException e) {
                            // Handle number format exceptions
                            cell.setCellValue(value);
                            cell.setCellStyle(textStyle);
                        }
                    } else {
                        // If not numeric, set as text
                        cell.setCellValue(value);
                        cell.setCellStyle(textStyle);
                    }

                }
            }

            // Write output to response
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            System.out.println("sww!!");
        }
    }

    // Helper method to check if a string is numeric
    private static boolean isNumeric(String value) {
        if (value == null) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
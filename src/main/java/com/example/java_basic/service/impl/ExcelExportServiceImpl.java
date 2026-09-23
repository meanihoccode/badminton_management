package com.example.java_basic.service.impl;

import com.example.java_basic.dto.TransactionResponseDTO;
import com.example.java_basic.service.ExcelExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public byte[] exportTransactionsToExcel(List<TransactionResponseDTO> transactions) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Lich_Su_Giao_Dich");

            // Header Font
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            // Header Style
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            // Row Style (for normal rows)
            CellStyle rowStyle = workbook.createCellStyle();
            rowStyle.setBorderBottom(BorderStyle.THIN);
            rowStyle.setBorderTop(BorderStyle.THIN);
            rowStyle.setBorderLeft(BorderStyle.THIN);
            rowStyle.setBorderRight(BorderStyle.THIN);
            rowStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Money Style
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.cloneStyleFrom(rowStyle);
            DataFormat format = workbook.createDataFormat();
            moneyStyle.setDataFormat(format.getFormat("#,##0"));

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(25);
            String[] columns = {"ID", "Ngày Giờ", "Loại Giao Dịch", "Số Tiền (VND)", "Mô Tả"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Create Data Rows
            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            for (TransactionResponseDTO tx : transactions) {
                Row row = sheet.createRow(rowIdx++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(tx.getId() != null ? tx.getId().toString() : "");
                cell0.setCellStyle(rowStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(tx.getCreatedAt() != null ? tx.getCreatedAt().format(formatter) : "");
                cell1.setCellStyle(rowStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(tx.getTransactionType() != null ? tx.getTransactionType().name() : "");
                cell2.setCellStyle(rowStyle);

                Cell cell3 = row.createCell(3);
                if (tx.getAmount() != null) {
                    cell3.setCellValue(tx.getAmount().doubleValue());
                }
                cell3.setCellStyle(moneyStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(tx.getDescription() != null ? tx.getDescription() : "");
                cell4.setCellStyle(rowStyle);
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
                // Thêm tí padding
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage(), e);
        }
    }
}

package com.dairy.farm.service;

import com.dairy.farm.entity.MilkSale;
import com.dairy.farm.repository.MilkSaleRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private MilkSaleRepository milkSaleRepository;


    public byte[] generateExcelReport() {
        // Create a Workbook
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Create a Sheet
            Sheet sheet = workbook.createSheet("Milk Sales Report");

            // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Create a Row for the header
            Row headerRow = sheet.createRow(0);

            // Define header cells
            String[] columns = {"ID", "Date", "Day of Week", "Liters Sold"};

            // Create cells and set their style
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Fetch data from the repository
            List<MilkSale> milkSales = milkSaleRepository.findAll();

            // Populate the sheet with data
            int rowNum = 1;
            for (MilkSale sale : milkSales) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(sale.getId());
                row.createCell(1).setCellValue(sale.getDate().toString());
                row.createCell(2).setCellValue(sale.getDayOfWeek());
                row.createCell(3).setCellValue(sale.getTotalLitres());
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a byte array
            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
    public byte[] generatePdfReport() {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Create a new page
            PDPage page = new PDPage();
            document.addPage(page);

            // Start a new content stream
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true)) {
                // Set the font and font size
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);

                // Write title
                contentStream.beginText();
                contentStream.setLeading(20f);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Milk Sales Report");
                contentStream.newLine();
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Fetch data from the repository
                List<MilkSale> milkSales = milkSaleRepository.findAll();

                // Write column headers
                contentStream.showText("ID    Date       Day of Week      Liters Sold");
                contentStream.newLine();

                // Write each record
                for (MilkSale sale : milkSales) {
                    contentStream.showText(String.format("%-5d %-10s %-15s %-10.2f",
                            sale.getId(),
                            sale.getDate().toString(),
                            sale.getDayOfWeek(),
                            sale.getTotalLitres()));
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            // Save document to byte array
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}

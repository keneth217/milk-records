package com.dairy.farm.service;

import com.dairy.farm.entity.MilkSale;
import com.dairy.farm.repository.MilkSaleRepository;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private MilkSaleRepository milkSaleRepository;

    public ByteArrayInputStream createSalesPdf() throws MalformedURLException {
        List<MilkSale> milkSales = milkSaleRepository.findAllByOrderByDateAsc();

        // Output stream to hold the PDF content
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Initialize PDF document
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Add Farm Header with Logo and Details
        String logoPath = "src/main/resources/static/logo.jpg";  // Adjust the path as per your project structure
        ImageData logoData = ImageDataFactory.create(logoPath);
        Image logo = new Image(logoData).scaleAbsolute(50, 50);

        // Create a table for header with logo and farm details
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
        headerTable.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER)); // Logo cell

        // Add farm details in the second column
        Cell farmDetailsCell = new Cell()
                .add(new Paragraph("Farm Name: Dairy Farm Ltd").setBold().setFontSize(12))
                .add(new Paragraph("Address: 1234 Farm Road, Green Valley").setFontSize(10))
                .add(new Paragraph("Phone: +1234567890").setFontSize(10))
                .add(new Paragraph("Location: Green Valley, TX").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);

        headerTable.addCell(farmDetailsCell);

        // Add header table to document
        document.add(headerTable);

        // Add a line separator after the header
        SolidLine solidLine = new SolidLine(1f);  // Create a solid line with 1.0 thickness
        LineSeparator separator = new LineSeparator(solidLine);
        document.add(separator);

        // Add title for the sales report
        document.add(new Paragraph("Milk Sales Report").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));

        // Add a gap before the sales table
        document.add(new Paragraph("\n"));

        // Define a table for the sales report with proper column widths
        Table salesTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2, 2, 2, 2})).useAllAvailableWidth();
        salesTable.setWidth(UnitValue.createPercentValue(100)); // Use full width

        // Repeat header on every page
        salesTable.setSkipFirstHeader(false);

        // Add headers with a bold font and blue color
        salesTable.addHeaderCell(new Cell().add(new Paragraph("#").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Date").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Day").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("T.Litres").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Per Litre").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("T. Amount").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Paid ").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Balance").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));

        // Initialize totals
        double totalLitres = 0;
        double totalAmount = 0;
        double totalPaidAmount = 0;
        double totalBalance = 0;

        // Populate table with milk sale data and calculate totals
        int counter = 1;
        for (MilkSale sale : milkSales) {
            totalLitres += sale.getTotalLitres();
            totalAmount += sale.getTotalAmount();
            totalPaidAmount += sale.getTotalPaidAmount();
            totalBalance += sale.getRemainingAmount();

            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(counter++)).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(sale.getDate().toString()).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(sale.getDayOfWeek()).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(sale.getTotalLitres().toString()).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getLitreCost())).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getTotalAmount())).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getTotalPaidAmount())).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getRemainingAmount())).setFontSize(8)));
        }

        // Add totals row with bold, blue text
        salesTable.addCell(new Cell(1, 3).add(new Paragraph("Total").setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalLitres)).setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addCell(new Cell().add(new Paragraph("")));  // Skip Litre Cost
        salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalAmount)).setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalPaidAmount)).setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));
        salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalBalance)).setBold().setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE)));

        // Add the sales table to the document
        document.add(salesTable);

        // Add a small description or report date
        document.add(new Paragraph("Generated on: " + LocalDate.now()).setFontSize(10).setTextAlignment(TextAlignment.CENTER));

        // Close document
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream createSalesBtwPdf(LocalDate startDate, LocalDate endDate) throws MalformedURLException {
        List<MilkSale> milkSales = milkSaleRepository.findAllByDateBetweenOrderByDateAsc(startDate, endDate);

        // Output stream to hold the PDF content
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Initialize PDF document
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDoc);

        // Add Farm Header with Logo and Details
        String logoPath = "src/main/resources/static/logo.jpg";  // Adjust the path as per your project structure
        Image logoData = new Image(ImageDataFactory.create(logoPath)).scaleAbsolute(50, 50);

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
        headerTable.addCell(new Cell().add(logoData).setBorder(Border.NO_BORDER));
        Cell farmDetailsCell = new Cell()
                .add(new Paragraph("Farm Name: Dairy Farm Ltd").setBold().setFontSize(12))
                .add(new Paragraph("Address: 1234 Farm Road, Green Valley").setFontSize(10))
                .add(new Paragraph("Phone: +1234567890").setFontSize(10))
                .add(new Paragraph("Location: Green Valley, TX").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);
        headerTable.addCell(farmDetailsCell);
        document.add(headerTable);

        // Add a line separator after the header
        LineSeparator separator = new LineSeparator(new SolidLine(1f));
        document.add(separator);

        // Add title for the sales report
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Milk Sales Report").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));

        // Define a table for the sales report with proper column widths
        Table salesTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2, 2, 2, 2})).useAllAvailableWidth();
        salesTable.setWidth(UnitValue.createPercentValue(100));

        // Add headers with a bold font and blue color
        salesTable.addHeaderCell(new Cell().add(new Paragraph("#").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Date").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Day").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("T.Litres").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Per Litre").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("T. Amount").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Paid").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addHeaderCell(new Cell().add(new Paragraph("Balance").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));

        // Initialize totals
        double totalLitres = 0;
        double totalAmount = 0;
        double totalPaidAmount = 0;
        double totalBalance = 0;

        // Populate table with milk sale data and calculate totals
        int counter = 1;
        for (MilkSale sale : milkSales) {
            totalLitres += sale.getTotalLitres();
            totalAmount += sale.getTotalAmount();
            totalPaidAmount += sale.getTotalPaidAmount();
            totalBalance += sale.getRemainingAmount();

            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(counter++)).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(sale.getDate().toString()).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(sale.getDayOfWeek()).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(sale.getTotalLitres().toString()).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getLitreCost())).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getTotalAmount())).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getTotalPaidAmount())).setFontSize(8)));
            salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getRemainingAmount())).setFontSize(8)));
        }

        // Add totals row with bold, blue text
        salesTable.addCell(new Cell(1, 3).add(new Paragraph("Total").setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalLitres)).setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addCell(new Cell().add(new Paragraph("")));  // Skip Litre Cost
        salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalAmount)).setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalPaidAmount)).setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));
        salesTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalBalance)).setBold().setFontSize(10).setFontColor(ColorConstants.BLUE)));

        // Add the sales table to the document
        document.add(salesTable);

        // Add footer text with the selected date range and report generation date
        document.add(new Paragraph("\nReport for selected date: " + startDate + " to " + endDate)
                .setFontSize(10).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Generated on: " + LocalDate.now())
                .setFontSize(10).setTextAlignment(TextAlignment.CENTER));

        // Close document
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}

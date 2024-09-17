package com.dairy.farm.controller;

import com.dairy.farm.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadSalesPdf() throws MalformedURLException {
        ByteArrayInputStream pdfStream = reportService.createSalesPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=sales_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }

    @GetMapping("/sales/pdf" )
    public ResponseEntity<InputStreamResource> getSalesPdf(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) throws MalformedURLException {

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        ByteArrayInputStream bais = reportService.createSalesBtwPdf(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=milk-report-" + LocalDate.now() + ".pdf");

        return  ResponseEntity

                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bais));

    }
}

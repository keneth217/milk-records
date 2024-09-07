package com.dairy.farm.controller;

import com.dairy.farm.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/excel")
    public ResponseEntity<byte[]> generateExcelReport() {
        byte[] excelReport = reportService.generateExcelReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=milk_sales_report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelReport);
    }
}

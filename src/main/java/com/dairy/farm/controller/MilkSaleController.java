package com.dairy.farm.controller;

import com.dairy.farm.entity.MilkSale;
import com.dairy.farm.enums.PaymentStatus;
import com.dairy.farm.service.MilkSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/milk-sales")
//@CrossOrigin("*")
public class MilkSaleController {

    @Autowired
    private MilkSaleService milkSaleService;

    // Get all milk sales
    @GetMapping
    public ResponseEntity<List<MilkSale>> getAllSales() {
        List<MilkSale> sales = milkSaleService.getAllSales();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // Get milk sales by specific date
    @GetMapping("/by-date")
    public ResponseEntity<List<MilkSale>> getSalesByDate(@RequestParam LocalDate date) {
        List<MilkSale> sales = milkSaleService.getSalesByDate(date);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // Get milk sales by payment status
    @GetMapping("/by-status")
    public ResponseEntity<List<MilkSale>> getSalesByStatus(@RequestParam PaymentStatus status) {
        List<MilkSale> sales = milkSaleService.getSalesByStatus(status);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // Get milk sales by date and payment status
    @GetMapping("/by-date-and-status")
    public ResponseEntity<List<MilkSale>> getSalesByDateAndStatus(@RequestParam LocalDate date, @RequestParam PaymentStatus status) {
        List<MilkSale> sales = milkSaleService.getSalesByDateAndStatus(date, status);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // Get milk sales by date range
    @GetMapping("/by-date-range")
    public ResponseEntity<List<MilkSale>> getSalesByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<MilkSale> sales = milkSaleService.getSalesByDateRange(startDate, endDate);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // Add a new MilkSale
    @PostMapping
    public ResponseEntity<MilkSale> addMilkSale(@RequestBody MilkSale milkSale) {
        MilkSale savedSale = milkSaleService.addMilkSale(milkSale);
        return new ResponseEntity<>(savedSale, HttpStatus.CREATED);
    }

    // Calculate total unpaid amount
    @GetMapping("/total-unpaid")
    public ResponseEntity<Double> calculateTotalUnpaidAmount() {
        double totalUnpaid = milkSaleService.calculateTotalUnpaidAmount();
        return new ResponseEntity<>(totalUnpaid, HttpStatus.OK);
    }
}

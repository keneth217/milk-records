package com.dairy.farm.controller;

import com.dairy.farm.entity.Payment;
import com.dairy.farm.enums.PaymentStatus;
import com.dairy.farm.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/api/payments")

public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Get all payments
    @GetMapping
    public ResponseEntity<List<Payment>> allPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
    @GetMapping("/status-counts")
    public Map<PaymentStatus, Integer> getPaymentStatusCounts() {
        return paymentService.getStatusCounts();
    }


    // Method to add payment by milk sale ID
    @PostMapping("/{milkSaleId}")
    public ResponseEntity<?> addPayment(@PathVariable Long milkSaleId, @RequestBody Payment payment) {
        try {
            Payment createdPayment = paymentService.addPayment(milkSaleId, payment); // Pass milkSaleId
            return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while processing the payment.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get payments by a specific date
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getPaymentsByDate(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Payment> payments = paymentService.getPaymentsByDate(localDate);

            if (payments.isEmpty()) {
                return new ResponseEntity<>("No payments found for the specified date.", HttpStatusCode.valueOf(400));
            }
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid date format or other error occurred.", HttpStatus.BAD_REQUEST);
        }
    }

    // Get payments by date range
    @GetMapping("/date-range")
    public ResponseEntity<?> getPaymentsByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            List<Payment> payments = paymentService.getPaymentsByDateRange(start, end);

            if (payments.isEmpty()) {
                return new ResponseEntity<>("No payments found for the specified date range.", HttpStatusCode.valueOf(400));

            }
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>("Invalid date format. Please use yyyy-MM-dd.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Log the exception here
            return new ResponseEntity<>("An error occurred while processing your request.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Get payments by status (e.g., PAID, PARTIALLY_PAID, OVERPAID)
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPaymentsByStatus(@PathVariable String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentService.getPaymentsByStatus(paymentStatus);

            if (payments.isEmpty()) {
                return new ResponseEntity<>("No payments found for the specified status.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid payment status provided.", HttpStatus.BAD_REQUEST);
        }
    }

    // Get payments by milk sale (e.g., payments related to a specific MilkSale ID)
    @GetMapping("/milk-sale/{milkSaleId}")
    public ResponseEntity<?> getPaymentsByMilkSale(@PathVariable Long milkSaleId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByMilkSaleId(milkSaleId);

            if (payments.isEmpty()) {
                return new ResponseEntity<>("No payments found for the specified milk sale.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while retrieving payments.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.dairy.farm.service;

import com.dairy.farm.entity.MilkSale;
import com.dairy.farm.entity.Payment;
import com.dairy.farm.enums.CompensationStatus;
import com.dairy.farm.enums.PaymentStatus;
import com.dairy.farm.exceptions.PaymentNotAllowedException;
import com.dairy.farm.repository.MilkSaleRepository;
import com.dairy.farm.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MilkSaleRepository milkSaleRepository;

    // Add payment method that handles overpayments and compensates for new sales
    // Add payment method that handles overpayments and compensates for new sales
    public Payment addPayment(Long milkSaleId, Payment payment) {
        // Fetch MilkSale by the provided ID
        MilkSale milkSale = milkSaleRepository.findById(milkSaleId)
                .orElseThrow(() -> new IllegalArgumentException("MilkSale not found"));

        // Check if the milk sale has been fully paid
        if (milkSale.getStatus() == PaymentStatus.PAID) {
            throw new PaymentNotAllowedException("Payment not allowed. This milk sale has already been fully paid.");
        }

        // Apply overpayment from previous sales
        double compensation = applyOverpaymentCompensation(payment);

        // Calculate new balance after compensation
        double totalPaymentsMade = calculateTotalPaymentsForSale(milkSale);
        double newBalance = milkSale.getTotalAmount() - totalPaymentsMade - payment.getAmountPaid();

        // Determine payment status based on balance
        PaymentStatus paymentStatus = getPaymentStatus(newBalance);

        // Update MilkSale entity with new balance and status
        updateMilkSale(milkSale, totalPaymentsMade + payment.getAmountPaid(), paymentStatus);

        // Save the payment with new balance and compensation details
        return savePayment(payment, milkSale, newBalance, compensation, paymentStatus);
    }


    // Apply overpayment compensation to current payment
    private double applyOverpaymentCompensation(Payment payment) {
        List<Payment> previousOverpayments = paymentRepository.findOverpayments();

        if (previousOverpayments.isEmpty()) {
            return 0.0;
        }

        Payment lastOverpayment = previousOverpayments.get(0);
        double overpaymentAmount = Math.abs(lastOverpayment.getNewBalance());

        if (overpaymentAmount >= payment.getAmountPaid()) {
            payment.setAmountPaid(0.0);
            lastOverpayment.setNewBalance(lastOverpayment.getNewBalance() + payment.getAmountPaid());
            paymentRepository.save(lastOverpayment);
            return overpaymentAmount;
        } else {
            payment.setAmountPaid(payment.getAmountPaid() - overpaymentAmount);
            lastOverpayment.setNewBalance(0.0);
            paymentRepository.save(lastOverpayment);
            return overpaymentAmount;
        }
    }

    // Save payment with details
    private Payment savePayment(Payment payment, MilkSale milkSale, double newBalance, double compensation, PaymentStatus status) {
        return paymentRepository.save(
                Payment.builder()
                        .paymentDate(payment.getPaymentDate())
                        .paymentDay(payment.getPaymentDay())
                        .amountPaid(payment.getAmountPaid())
                        .milkSale(milkSale)
                        .newBalance(newBalance)
                        .compensationStatus(compensation > 0 ? CompensationStatus.COMPENSATED : CompensationStatus.NONE)
                        .compensationAmount(compensation)
                        .status(status)
                        .description(compensation > 0 ?
                                String.format("Payment applied with overpayment of %.2f", compensation) :
                                "Payment processed successfully")
                        .build());
    }

    // Get payment status based on balance
    private PaymentStatus getPaymentStatus(double newBalance) {
        if (newBalance == 0) {
            return PaymentStatus.PAID;
        } else if (newBalance > 0) {
            return PaymentStatus.PARTIALLY_PAID;
        } else {
            return PaymentStatus.OVERPAID;
        }
    }

    // Update MilkSale with total paid amount and remaining balance
    private void updateMilkSale(MilkSale milkSale, double totalPaymentsMade, PaymentStatus status) {
        milkSale.setTotalPaidAmount(totalPaymentsMade);
        milkSale.setRemainingAmount(milkSale.getTotalAmount() - totalPaymentsMade);
        milkSale.setStatus(status);
        milkSaleRepository.save(milkSale);
    }

    // Calculate total payments made for a specific MilkSale
    private double calculateTotalPaymentsForSale(MilkSale milkSale) {
        return paymentRepository.findByMilkSale(milkSale)
                .stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();
    }

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get payments by a specific date
    public List<Payment> getPaymentsByDate(LocalDate date) {
        return paymentRepository.findByPaymentDate(date);
    }

    // Get payments by a date range
    public List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    // Get payments by status
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    // Get payments by milk sale ID
    public List<Payment> getPaymentsByMilkSaleId(Long milkSaleId) {
        MilkSale milkSale = milkSaleRepository.findById(milkSaleId)
                .orElseThrow(() -> new IllegalArgumentException("MilkSale not found"));

        return paymentRepository.findByMilkSale(milkSale);
    }
}

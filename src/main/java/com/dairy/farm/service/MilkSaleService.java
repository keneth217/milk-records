package com.dairy.farm.service;

import com.dairy.farm.entity.MilkSale;
import com.dairy.farm.entity.Payment;
import com.dairy.farm.enums.PaymentStatus;
import com.dairy.farm.repository.MilkSaleRepository;
import com.dairy.farm.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MilkSaleService {

    @Autowired
    private MilkSaleRepository milkSaleRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    // Fetch all milk sales
    public List<MilkSale> getAllSales() {
        return milkSaleRepository.findAll();
    }

    // Fetch sales by date
    public List<MilkSale> getSalesByDate(LocalDate date) {
        return milkSaleRepository.findByDate(date);
    }

    // Fetch sales by status
    public List<MilkSale> getSalesByStatus(PaymentStatus status) {
        return milkSaleRepository.findByStatus(status);
    }

    // Fetch sales by date range
    public List<MilkSale> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        return milkSaleRepository.findByDateBetween(startDate, endDate);
    }

    // Fetch sales by date and status
    public List<MilkSale> getSalesByDateAndStatus(LocalDate date, PaymentStatus status) {
        return milkSaleRepository.findByDateAndStatus(date, status);
    }

    // Add new MilkSale and handle overpayment
    public MilkSale addMilkSale(MilkSale milkSaleInput) {
        double totalAmount = milkSaleInput.getTotalLitres() * milkSaleInput.getLitreCost();
        double remainingAmount = checkAndApplyOverpayment(totalAmount);

        MilkSale milkSale = MilkSale.builder()
                .date(milkSaleInput.getDate())
                .dayOfWeek(milkSaleInput.getDate().getDayOfWeek().toString())
                .totalLitres(milkSaleInput.getTotalLitres())
                .litreCost(milkSaleInput.getLitreCost())
                .totalAmount(totalAmount)
                .remainingAmount(remainingAmount)
                .status(remainingAmount == 0 ? PaymentStatus.PAID : PaymentStatus.UNPAID)
                .build();

        return milkSaleRepository.save(milkSale);
    }

    // Check for previous overpayments and apply them to the new sale
    private double checkAndApplyOverpayment(double totalAmount) {
        List<Payment> previousOverpayments = paymentRepository.findOverpayments();
        if (!previousOverpayments.isEmpty()) {
            Payment lastOverpayment = previousOverpayments.get(0);
            double overpaymentAmount = Math.abs(lastOverpayment.getNewBalance());

            if (overpaymentAmount >= totalAmount) {
                lastOverpayment.setNewBalance(lastOverpayment.getNewBalance() + totalAmount);
                paymentRepository.save(lastOverpayment);
                return 0;
            } else {
                lastOverpayment.setNewBalance((double) 0);
                paymentRepository.save(lastOverpayment);
                return totalAmount - overpaymentAmount;
            }
        }
        return totalAmount;
    }

    // Calculate total unpaid amount
    public double calculateTotalUnpaidAmount() {
        return milkSaleRepository.findByStatus(PaymentStatus.UNPAID)
                .stream()
                .mapToDouble(MilkSale::getRemainingAmount)
                .sum();
    }
}

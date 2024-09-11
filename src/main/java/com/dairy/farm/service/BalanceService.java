package com.dairy.farm.service;

import com.dairy.farm.dto.DashboardTotals;
import com.dairy.farm.repository.MilkSaleRepository;
import com.dairy.farm.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

    @Autowired
    private MilkSaleRepository milkSaleRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public DashboardTotals getAllBalances() {
        // Total litres of milk
        Double totalLitres = milkSaleRepository.sumTotalLitres();
        if (totalLitres == null) {
            totalLitres = 0.0; // Assign default value if null
        }

        // Total amount paid
        Double totalAmountPaid = paymentRepository.sumTotalAmountPaid();
        if (totalAmountPaid == null) {
            totalAmountPaid = 0.0; // Assign default value if null
        }

        // Total unpaid amount (remaining balance) from MilkSale entity
        Double totalUnpaidAmount = milkSaleRepository.sumRemainingBalances();
        if (totalUnpaidAmount == null) {
            totalUnpaidAmount = 0.0; // Assign default value if null
        }

        // Total overpayments (where newBalance is negative)
        Double totalOverpayments = paymentRepository.sumTotalOverpayments();
        if (totalOverpayments == null) {
            totalOverpayments = 0.0; // Assign default value if null
        }

        // Calculate the final balance (unpaid amount minus overpayments)
        Double finalBalance = totalUnpaidAmount - totalOverpayments;

        // Return aggregated totals, including adjusted balance
        return new DashboardTotals(totalLitres, totalAmountPaid, finalBalance, totalOverpayments);
    }
}

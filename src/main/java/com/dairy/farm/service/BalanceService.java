package com.dairy.farm.service;

import com.dairy.farm.dto.DashboardTotals;
import com.dairy.farm.entity.MilkSale;
import com.dairy.farm.entity.Payment;
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

        // Total amount paid
        Double totalAmountPaid = paymentRepository.sumTotalAmountPaid();

        // Total unpaid amount (remaining balance) from MilkSale entity
        Double totalUnpaidAmount = milkSaleRepository.sumRemainingBalances();

        // Total overpayments (where newBalance is negative)
        Double totalOverpayments = paymentRepository.sumTotalOverpayments();

        // Calculate the final balance (unpaid amount minus overpayments)
        Double finalBalance = totalUnpaidAmount - totalOverpayments;

        // Return aggregated totals, including adjusted balance
        return new DashboardTotals(totalLitres, totalAmountPaid, finalBalance, totalOverpayments);
    }
}

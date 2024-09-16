package com.dairy.farm.dto;

import com.dairy.farm.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DashboardTotals {
    private Double totalLitres;
    private Double totalAmountPaid;
    private Double totalBalance;
    private Double  totalOverpayments; // This should be List<Payment>
    private Double lastMonthTotalEarnings;
    private Double thisMonthTotalEarnings;
    private Double lastMonthTotalLitres;
    private Double thisMonthTotalLitres;

}

package com.dairy.farm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardTotals {
    private Double totalLitresOfMilk;
    private Double totalAmountPaid;
    private Double totalBalance;
    private Double totalOverpayments;
}

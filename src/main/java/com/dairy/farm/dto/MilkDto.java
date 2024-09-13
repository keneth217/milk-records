package com.dairy.farm.dto;

import com.dairy.farm.enums.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MilkDto {
    private Long id;
    private LocalDate date;
    private String dayOfWeek;
    private Double totalLitres;
    private double litreCost;
    private double totalAmount;
    private double totalPaidAmount;
    private double remainingAmount;
    private PaymentStatus status;
}

package com.dairy.farm.entity;

import com.dairy.farm.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class MilkSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String dayOfWeek;
    private Double totalLitres;
    private double litreCost;
    private double totalAmount;
    private double totalPaidAmount;
    private double remainingAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}

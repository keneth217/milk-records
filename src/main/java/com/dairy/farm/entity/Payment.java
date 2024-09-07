package com.dairy.farm.entity;

import com.dairy.farm.enums.CompensationStatus;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate paymentDate;
    private String paymentDay;
    private Double amountPaid;
    private Double newBalance;
    @ManyToOne
    private MilkSale milkSale;

    // Add a description field
    private String description; // Added description for compensation and payment status

    // Fields for payment status
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Fields for compensation
    @Enumerated(EnumType.STRING)
    private CompensationStatus compensationStatus = CompensationStatus.NONE;  // Default is NONE
    private Double compensationAmount = 0.0; // Amount used from overpayment as compensation
}

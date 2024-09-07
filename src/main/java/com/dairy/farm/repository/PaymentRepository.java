package com.dairy.farm.repository;

import com.dairy.farm.entity.MilkSale;
import com.dairy.farm.entity.Payment;
import com.dairy.farm.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Method to find payments by MilkSale
    List<Payment> findByMilkSale(MilkSale milkSale);
    // Additional methods for finding payments by date, etc.
    List<Payment> findByPaymentDate(LocalDate paymentDate);
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    // Find all payments with overpayments (newBalance < 0)
    @Query("SELECT p FROM Payment p WHERE p.newBalance < 0 ORDER BY p.paymentDate DESC")
    List<Payment> findOverpayments();

    @Query("SELECT SUM(p.amountPaid) FROM Payment p")
    Double sumTotalAmountPaid();


    @Query("SELECT SUM(ABS(p.newBalance)) FROM Payment p WHERE p.newBalance < 0")
    Double sumTotalOverpayments();  // Sum of all overpayments (negative balances)



    List<Payment> findByStatus(PaymentStatus status);

}

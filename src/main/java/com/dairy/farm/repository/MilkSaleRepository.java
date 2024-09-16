package com.dairy.farm.repository;

import com.dairy.farm.entity.MilkSale;
import com.dairy.farm.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MilkSaleRepository extends JpaRepository<MilkSale, Long> {
    List<MilkSale> findByDate(LocalDate date);
    @Query("SELECT m FROM MilkSale m WHERE m.totalAmount < (m.totalLitres * m.litreCost)")
    List<MilkSale> findUnpaidSales();

    @Query("SELECT SUM(m.totalLitres) FROM MilkSale m")
    Double sumTotalLitres();

    @Query("SELECT SUM(m.remainingAmount) FROM MilkSale m")
    Double sumRemainingBalances();

    List<MilkSale> findByStatus(PaymentStatus status);

    List<MilkSale> findByDateAndStatus(LocalDate date, PaymentStatus status);

    List<MilkSale> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<MilkSale> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    long countByStatus(PaymentStatus status);

    @Query("SELECT SUM(ms.totalLitres) FROM MilkSale ms")
    Double getTotalLitres();

    @Query("SELECT SUM(ms.totalPaidAmount) FROM MilkSale ms")
    Double getTotalAmountPaid();

    @Query("SELECT SUM(ms.remainingAmount) FROM MilkSale ms")
    Double getTotalBalance();




}

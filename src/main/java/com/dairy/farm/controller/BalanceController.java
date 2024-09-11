package com.dairy.farm.controller;

import com.dairy.farm.dto.DashboardTotals;
import com.dairy.farm.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")

public class BalanceController {


    @Autowired
    private BalanceService balanceService;

    @GetMapping("/totals")
    public ResponseEntity<DashboardTotals> getAllBalances() {
        DashboardTotals totals = balanceService.getAllBalances();
        return ResponseEntity.ok(totals);
    }
}

package com.abhishek.expense_tracker.controller;

import com.abhishek.expense_tracker.dto.DashboardSummaryDto;
import com.abhishek.expense_tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryDto getSummary(Authentication authentication) {

        return dashboardService.getSummary(authentication.getName());
    }
}
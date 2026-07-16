package com.abhishek.expense_tracker.controller;

import com.abhishek.expense_tracker.dto.CategoryExpenseDto;
import com.abhishek.expense_tracker.dto.IncomeVsExpenseDto;
import com.abhishek.expense_tracker.dto.MonthlyExpenseDto;
import com.abhishek.expense_tracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/monthly-expenses")
    public List<MonthlyExpenseDto> getMonthlyExpenses(
            Authentication authentication) {

        return analyticsService.getMonthlyExpenses(
                authentication.getName()
        );
    }

    @GetMapping("/category-expenses")
    public List<CategoryExpenseDto> getCategoryExpenses(
            Authentication authentication) {

        return analyticsService.getCategoryExpenses(
                authentication.getName());
    }

    @GetMapping("/income-vs-expense")
    public IncomeVsExpenseDto getIncomeVsExpense(
            Authentication authentication) {

        return analyticsService.getIncomeVsExpense(
                authentication.getName());
    }
}
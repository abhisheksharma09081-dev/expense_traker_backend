package com.abhishek.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardSummaryDto {

    private Double totalExpenses;

    private Long totalTransactions;

    private Double currentMonthExpenses;
}
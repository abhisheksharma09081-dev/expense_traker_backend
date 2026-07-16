package com.abhishek.expense_tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryResult {

    private Double totalExpenses;

    private Long totalTransactions;
}
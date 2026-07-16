package com.abhishek.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IncomeVsExpenseDto {

    private Double totalIncome;

    private Double totalExpense;

    private Double balance;
}
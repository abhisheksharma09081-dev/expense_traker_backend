package com.abhishek.expense_tracker.service;

import com.abhishek.expense_tracker.dto.CategoryExpenseDto;
import com.abhishek.expense_tracker.dto.IncomeVsExpenseDto;
import com.abhishek.expense_tracker.dto.MonthlyExpenseDto;

import java.util.List;

public interface AnalyticsService {

    List<MonthlyExpenseDto> getMonthlyExpenses(String email);

    List<CategoryExpenseDto> getCategoryExpenses(String email);

    IncomeVsExpenseDto getIncomeVsExpense(String email);
}
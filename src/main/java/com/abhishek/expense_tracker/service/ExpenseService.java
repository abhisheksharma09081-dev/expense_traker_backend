package com.abhishek.expense_tracker.service;

import com.abhishek.expense_tracker.dto.ExpenseRequest;
import com.abhishek.expense_tracker.dto.ExpenseResponse;
import com.abhishek.expense_tracker.entity.Expense;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    ExpenseResponse saveExpense(ExpenseRequest request, String email);

    Page<ExpenseResponse> getUserExpenses(
            String email,
            int page,
            int size,
            String sortBy,
            String direction
    );

    ExpenseResponse updateExpense(
            String id,
            String email,
            ExpenseRequest request
    );

    void deleteExpense(String id, String email);

    List<ExpenseResponse> searchExpenses(
            String email,
            String category,
            LocalDate date,
            Double minAmount,
            Double maxAmount
    );

    List<ExpenseResponse> filterExpenses(
            String email,
            String type,
            LocalDate startDate,
            LocalDate endDate
    );
}
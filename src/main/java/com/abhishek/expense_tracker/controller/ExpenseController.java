package com.abhishek.expense_tracker.controller;

import com.abhishek.expense_tracker.dto.ExpenseRequest;
import com.abhishek.expense_tracker.dto.ExpenseResponse;
import com.abhishek.expense_tracker.dto.PageResponse;
import com.abhishek.expense_tracker.entity.Expense;
import com.abhishek.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ExpenseResponse addExpense(
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        return expenseService.saveExpense(
                request,
                authentication.getName()
        );
    }

    @GetMapping
    public PageResponse<ExpenseResponse> getExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication) {

        Page<ExpenseResponse> expensePage =
                expenseService.getUserExpenses(
                        authentication.getName(),
                        page,
                        size,
                        sortBy,
                        direction
                );

        return new PageResponse<>(
                expensePage.getContent(),
                expensePage.getNumber(),
                expensePage.getSize(),
                expensePage.getTotalElements(),
                expensePage.getTotalPages(),
                expensePage.isFirst(),
                expensePage.isLast()
        );
    }

    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(
            @PathVariable String id,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        return expenseService.updateExpense(
                id,
                authentication.getName(),
                request
        );
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(
            @PathVariable String id,
            Authentication authentication) {

        expenseService.deleteExpense(
                id,
                authentication.getName()
        );

        return "Expense deleted successfully";
    }

    @GetMapping("/search")
    public List<ExpenseResponse> searchExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            Authentication authentication) {

        return expenseService.searchExpenses(
                authentication.getName(),
                category,
                date,
                minAmount,
                maxAmount
        );
    }

    @GetMapping("/filter")
    public List<ExpenseResponse> filterExpenses(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Authentication authentication) {

        return expenseService.filterExpenses(
                authentication.getName(),
                type,
                startDate,
                endDate
        );
    }
}
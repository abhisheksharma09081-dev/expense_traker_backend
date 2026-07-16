package com.abhishek.expense_tracker.controller;

import com.abhishek.expense_tracker.dto.PageResponse;
import com.abhishek.expense_tracker.dto.IncomeRequest;
import com.abhishek.expense_tracker.dto.IncomeResponse;
import com.abhishek.expense_tracker.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public IncomeResponse addIncome(
            @Valid @RequestBody IncomeRequest request,
            Authentication authentication) {

        return incomeService.saveIncome(
                authentication.getName(),
                request
        );
    }

    @GetMapping
    public PageResponse<IncomeResponse> getAllIncomes(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "date") String sortBy,

            @RequestParam(defaultValue = "desc") String direction,

            Authentication authentication) {

        return incomeService.getUserIncomes(
                authentication.getName(),
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/{id}")
    public IncomeResponse getIncomeById(
            @PathVariable String id,
            Authentication authentication) {

        return incomeService.getIncomeById(
                id,
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public IncomeResponse updateIncome(
            @PathVariable String id,
            @Valid @RequestBody IncomeRequest request,
            Authentication authentication) {

        return incomeService.updateIncome(
                id,
                authentication.getName(),
                request
        );
    }

    @DeleteMapping("/{id}")
    public String deleteIncome(
            @PathVariable String id,
            Authentication authentication) {

        incomeService.deleteIncome(
                id,
                authentication.getName()
        );

        return "Income deleted successfully";
    }
}
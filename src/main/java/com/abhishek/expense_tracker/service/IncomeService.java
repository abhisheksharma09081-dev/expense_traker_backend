package com.abhishek.expense_tracker.service;

import com.abhishek.expense_tracker.dto.IncomeRequest;
import com.abhishek.expense_tracker.dto.IncomeResponse;
import com.abhishek.expense_tracker.dto.PageResponse;

public interface IncomeService {

    IncomeResponse saveIncome(String email, IncomeRequest request);

    PageResponse<IncomeResponse> getUserIncomes(
            String email,
            int page,
            int size,
            String sortBy,
            String direction
    );

    IncomeResponse getIncomeById(String id, String email);

    IncomeResponse updateIncome(
            String id,
            String email,
            IncomeRequest request
    );

    void deleteIncome(String id, String email);
}
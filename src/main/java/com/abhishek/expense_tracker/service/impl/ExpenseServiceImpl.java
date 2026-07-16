package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.ExpenseRequest;
import com.abhishek.expense_tracker.dto.ExpenseResponse;
import com.abhishek.expense_tracker.entity.Expense;
import com.abhishek.expense_tracker.exception.ResourceNotFoundException;
import com.abhishek.expense_tracker.repository.ExpenseRepository;
import com.abhishek.expense_tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger logger =
            LoggerFactory.getLogger(ExpenseServiceImpl.class);
    private final ExpenseRepository expenseRepository;
    private final MongoTemplate mongoTemplate;

    private ExpenseResponse mapToResponse(Expense expense) {

        return ExpenseResponse.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .category(expense.getCategory())
                .amount(expense.getAmount().doubleValue())
                .date(expense.getDate())
                .description(expense.getDescription())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

    @Override
    public ExpenseResponse saveExpense(
            ExpenseRequest request,
            String email) {

        logger.info("Saving expense for user {}", email);

        Expense expense = Expense.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .amount(BigDecimal.valueOf(request.getAmount()))
                .date(request.getDate())
                .description(request.getDescription())
                .userEmail(email)
                .build();

        return mapToResponse(
                expenseRepository.save(expense)
        );
    }
    @Override
    public Page<ExpenseResponse> getUserExpenses(
            String email,
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return expenseRepository
                .findByUserEmail(email, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ExpenseResponse updateExpense(
            String id,
            String email,
            ExpenseRequest request) {

        Expense expense = expenseRepository
                .findByIdAndUserEmail(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found"));

        expense.setTitle(request.getTitle());
        expense.setCategory(request.getCategory());
        expense.setAmount(BigDecimal.valueOf(request.getAmount()));
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());

        return mapToResponse(
                expenseRepository.save(expense)
        );
    }

    @Override
    public void deleteExpense(String id, String email) {
        logger.info("Deleting expense {} for user {}", id, email);
        Expense expense = expenseRepository
                .findByIdAndUserEmail(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found"));
        logger.info("Expense {} deleted successfully", id);
        expenseRepository.delete(expense);
    }

    @Override
    public List<ExpenseResponse> searchExpenses(
            String email,
            String category,
            LocalDate date,
            Double minAmount,
            Double maxAmount) {

        logger.info(
                "Searching expenses | user={} category={} date={} min={} max={}",
                email,
                category,
                date,
                minAmount,
                maxAmount
        );

        Query query = new Query();

        query.addCriteria(Criteria.where("userEmail").is(email));

        if (category != null && !category.isBlank()) {
            query.addCriteria(
                    Criteria.where("category")
                            .regex("^" + category + "$", "i")
            );
        }

        if (date != null) {
            query.addCriteria(
                    Criteria.where("date").is(date)
            );
        }

        if (minAmount != null && maxAmount != null) {
            query.addCriteria(
                    Criteria.where("amount")
                            .gte(minAmount)
                            .lte(maxAmount)
            );
        }

        List<ExpenseResponse> expenses = mongoTemplate
                .find(query, Expense.class)
                .stream()
                .map(this::mapToResponse)
                .toList();

        logger.info("Search returned {} expense(s)", expenses.size());

        return expenses;
    }

    @Override
    public List<ExpenseResponse> filterExpenses(
            String email,
            String type,
            LocalDate startDate,
            LocalDate endDate) {

        logger.info(
                "Filtering expenses | user={} type={} start={} end={}",
                email,
                type,
                startDate,
                endDate
        );

        Query query = new Query();

        query.addCriteria(
                Criteria.where("userEmail").is(email)
        );

        LocalDate today = LocalDate.now();

        if (type != null && !type.isBlank()) {

            switch (type.toLowerCase()) {

                case "weekly" -> {
                    LocalDate weekStart = today.minusDays(6);

                    query.addCriteria(
                            Criteria.where("date")
                                    .gte(weekStart)
                                    .lte(today)
                    );
                }

                case "monthly" -> {
                    LocalDate monthStart = today.withDayOfMonth(1);

                    query.addCriteria(
                            Criteria.where("date")
                                    .gte(monthStart)
                                    .lte(today)
                    );
                }
            }
        }

        if (startDate != null && endDate != null) {
            query.addCriteria(
                    Criteria.where("date")
                            .gte(startDate)
                            .lte(endDate)
            );
        }

        List<ExpenseResponse> expenses = mongoTemplate
                .find(query, Expense.class)
                .stream()
                .map(this::mapToResponse)
                .toList();

        logger.info("Filter returned {} expense(s)", expenses.size());

        return expenses;
    }
}
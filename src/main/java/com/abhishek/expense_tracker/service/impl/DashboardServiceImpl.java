package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.DashboardSummaryDto;
import com.abhishek.expense_tracker.dto.SummaryResult;
import com.abhishek.expense_tracker.entity.Expense;
import com.abhishek.expense_tracker.repository.ExpenseRepository;
import com.abhishek.expense_tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MongoTemplate mongoTemplate;
    @Override
    public DashboardSummaryDto getSummary(String email) {

        MatchOperation match = Aggregation.match(
                Criteria.where("userEmail").is(email)
        );

        GroupOperation group = Aggregation.group()
                .sum("amount").as("totalExpenses")
                .count().as("totalTransactions");

        Aggregation aggregation =
                Aggregation.newAggregation(match, group);

        AggregationResults<SummaryResult> results =
                mongoTemplate.aggregate(
                        aggregation,
                        Expense.class,
                        SummaryResult.class
                );

        SummaryResult summary = results.getUniqueMappedResult();

        if (summary == null) {
            summary = new SummaryResult();
            summary.setTotalExpenses(0.0);
            summary.setTotalTransactions(0L);
        }

        return new DashboardSummaryDto(
                summary.getTotalExpenses(),
                summary.getTotalTransactions(),
                0.0
        );
    }
   }
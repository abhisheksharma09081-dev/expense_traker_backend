package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.CategoryExpenseDto;
import com.abhishek.expense_tracker.dto.IncomeVsExpenseDto;
import com.abhishek.expense_tracker.dto.MonthlyExpenseDto;
import com.abhishek.expense_tracker.entity.Expense;
import com.abhishek.expense_tracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<MonthlyExpenseDto> getMonthlyExpenses(String email) {

        MatchOperation match = Aggregation.match(
                org.springframework.data.mongodb.core.query.Criteria
                        .where("userEmail").is(email)
        );

        ProjectionOperation project = Aggregation.project()
                .and("amount").as("amount")
                .andExpression("month(date)").as("month");
        GroupOperation group = Aggregation.group("month")
                .sum("amount").as("total");

        SortOperation sort = Aggregation.sort(Sort.by("_id"));

        Aggregation aggregation = Aggregation.newAggregation(
                match,
                project,
                group,
                sort
        );

        AggregationResults<MonthlyExpenseResult> results =
                mongoTemplate.aggregate(
                        aggregation,
                        Expense.class,
                        MonthlyExpenseResult.class
                );

        List<MonthlyExpenseDto> response = new ArrayList<>();

        for (MonthlyExpenseResult result : results.getMappedResults()) {

            response.add(
                    new MonthlyExpenseDto(
                            Month.of(result.getId()).name(),
                            result.getTotal()
                    )
            );
        }

        return response;
    }

    @Override
    public List<CategoryExpenseDto> getCategoryExpenses(String email) {

        MatchOperation match = Aggregation.match(
                Criteria.where("userEmail").is(email));

        GroupOperation group = Aggregation.group("category")
                .sum("amount").as("total");

        SortOperation sort = Aggregation.sort(
                Sort.by(Sort.Direction.DESC, "total"));

        Aggregation aggregation = Aggregation.newAggregation(
                match,
                group,
                sort
        );

        AggregationResults<CategoryExpenseResult> results =
                mongoTemplate.aggregate(
                        aggregation,
                        Expense.class,
                        CategoryExpenseResult.class
                );

        return results.getMappedResults()
                .stream()
                .map(r -> new CategoryExpenseDto(
                        r.getId(),
                        r.getTotal()))
                .toList();
    }

    @Override
    public IncomeVsExpenseDto getIncomeVsExpense(String email) {

        MatchOperation incomeMatch = Aggregation.match(
                Criteria.where("userEmail").is(email));

        GroupOperation incomeGroup = Aggregation.group()
                .sum("amount").as("total");

        Aggregation incomeAggregation =
                Aggregation.newAggregation(
                        incomeMatch,
                        incomeGroup
                );

        AggregationResults<TotalResult> incomeResult =
                mongoTemplate.aggregate(
                        incomeAggregation,
                        "incomes",
                        TotalResult.class
                );

        double totalIncome = incomeResult.getUniqueMappedResult() != null
                ? incomeResult.getUniqueMappedResult().getTotal()
                : 0;

        MatchOperation expenseMatch = Aggregation.match(
                Criteria.where("userEmail").is(email));

        GroupOperation expenseGroup = Aggregation.group()
                .sum("amount").as("total");

        Aggregation expenseAggregation =
                Aggregation.newAggregation(
                        expenseMatch,
                        expenseGroup
                );

        AggregationResults<TotalResult> expenseResult =
                mongoTemplate.aggregate(
                        expenseAggregation,
                        "expenses",
                        TotalResult.class
                );

        double totalExpense = expenseResult.getUniqueMappedResult() != null
                ? expenseResult.getUniqueMappedResult().getTotal()
                : 0;

        return new IncomeVsExpenseDto(
                totalIncome,
                totalExpense,
                totalIncome - totalExpense
        );
    }

    private static class CategoryExpenseResult {

        private String id;

        private Double total;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }
    }

    private static class MonthlyExpenseResult {

        private Integer id;

        private Double total;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }
    }
    private static class TotalResult {

        private Double total;

        public Double getTotal() {
            return total == null ? 0 : total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }
    }
}
package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.DashboardSummaryDto;
import com.abhishek.expense_tracker.dto.SummaryResult;
import com.abhishek.expense_tracker.entity.Expense;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.bson.Document;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void shouldReturnDashboardSummary() {

        SummaryResult summary = new SummaryResult();
        summary.setTotalExpenses(5000.0);
        summary.setTotalTransactions(10L);

        AggregationResults<SummaryResult> results =
                new AggregationResults<>(
                        List.of(summary),
                        new Document()
                );

        when(mongoTemplate.aggregate(
                any(Aggregation.class),
                eq(Expense.class),
                eq(SummaryResult.class)
        )).thenReturn(results);

        DashboardSummaryDto dto =
                dashboardService.getSummary("abc@gmail.com");

        assertNotNull(dto);
        assertEquals(5000.0, dto.getTotalExpenses());
        assertEquals(10L, dto.getTotalTransactions());

        verify(mongoTemplate).aggregate(
                any(Aggregation.class),
                eq(Expense.class),
                eq(SummaryResult.class)
        );
    }

    @Test
    void shouldReturnEmptySummaryWhenNoData() {

        AggregationResults<SummaryResult> results =
                new AggregationResults<>(
                        List.of(),
                        new Document()
                );

        when(mongoTemplate.aggregate(
                any(Aggregation.class),
                eq(Expense.class),
                eq(SummaryResult.class)
        )).thenReturn(results);

        DashboardSummaryDto dto =
                dashboardService.getSummary("abc@gmail.com");

        assertEquals(0.0, dto.getTotalExpenses());
        assertEquals(0L, dto.getTotalTransactions());
    }

}
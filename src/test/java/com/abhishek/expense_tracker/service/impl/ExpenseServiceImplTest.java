package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.ExpenseRequest;
import com.abhishek.expense_tracker.dto.ExpenseResponse;
import com.abhishek.expense_tracker.entity.Expense;
import com.abhishek.expense_tracker.exception.ResourceNotFoundException;
import com.abhishek.expense_tracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Expense expense;
    private ExpenseRequest request;

    @BeforeEach
    void setup() {

        request = ExpenseRequest.builder()
                .title("Food")
                .category("Food")
                .amount(500.0)
                .description("Pizza")
                .date(LocalDate.of(2026,7,13))
                .build();

        expense = Expense.builder()
                .id("1")
                .title("Food")
                .category("Food")
                .amount(BigDecimal.valueOf(500))
                .description("Pizza")
                .date(LocalDate.of(2026,7,13))
                .userEmail("abc@gmail.com")
                .build();
    }

    @Test
    void shouldSaveExpense() {

        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(expense);

        ExpenseResponse response =
                expenseService.saveExpense(
                        request,
                        "abc@gmail.com"
                );

        assertNotNull(response);

        assertEquals("Food", response.getTitle());

        assertEquals("Food", response.getCategory());

        assertEquals(500.0, response.getAmount());

        assertEquals("Pizza", response.getDescription());

        verify(expenseRepository).save(any(Expense.class));
    }
    @Test
    void shouldUpdateExpense() {

        when(expenseRepository.findByIdAndUserEmail("1", "abc@gmail.com"))
                .thenReturn(java.util.Optional.of(expense));

        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(expense);

        ExpenseResponse response = expenseService.updateExpense(
                "1",
                "abc@gmail.com",
                request
        );

        assertNotNull(response);

        assertEquals("Food", response.getTitle());
        assertEquals("Food", response.getCategory());
        assertEquals(500.0, response.getAmount());

        verify(expenseRepository).findByIdAndUserEmail("1", "abc@gmail.com");
        verify(expenseRepository).save(any(Expense.class));
    }
    @Test
    void shouldGetUserExpenses() {

        Page<Expense> page = new PageImpl<>(List.of(expense));

        when(expenseRepository.findByUserEmail(
                anyString(),
                any(Pageable.class)))
                .thenReturn(page);

        Page<ExpenseResponse> response =
                expenseService.getUserExpenses(
                        "abc@gmail.com",
                        0,
                        10,
                        "date",
                        "desc"
                );

        assertEquals(1, response.getTotalElements());

        assertEquals("Food",
                response.getContent().get(0).getTitle());

        verify(expenseRepository)
                .findByUserEmail(anyString(), any(Pageable.class));
    }
    @Test
    void shouldDeleteExpense() {

        when(expenseRepository.findByIdAndUserEmail(
                "1",
                "abc@gmail.com"))
                .thenReturn(Optional.of(expense));

        expenseService.deleteExpense(
                "1",
                "abc@gmail.com"
        );

        verify(expenseRepository).delete(expense);
    }
    @Test
    void shouldSearchExpenses() {

        when(mongoTemplate.find(any(Query.class), eq(Expense.class)))
                .thenReturn(List.of(expense));

        List<ExpenseResponse> response =
                expenseService.searchExpenses(
                        "abc@gmail.com",
                        "Food",
                        null,
                        null,
                        null
                );

        assertEquals(1, response.size());

        assertEquals("Food",
                response.get(0).getCategory());

        verify(mongoTemplate)
                .find(any(Query.class), eq(Expense.class));
    }
    @Test
    void shouldReturnEmptyListWhenSearchHasNoResult() {

        when(mongoTemplate.find(any(Query.class), eq(Expense.class)))
                .thenReturn(List.of());

        List<ExpenseResponse> response =
                expenseService.searchExpenses(
                        "abc@gmail.com",
                        "Shopping",
                        null,
                        null,
                        null
                );

        assertTrue(response.isEmpty());
    }
    @Test
    void shouldThrowExceptionWhenDeletingInvalidExpense() {

        when(expenseRepository.findByIdAndUserEmail(
                anyString(),
                anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.deleteExpense(
                        "1",
                        "abc@gmail.com"
                )
        );
    }
    @Test
    void shouldThrowExceptionWhenExpenseNotFoundForUpdate() {

        when(expenseRepository.findByIdAndUserEmail(
                anyString(),
                anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.updateExpense(
                        "1",
                        "abc@gmail.com",
                        request
                )
        );
    }
    @Test
    void shouldThrowExceptionWhenSaveFails() {

        when(expenseRepository.save(any()))
                .thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () ->
                expenseService.saveExpense(request, "abc@gmail.com"));
    }
    @Test
    void shouldFilterExpenses() {

        when(mongoTemplate.find(any(Query.class), eq(Expense.class)))
                .thenReturn(List.of(expense));

        List<ExpenseResponse> response =
                expenseService.filterExpenses(
                        "abc@gmail.com",
                        "monthly",
                        null,
                        null
                );

        assertEquals(1, response.size());

        verify(mongoTemplate)
                .find(any(Query.class), eq(Expense.class));
    }
    @Test
    void shouldReturnEmptyListWhenNoFilteredExpenseFound() {

        when(mongoTemplate.find(any(Query.class), eq(Expense.class)))
                .thenReturn(List.of());

        List<ExpenseResponse> response =
                expenseService.filterExpenses(
                        "abc@gmail.com",
                        "weekly",
                        null,
                        null
                );

        assertTrue(response.isEmpty());
    }
}
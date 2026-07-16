package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.IncomeRequest;
import com.abhishek.expense_tracker.dto.IncomeResponse;
import com.abhishek.expense_tracker.dto.PageResponse;
import com.abhishek.expense_tracker.entity.Income;
import com.abhishek.expense_tracker.exception.ResourceNotFoundException;
import com.abhishek.expense_tracker.repository.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceImplTest {

    @Mock
    private IncomeRepository incomeRepository;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private Income income;
    private IncomeRequest request;

    @BeforeEach
    void setup() {

        request = new IncomeRequest();
        request.setSource("Salary");
        request.setAmount(50000.0);
        request.setDate(LocalDate.of(2026, 7, 13));
        request.setDescription("Monthly Salary");

        income = Income.builder()
                .id("1")
                .source("Salary")
                .amount(50000.0)
                .date(LocalDate.of(2026, 7, 13))
                .description("Monthly Salary")
                .userEmail("abc@gmail.com")
                .build();
    }

    @Test
    void shouldSaveIncome() {

        when(incomeRepository.save(any(Income.class)))
                .thenReturn(income);

        IncomeResponse response =
                incomeService.saveIncome(
                        "abc@gmail.com",
                        request
                );

        assertNotNull(response);
        assertEquals("Salary", response.getSource());
        assertEquals(50000.0, response.getAmount());
        assertEquals("Monthly Salary", response.getDescription());

        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    void shouldGetIncomeById() {

        when(incomeRepository.findByIdAndUserEmail(
                "1",
                "abc@gmail.com"))
                .thenReturn(Optional.of(income));

        IncomeResponse response =
                incomeService.getIncomeById(
                        "1",
                        "abc@gmail.com"
                );

        assertNotNull(response);
        assertEquals("Salary", response.getSource());
        assertEquals(50000.0, response.getAmount());

        verify(incomeRepository)
                .findByIdAndUserEmail("1", "abc@gmail.com");
    }

    @Test
    void shouldThrowExceptionWhenIncomeNotFound() {

        when(incomeRepository.findByIdAndUserEmail(
                "1",
                "abc@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> incomeService.getIncomeById(
                        "1",
                        "abc@gmail.com")
        );

        verify(incomeRepository)
                .findByIdAndUserEmail("1", "abc@gmail.com");
    }

    @Test
    void shouldUpdateIncome() {

        when(incomeRepository.findByIdAndUserEmail(
                "1",
                "abc@gmail.com"))
                .thenReturn(Optional.of(income));

        when(incomeRepository.save(any(Income.class)))
                .thenReturn(income);

        IncomeResponse response =
                incomeService.updateIncome(
                        "1",
                        "abc@gmail.com",
                        request
                );

        assertNotNull(response);
        assertEquals("Salary", response.getSource());
        assertEquals(50000.0, response.getAmount());

        verify(incomeRepository)
                .findByIdAndUserEmail("1", "abc@gmail.com");

        verify(incomeRepository)
                .save(any(Income.class));
    }

    @Test
    void shouldDeleteIncome() {

        when(incomeRepository.findByIdAndUserEmail(
                "1",
                "abc@gmail.com"))
                .thenReturn(Optional.of(income));

        incomeService.deleteIncome(
                "1",
                "abc@gmail.com"
        );

        verify(incomeRepository)
                .delete(income);
    }

    @Test
    void shouldGetAllUserIncomes() {

        Page<Income> page = new PageImpl<>(
                List.of(income),
                PageRequest.of(0, 10),
                1
        );

        when(incomeRepository.findByUserEmail(
                eq("abc@gmail.com"),
                any(Pageable.class)))
                .thenReturn(page);

        PageResponse<IncomeResponse> response =
                incomeService.getUserIncomes(
                        "abc@gmail.com",
                        0,
                        10,
                        "date",
                        "desc"
                );

        assertNotNull(response);

        assertEquals(1, response.getContent().size());

        assertEquals(
                "Salary",
                response.getContent().get(0).getSource()
        );

        verify(incomeRepository)
                .findByUserEmail(
                        eq("abc@gmail.com"),
                        any(Pageable.class));
    }
}
package com.abhishek.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private String id;

    private String title;

    private String category;

    private Double amount;

    private LocalDate date;

    private String description;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
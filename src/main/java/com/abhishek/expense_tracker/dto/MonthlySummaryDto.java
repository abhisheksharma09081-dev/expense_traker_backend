package com.abhishek.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MonthlySummaryDto {

    private String month;

    private Double total;
}
package com.abhishek.expense_tracker.service;

import com.abhishek.expense_tracker.dto.DashboardSummaryDto;

public interface DashboardService {

    DashboardSummaryDto getSummary(String email);
}
package com.abhishek.expense_tracker.service;

import com.abhishek.expense_tracker.dto.AuthResponse;
import com.abhishek.expense_tracker.dto.LoginRequest;
import com.abhishek.expense_tracker.dto.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
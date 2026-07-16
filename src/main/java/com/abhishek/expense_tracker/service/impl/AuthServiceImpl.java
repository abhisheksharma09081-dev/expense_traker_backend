package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.AuthResponse;
import com.abhishek.expense_tracker.dto.LoginRequest;
import com.abhishek.expense_tracker.dto.RegisterRequest;
import com.abhishek.expense_tracker.entity.Role;
import com.abhishek.expense_tracker.entity.User;
import com.abhishek.expense_tracker.exception.ResourceNotFoundException;
import com.abhishek.expense_tracker.repository.UserRepository;
import com.abhishek.expense_tracker.security.JwtService;
import com.abhishek.expense_tracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {

        logger.info("Register request received for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {

            logger.warn("Registration failed. Email already exists: {}", request.getEmail());

            throw new ResourceNotFoundException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        logger.info("User registered successfully: {}", user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        logger.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {

                    logger.warn("Login failed. Email not found: {}", request.getEmail());

                    return new ResourceNotFoundException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            logger.warn("Login failed. Invalid password for email: {}", request.getEmail());

            throw new ResourceNotFoundException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        logger.info("User logged in successfully: {}", user.getEmail());

        return new AuthResponse(token, "Login successful");
    }
}
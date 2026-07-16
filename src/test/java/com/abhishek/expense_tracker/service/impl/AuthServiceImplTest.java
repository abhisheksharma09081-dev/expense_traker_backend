package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.AuthResponse;
import com.abhishek.expense_tracker.dto.LoginRequest;
import com.abhishek.expense_tracker.dto.RegisterRequest;
import com.abhishek.expense_tracker.entity.Role;
import com.abhishek.expense_tracker.entity.User;
import com.abhishek.expense_tracker.exception.ResourceNotFoundException;
import com.abhishek.expense_tracker.repository.UserRepository;
import com.abhishek.expense_tracker.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setup() {

        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Abhishek");
        registerRequest.setLastName("Sharma");
        registerRequest.setEmail("abc@gmail.com");
        registerRequest.setPassword("123456");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("abc@gmail.com");
        loginRequest.setPassword("123456");

        user = User.builder()
                .id("1")
                .firstName("Abhishek")
                .lastName("Sharma")
                .email("abc@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn("encodedPassword");

        authService.register(registerRequest);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(true);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> authService.register(registerRequest)
                );

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateToken(user.getEmail()))
                .thenReturn("jwt-token");

        AuthResponse response =
                authService.login(loginRequest);

        assertNotNull(response);

        assertEquals("jwt-token", response.getToken());

        assertEquals("Login successful", response.getMessage());

        verify(jwtService).generateToken(user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()))
                .thenReturn(false);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(jwtService, never()).generateToken(anyString());
    }
}
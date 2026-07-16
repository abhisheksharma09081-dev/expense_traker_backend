package com.abhishek.expense_tracker.controller;

import com.abhishek.expense_tracker.dto.ExpenseRequest;
import com.abhishek.expense_tracker.dto.ExpenseResponse;
import com.abhishek.expense_tracker.dto.PageResponse;
import com.abhishek.expense_tracker.exception.GlobalExceptionHandler;
import com.abhishek.expense_tracker.exception.ResourceNotFoundException;
import com.abhishek.expense_tracker.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ExpenseController expenseController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ExpenseRequest expenseRequest;
    private ExpenseResponse expenseResponse;
    private Page<ExpenseResponse> expensePage;
    private PageResponse<ExpenseResponse> pageResponse;

    private final String TEST_USER_EMAIL = "test@example.com";
    private final String TEST_EXPENSE_ID = "123e4567-e89b-12d3-a456-426614174000";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(expenseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup test data
        expenseRequest = ExpenseRequest.builder()
                .title("Lunch")
                .category("Food")
                .amount(25.50)
                .date(LocalDate.now())
                .description("Team lunch at restaurant")
                .build();

        expenseResponse = ExpenseResponse.builder()
                .id(TEST_EXPENSE_ID)
                .title("Lunch")
                .category("Food")
                .amount(25.50)
                .date(LocalDate.now())
                .description("Team lunch at restaurant")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Setup page response
        List<ExpenseResponse> expenseList = List.of(expenseResponse);
        Pageable pageable = PageRequest.of(0, 10);
        expensePage = new PageImpl<>(expenseList, pageable, 1);

        pageResponse = new PageResponse<>(
                expenseList,
                0,
                10,
                1L,
                1,
                true,
                true
        );

        // Mock authentication
        when(authentication.getName()).thenReturn(TEST_USER_EMAIL);
    }

    // ==================== POST /api/expenses ====================

    @Test
    void addExpense_ShouldReturnCreatedExpense() throws Exception {
        // Given
        when(expenseService.saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL)))
                .thenReturn(expenseResponse);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_EXPENSE_ID))
                .andExpect(jsonPath("$.title").value("Lunch"))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.description").value("Team lunch at restaurant"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(expenseService, times(1))
                .saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL));
    }

    @Test
    void addExpense_WithNullTitle_ShouldReturnBadRequest() throws Exception {
        // Given
        expenseRequest.setTitle(null);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    @Test
    void addExpense_WithBlankTitle_ShouldReturnBadRequest() throws Exception {
        // Given
        expenseRequest.setTitle("");

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    @Test
    void addExpense_WithNullCategory_ShouldReturnBadRequest() throws Exception {
        // Given
        expenseRequest.setCategory(null);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.category").value("Category is required"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    @Test
    void addExpense_WithBlankCategory_ShouldReturnBadRequest() throws Exception {
        // Given
        expenseRequest.setCategory("");

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.category").value("Category is required"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    @Test
    void addExpense_WithNullAmount_ShouldReturnBadRequest() throws Exception {
        // Given
        expenseRequest.setAmount(null);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").value("Amount is required"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    @Test
    void addExpense_WithNegativeAmount_ShouldReturnBadRequest() throws Exception {
        // Given
        expenseRequest.setAmount(-25.50);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").value("Amount must be greater than 0"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    @Test
    void addExpense_WithZeroAmount_ShouldReturnBadRequest() throws Exception {
        // Given
        expenseRequest.setAmount(0.0);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").value("Amount must be greater than 0"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    @Test
    void addExpense_WithNullDate_ShouldReturnBadRequest() throws Exception {
        // Given
        expenseRequest.setDate(null);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.date").value("Date is required"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    @Test
    void addExpense_WithMultipleValidationErrors_ShouldReturnAllErrors() throws Exception {
        // Given
        ExpenseRequest invalidRequest = ExpenseRequest.builder()
                .title("")
                .category("")
                .amount(-100.0)
                .date(null)
                .build();

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"))
                .andExpect(jsonPath("$.errors.category").value("Category is required"))
                .andExpect(jsonPath("$.errors.amount").value("Amount must be greater than 0"))
                .andExpect(jsonPath("$.errors.date").value("Date is required"));

        verify(expenseService, never()).saveExpense(any(ExpenseRequest.class), anyString());
    }

    // ==================== GET /api/expenses ====================

    @Test
    void getExpenses_ShouldReturnPageResponse() throws Exception {
        // Given
        when(expenseService.getUserExpenses(
                eq(TEST_USER_EMAIL), eq(0), eq(10), eq("date"), eq("desc")))
                .thenReturn(expensePage);

        // When & Then
        mockMvc.perform(get("/api/expenses")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "date")
                        .param("direction", "desc")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(TEST_EXPENSE_ID))
                .andExpect(jsonPath("$.content[0].title").value("Lunch"))
                .andExpect(jsonPath("$.content[0].category").value("Food"))
                .andExpect(jsonPath("$.content[0].amount").value(25.50))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        verify(expenseService, times(1))
                .getUserExpenses(eq(TEST_USER_EMAIL), eq(0), eq(10), eq("date"), eq("desc"));
    }

    @Test
    void getExpenses_WithDefaultParameters_ShouldUseDefaults() throws Exception {
        // Given
        when(expenseService.getUserExpenses(
                eq(TEST_USER_EMAIL), eq(0), eq(10), eq("date"), eq("desc")))
                .thenReturn(expensePage);

        // When & Then
        mockMvc.perform(get("/api/expenses")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(expenseService, times(1))
                .getUserExpenses(eq(TEST_USER_EMAIL), eq(0), eq(10), eq("date"), eq("desc"));
    }

    @Test
    void getExpenses_WithCustomParameters_ShouldUseCustomValues() throws Exception {
        // Given
        when(expenseService.getUserExpenses(
                eq(TEST_USER_EMAIL), eq(2), eq(20), eq("amount"), eq("asc")))
                .thenReturn(expensePage);

        // When & Then
        mockMvc.perform(get("/api/expenses")
                        .param("page", "2")
                        .param("size", "20")
                        .param("sortBy", "amount")
                        .param("direction", "asc")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(expenseService, times(1))
                .getUserExpenses(eq(TEST_USER_EMAIL), eq(2), eq(20), eq("amount"), eq("asc"));
    }

    @Test
    void getExpenses_WithEmptyResult_ShouldReturnEmptyPage() throws Exception {
        // Given
        Page<ExpenseResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(expenseService.getUserExpenses(
                eq(TEST_USER_EMAIL), eq(0), eq(10), eq("date"), eq("desc")))
                .thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/expenses")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        verify(expenseService, times(1))
                .getUserExpenses(eq(TEST_USER_EMAIL), eq(0), eq(10), eq("date"), eq("desc"));
    }

    // ==================== PUT /api/expenses/{id} ====================

    @Test
    void updateExpense_ShouldReturnUpdatedExpense() throws Exception {
        // Given
        ExpenseRequest updateRequest = ExpenseRequest.builder()
                .title("Dinner")
                .category("Food")
                .amount(45.75)
                .date(LocalDate.now())
                .description("Dinner at Italian restaurant")
                .build();

        ExpenseResponse updatedResponse = ExpenseResponse.builder()
                .id(TEST_EXPENSE_ID)
                .title("Dinner")
                .category("Food")
                .amount(45.75)
                .date(LocalDate.now())
                .description("Dinner at Italian restaurant")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(expenseService.updateExpense(eq(TEST_EXPENSE_ID), eq(TEST_USER_EMAIL), any(ExpenseRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/expenses/{id}", TEST_EXPENSE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_EXPENSE_ID))
                .andExpect(jsonPath("$.title").value("Dinner"))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.amount").value(45.75))
                .andExpect(jsonPath("$.description").value("Dinner at Italian restaurant"));

        verify(expenseService, times(1))
                .updateExpense(eq(TEST_EXPENSE_ID), eq(TEST_USER_EMAIL), any(ExpenseRequest.class));
    }

    @Test
    void updateExpense_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        ExpenseRequest invalidRequest = ExpenseRequest.builder()
                .title("")
                .category("")
                .amount(-100.0)
                .date(null)
                .build();

        // When & Then
        mockMvc.perform(put("/api/expenses/{id}", TEST_EXPENSE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"))
                .andExpect(jsonPath("$.errors.category").value("Category is required"))
                .andExpect(jsonPath("$.errors.amount").value("Amount must be greater than 0"))
                .andExpect(jsonPath("$.errors.date").value("Date is required"));

        verify(expenseService, never())
                .updateExpense(anyString(), anyString(), any(ExpenseRequest.class));
    }

    @Test
    void updateExpense_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        when(expenseService.updateExpense(eq(nonExistentId), eq(TEST_USER_EMAIL), any(ExpenseRequest.class)))
                .thenThrow(new ResourceNotFoundException("Expense not found"));

        // When & Then
        mockMvc.perform(put("/api/expenses/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Expense not found"));

        verify(expenseService, times(1))
                .updateExpense(eq(nonExistentId), eq(TEST_USER_EMAIL), any(ExpenseRequest.class));
    }

    // ==================== DELETE /api/expenses/{id} ====================

    @Test
    void deleteExpense_ShouldReturnSuccessMessage() throws Exception {
        // Given
        doNothing().when(expenseService).deleteExpense(eq(TEST_EXPENSE_ID), eq(TEST_USER_EMAIL));

        // When & Then
        mockMvc.perform(delete("/api/expenses/{id}", TEST_EXPENSE_ID)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense deleted successfully"));

        verify(expenseService, times(1))
                .deleteExpense(eq(TEST_EXPENSE_ID), eq(TEST_USER_EMAIL));
    }

    @Test
    void deleteExpense_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        doThrow(new ResourceNotFoundException("Expense not found"))
                .when(expenseService).deleteExpense(eq(nonExistentId), eq(TEST_USER_EMAIL));

        // When & Then
        mockMvc.perform(delete("/api/expenses/{id}", nonExistentId)
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Expense not found"));

        verify(expenseService, times(1))
                .deleteExpense(eq(nonExistentId), eq(TEST_USER_EMAIL));
    }

    // ==================== GET /api/expenses/search ====================

    @Test
    void searchExpenses_WithAllFilters_ShouldReturnFilteredList() throws Exception {
        // Given
        List<ExpenseResponse> searchResults = List.of(expenseResponse);
        when(expenseService.searchExpenses(
                eq(TEST_USER_EMAIL),
                eq("Food"),
                any(LocalDate.class),
                eq(10.0),
                eq(50.0)))
                .thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/search")
                        .param("category", "Food")
                        .param("date", LocalDate.now().toString())
                        .param("minAmount", "10.0")
                        .param("maxAmount", "50.0")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(TEST_EXPENSE_ID))
                .andExpect(jsonPath("$[0].title").value("Lunch"))
                .andExpect(jsonPath("$[0].category").value("Food"))
                .andExpect(jsonPath("$[0].amount").value(25.50));

        verify(expenseService, times(1))
                .searchExpenses(eq(TEST_USER_EMAIL), eq("Food"), any(LocalDate.class), eq(10.0), eq(50.0));
    }

    @Test
    void searchExpenses_WithOnlyCategory_ShouldReturnFilteredList() throws Exception {
        // Given
        List<ExpenseResponse> searchResults = List.of(expenseResponse);
        when(expenseService.searchExpenses(
                eq(TEST_USER_EMAIL),
                eq("Food"),
                isNull(),
                isNull(),
                isNull()))
                .thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/search")
                        .param("category", "Food")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("Food"));

        verify(expenseService, times(1))
                .searchExpenses(eq(TEST_USER_EMAIL), eq("Food"), isNull(), isNull(), isNull());
    }

    @Test
    void searchExpenses_WithOnlyDate_ShouldReturnFilteredList() throws Exception {
        // Given
        LocalDate searchDate = LocalDate.now();
        List<ExpenseResponse> searchResults = List.of(expenseResponse);
        when(expenseService.searchExpenses(
                eq(TEST_USER_EMAIL),
                isNull(),
                eq(searchDate),
                isNull(),
                isNull()))
                .thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/search")
                        .param("date", searchDate.toString())
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].date").exists());

        verify(expenseService, times(1))
                .searchExpenses(eq(TEST_USER_EMAIL), isNull(), eq(searchDate), isNull(), isNull());
    }

    @Test
    void searchExpenses_WithAmountRange_ShouldReturnFilteredList() throws Exception {
        // Given
        List<ExpenseResponse> searchResults = List.of(expenseResponse);
        when(expenseService.searchExpenses(
                eq(TEST_USER_EMAIL),
                isNull(),
                isNull(),
                eq(20.0),
                eq(30.0)))
                .thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/search")
                        .param("minAmount", "20.0")
                        .param("maxAmount", "30.0")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].amount").value(25.50));

        verify(expenseService, times(1))
                .searchExpenses(eq(TEST_USER_EMAIL), isNull(), isNull(), eq(20.0), eq(30.0));
    }

    @Test
    void searchExpenses_WithNoFilters_ShouldReturnAllExpenses() throws Exception {
        // Given
        List<ExpenseResponse> searchResults = List.of(expenseResponse);
        when(expenseService.searchExpenses(
                eq(TEST_USER_EMAIL),
                isNull(),
                isNull(),
                isNull(),
                isNull()))
                .thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/search")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(TEST_EXPENSE_ID));

        verify(expenseService, times(1))
                .searchExpenses(eq(TEST_USER_EMAIL), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void searchExpenses_WithEmptyResults_ShouldReturnEmptyList() throws Exception {
        // Given
        when(expenseService.searchExpenses(
                eq(TEST_USER_EMAIL),
                eq("NonExistentCategory"),
                isNull(),
                isNull(),
                isNull()))
                .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/expenses/search")
                        .param("category", "NonExistentCategory")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(expenseService, times(1))
                .searchExpenses(eq(TEST_USER_EMAIL), eq("NonExistentCategory"), isNull(), isNull(), isNull());
    }

    // ==================== GET /api/expenses/filter ====================

    @Test
    void filterExpenses_WithWeeklyType_ShouldReturnFilteredList() throws Exception {
        // Given
        List<ExpenseResponse> filteredResults = List.of(expenseResponse);
        when(expenseService.filterExpenses(
                eq(TEST_USER_EMAIL),
                eq("weekly"),
                isNull(),
                isNull()))
                .thenReturn(filteredResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/filter")
                        .param("type", "weekly")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(TEST_EXPENSE_ID))
                .andExpect(jsonPath("$[0].title").value("Lunch"));

        verify(expenseService, times(1))
                .filterExpenses(eq(TEST_USER_EMAIL), eq("weekly"), isNull(), isNull());
    }

    @Test
    void filterExpenses_WithMonthlyType_ShouldReturnFilteredList() throws Exception {
        // Given
        List<ExpenseResponse> filteredResults = List.of(expenseResponse);
        when(expenseService.filterExpenses(
                eq(TEST_USER_EMAIL),
                eq("monthly"),
                isNull(),
                isNull()))
                .thenReturn(filteredResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/filter")
                        .param("type", "monthly")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(TEST_EXPENSE_ID));

        verify(expenseService, times(1))
                .filterExpenses(eq(TEST_USER_EMAIL), eq("monthly"), isNull(), isNull());
    }

    @Test
    void filterExpenses_WithDateRange_ShouldReturnFilteredList() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<ExpenseResponse> filteredResults = List.of(expenseResponse);
        when(expenseService.filterExpenses(
                eq(TEST_USER_EMAIL),
                isNull(),
                eq(startDate),
                eq(endDate)))
                .thenReturn(filteredResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/filter")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(TEST_EXPENSE_ID));

        verify(expenseService, times(1))
                .filterExpenses(eq(TEST_USER_EMAIL), isNull(), eq(startDate), eq(endDate));
    }

    @Test
    void filterExpenses_WithTypeAndDateRange_ShouldReturnFilteredList() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<ExpenseResponse> filteredResults = List.of(expenseResponse);
        when(expenseService.filterExpenses(
                eq(TEST_USER_EMAIL),
                eq("weekly"),
                eq(startDate),
                eq(endDate)))
                .thenReturn(filteredResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/filter")
                        .param("type", "weekly")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(TEST_EXPENSE_ID));

        verify(expenseService, times(1))
                .filterExpenses(eq(TEST_USER_EMAIL), eq("weekly"), eq(startDate), eq(endDate));
    }

    @Test
    void filterExpenses_WithNoFilters_ShouldReturnAllExpenses() throws Exception {
        // Given
        List<ExpenseResponse> filteredResults = List.of(expenseResponse);
        when(expenseService.filterExpenses(
                eq(TEST_USER_EMAIL),
                isNull(),
                isNull(),
                isNull()))
                .thenReturn(filteredResults);

        // When & Then
        mockMvc.perform(get("/api/expenses/filter")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(TEST_EXPENSE_ID));

        verify(expenseService, times(1))
                .filterExpenses(eq(TEST_USER_EMAIL), isNull(), isNull(), isNull());
    }

    @Test
    void filterExpenses_WithEmptyResults_ShouldReturnEmptyList() throws Exception {
        // Given
        when(expenseService.filterExpenses(
                eq(TEST_USER_EMAIL),
                eq("invalid"),
                isNull(),
                isNull()))
                .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/expenses/filter")
                        .param("type", "invalid")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(expenseService, times(1))
                .filterExpenses(eq(TEST_USER_EMAIL), eq("invalid"), isNull(), isNull());
    }

    // ==================== Edge Cases ====================

    @Test
    void addExpense_WithFutureDate_ShouldSucceed() throws Exception {
        // Given
        expenseRequest.setDate(LocalDate.now().plusDays(30));
        when(expenseService.saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL)))
                .thenReturn(expenseResponse);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(expenseService, times(1))
                .saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL));
    }

    @Test
    void addExpense_WithVeryLargeAmount_ShouldSucceed() throws Exception {
        // Given
        expenseRequest.setAmount(999999999.99);
        when(expenseService.saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL)))
                .thenReturn(expenseResponse);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(expenseService, times(1))
                .saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL));
    }

    @Test
    void addExpense_WithEmptyDescription_ShouldSucceed() throws Exception {
        // Given
        expenseRequest.setDescription(null);
        when(expenseService.saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL)))
                .thenReturn(expenseResponse);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(expenseService, times(1))
                .saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL));
    }

    @Test
    void addExpense_WithLongDescription_ShouldSucceed() throws Exception {
        // Given
        String longDescription = "A".repeat(1000);
        expenseRequest.setDescription(longDescription);
        when(expenseService.saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL)))
                .thenReturn(expenseResponse);

        // When & Then
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(expenseService, times(1))
                .saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL));
    }

    // ==================== Integration-Style Tests ====================

    @Test
    void addExpense_ShouldCallServiceWithCorrectParameters() throws Exception {
        // Given
        when(expenseService.saveExpense(any(ExpenseRequest.class), eq(TEST_USER_EMAIL)))
                .thenReturn(expenseResponse);

        // When
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest))
                        .principal(authentication))
                .andExpect(status().isOk());

        // Then
        verify(expenseService, times(1))
                .saveExpense(argThat(request ->
                        request.getTitle().equals("Lunch") &&
                                request.getCategory().equals("Food") &&
                                request.getAmount().equals(25.50) &&
                                request.getDescription().equals("Team lunch at restaurant")
                ), eq(TEST_USER_EMAIL));
    }
}
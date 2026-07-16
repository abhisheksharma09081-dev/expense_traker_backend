package com.abhishek.expense_tracker.service.impl;

import com.abhishek.expense_tracker.dto.PageResponse;
import com.abhishek.expense_tracker.dto.IncomeRequest;
import com.abhishek.expense_tracker.dto.IncomeResponse;
import com.abhishek.expense_tracker.entity.Income;
import com.abhishek.expense_tracker.exception.ResourceNotFoundException;
import com.abhishek.expense_tracker.repository.IncomeRepository;
import com.abhishek.expense_tracker.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private static final Logger logger =
            LoggerFactory.getLogger(IncomeServiceImpl.class);

    private final IncomeRepository incomeRepository;

    @Override
    public IncomeResponse saveIncome(String email, IncomeRequest request) {

        logger.info("Saving income for user {}", email);

        Income income = Income.builder()
                .source(request.getSource())
                .amount(request.getAmount())
                .date(request.getDate())
                .description(request.getDescription())
                .userEmail(email)
                .build();

        return mapToResponse(incomeRepository.save(income));
    }

    @Override
    public PageResponse<IncomeResponse> getUserIncomes(
            String email,
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Income> incomePage =
                incomeRepository.findByUserEmail(email, pageable);

        return new PageResponse<>(
                incomePage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList(),
                incomePage.getNumber(),
                incomePage.getSize(),
                incomePage.getTotalElements(),
                incomePage.getTotalPages(),
                incomePage.isFirst(),
                incomePage.isLast()
        );
    }

    @Override
    public IncomeResponse getIncomeById(String id, String email) {

        Income income = incomeRepository
                .findByIdAndUserEmail(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Income not found"));

        return mapToResponse(income);
    }

    @Override
    public IncomeResponse updateIncome(
            String id,
            String email,
            IncomeRequest request) {

        Income income = incomeRepository
                .findByIdAndUserEmail(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Income not found"));

        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setDate(request.getDate());
        income.setDescription(request.getDescription());

        logger.info("Income {} updated successfully", id);

        return mapToResponse(
                incomeRepository.save(income)
        );
    }

    @Override
    public void deleteIncome(String id, String email) {

        Income income = incomeRepository
                .findByIdAndUserEmail(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Income not found"));

        incomeRepository.delete(income);

        logger.info("Income {} deleted successfully", id);
    }

    private IncomeResponse mapToResponse(Income income) {

        return IncomeResponse.builder()
                .id(income.getId())
                .source(income.getSource())
                .amount(income.getAmount())
                .date(income.getDate())
                .description(income.getDescription())
                .build();
    }
}
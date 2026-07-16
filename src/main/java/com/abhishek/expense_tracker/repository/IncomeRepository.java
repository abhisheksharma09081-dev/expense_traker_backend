package com.abhishek.expense_tracker.repository;

import com.abhishek.expense_tracker.entity.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends MongoRepository<Income, String> {

    Page<Income> findByUserEmail(String userEmail, Pageable pageable);

    Optional<Income> findByIdAndUserEmail(String id, String email);
}
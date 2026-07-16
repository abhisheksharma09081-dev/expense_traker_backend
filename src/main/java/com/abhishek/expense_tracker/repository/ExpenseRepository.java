package com.abhishek.expense_tracker.repository;

import com.abhishek.expense_tracker.entity.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

    Page<Expense> findByUserEmail(String userEmail, Pageable pageable);

    Optional<Expense> findByIdAndUserEmail(String id, String userEmail);

}
package com.abhishek.expense_tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incomes")
@CompoundIndex(
        name = "income_user_date_idx",
        def = "{'userEmail':1,'date':-1}"
)
public class Income extends BaseEntity {

    @Id
    private String id;

    private String source;

    private Double amount;

    @Indexed
    private LocalDate date;

    private String description;
    @Indexed
    private String userEmail;
}
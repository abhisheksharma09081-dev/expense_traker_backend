package com.abhishek.expense_tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "expenses")
@CompoundIndex(
        name = "user_date_idx",
        def = "{'userEmail':1,'date':-1}"
)
public class Expense extends BaseEntity {

    @Id
    private String id;

    // Owner of this expense
    @Indexed
    private String userEmail;

    // Food, Travel, Shopping...
    @Indexed
    private String category;

    // Expense title
    private String title;

    // Amount
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;

    // Date of expense
    @Indexed
    private LocalDate date;

    // Optional description
    private String description;
}
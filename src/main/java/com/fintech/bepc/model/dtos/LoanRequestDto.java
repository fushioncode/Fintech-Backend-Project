package com.fintech.bepc.model.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @Positive(message = "Loan amount must be positive")
    private Double amount;

    @Min(value = 30, message = "Tenure must be at least 30days")
    private Integer tenure;
}
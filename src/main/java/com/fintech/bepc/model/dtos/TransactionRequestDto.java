package com.fintech.bepc.model.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    @NotNull(message = "Loan ID cannot be null")
    private Long loanId;

    @Positive(message = "Transaction amount must be positive")
    private Double amount;

    @NotNull(message = "Transaction type cannot be null")
    private String type; // e.g., DISBURSEMENT, REPAYMENT
}


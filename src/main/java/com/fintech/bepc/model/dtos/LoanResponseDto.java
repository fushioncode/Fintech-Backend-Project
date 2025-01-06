package com.fintech.bepc.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {
    private Long id;
    private Long userId;
    private Double amount;
    private Integer tenure;
    private Double interestRate;
    private String status;
    private Double totalAmountToPay;
}

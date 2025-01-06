package com.fintech.bepc.services;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.LoanRequestDto;
import com.fintech.bepc.model.dtos.LoanResponseDto;
import com.fintech.bepc.model.entities.Loan;

import java.util.List;

public interface LoanService {
    ApiResponse<LoanResponseDto> applyForLoan(LoanRequestDto loanRequestDto);
    ApiResponse<LoanResponseDto> updateLoanStatus(Long loanId, String status);
    ApiResponse<List<LoanResponseDto>> getLoansByUser(Long userId);
}
package com.fintech.bepc.services;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.LoanResponseDto;
import com.fintech.bepc.model.dtos.TransactionRequestDto;
import com.fintech.bepc.model.dtos.TransactionResponseDto;
import com.fintech.bepc.model.entities.Transaction;

import java.util.List;

public interface TransactionService {

    ApiResponse<LoanResponseDto> recordTransaction(TransactionRequestDto transactionRequestDto);
    <T>ApiResponse<T> getTransactionsByLoan(Long loanId);
}

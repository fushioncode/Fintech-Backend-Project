package com.fintech.bepc.controller;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.TransactionRequestDto;
import com.fintech.bepc.model.dtos.TransactionResponseDto;
import com.fintech.bepc.model.entities.Transaction;
import com.fintech.bepc.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "Endpoints for managing transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    @PostMapping("/record")
    @Operation(
            summary = "Record transactions for loan",
            description = "Record loan transaction like DISBURSEMENT, REPAYMENT",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> recordTransaction(@Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        logger.info("Attempting to record transaction for loanId {}", transactionRequestDto.getLoanId());
        return ResponseEntity.ok(transactionService.recordTransaction(transactionRequestDto));
    }

    @GetMapping("/loan/{loanId}")
    @Operation(
            summary = "Get Transaction by loanId",
            description = "Retrieve transactions related to Loan by loanID",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> getTransactionsByLoan(@PathVariable Long loanId) {
        logger.info("Fetching transactions for loanId {}", loanId);
        return ResponseEntity.ok(transactionService.getTransactionsByLoan(loanId));
    }
}

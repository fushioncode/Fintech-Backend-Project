package com.fintech.bepc.controller;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.LoanRequestDto;
import com.fintech.bepc.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Endpoints for managing users")
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    @Operation(
            summary = "Apply for loan",
            description = "User apply for loan",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> applyForLoan(@Valid @RequestBody LoanRequestDto loanRequestDto) {
        log.info("Attempting to apply for loan for userId {}", loanRequestDto.getUserId());
        return ResponseEntity.ok(loanService.applyForLoan(loanRequestDto));
    }

    @PutMapping("/{loanId}/status/update")
    @Operation(
            summary = "Update loan status",
            description = "Admin updaate loan status",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> updateLoanStatus(@PathVariable Long loanId, @RequestParam String status) {
        log.info("Updating loan status for loanId {} to {}", loanId, status);
        return ResponseEntity.ok(loanService.updateLoanStatus(loanId, status));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get Loan by User ID",
            description = "Retrieve loan details by user ID",
            security = @SecurityRequirement(name = "Authorization")
    )
    public ResponseEntity<ApiResponse> getLoansByUser(@PathVariable Long userId) {
        log.info("Fetching loans for userId {}", userId);
        return ResponseEntity.ok(loanService.getLoansByUser(userId));
    }
}
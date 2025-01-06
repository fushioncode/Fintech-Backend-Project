package com.fintech.bepc.services.serviceImpl;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.LoanRequestDto;
import com.fintech.bepc.model.dtos.LoanResponseDto;
import com.fintech.bepc.model.entities.Loan;
import com.fintech.bepc.model.entities.User;
import com.fintech.bepc.repositories.LoanRepository;
import com.fintech.bepc.repositories.UserRepository;
import com.fintech.bepc.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ILoanService implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(ILoanService.class);
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    @Value("${zero-to-90-days-interest-rate}")
    private int interest0to90;
    @Value("${91-to-180-days-interest-rate}")
    private int interest91To180;
    @Value("${181-to-365-days-interest-rate}")
    private  int interest181To365;
    @Override
    public ApiResponse<LoanResponseDto> applyForLoan(LoanRequestDto loanRequestDto) {
        User user = userRepository.findById(loanRequestDto.getUserId())
                .orElseThrow(() -> {
                    logger.error("User with ID {} does not exist", loanRequestDto.getUserId());
                    return new IllegalArgumentException("User does not exist");
                });

        if (loanRequestDto.getAmount() < 1000 || loanRequestDto.getAmount() > 1000000) {
            throw new IllegalArgumentException("Loan amount must be between 1000 and 1000000");
        }

        if (loanRequestDto.getTenure() < 30 || loanRequestDto.getTenure() > 365) {
            throw new IllegalArgumentException("Loan tenure cannot be lower than 30days and cannot be higher than 365days");
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setAmount(loanRequestDto.getAmount());
        loan.setTenure(loanRequestDto.getTenure());
        loan.setStatus("PENDING");

        double interest = calculateInterest(loanRequestDto.getAmount(), loanRequestDto.getTenure());
        loan.setInterestRate(interest);
        loan.setTotalAmountToPay(loanRequestDto.getAmount() + interest);

        loan = loanRepository.save(loan);
        logger.info("Loan application recorded successfully for user ID {}", user.getId());

        return ApiResponse.success(new LoanResponseDto(loan.getId(), user.getId(), loan.getAmount(), loan.getTenure(), loan.getInterestRate(), loan.getStatus(), loan.getTotalAmountToPay()));
    }

    @Override
    public ApiResponse<LoanResponseDto> updateLoanStatus(Long loanId, String status) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> {
            logger.error("Cannot update status, loan with id {} does not exist", loanId);
            return new IllegalArgumentException("Loan does not exist");
        });

        status = status.toUpperCase();
        // Ensure valid status transition
        if (!isValidLoanStatusTransition(loan.getStatus(), status)) {
            throw new IllegalArgumentException("Invalid loan status transition");
        }

        // Loan can only be marked as REPAID if total amount is 0
        if (status.equals("REPAID") && loan.getTotalAmountToPay() != 0) {
            throw new IllegalArgumentException("Loan balance must be zero to mark as REPAID");
        }

        if (status.equals("APPROVED")){
            if (loan.getUser().isActive()){
                loan.setStatus(status);
            }else{
                loan.setStatus("REJECTED");
            }
            loanRepository.save(loan);
            return ApiResponse.success(new LoanResponseDto(loan.getId(), loan.getUser().getId(), loan.getAmount(), loan.getTenure(), loan.getInterestRate(), loan.getStatus(), loan.getTotalAmountToPay()));
        }

        loan.setStatus(status);
        loan = loanRepository.save(loan);
        logger.info("Loan status updated to {} for loanId {}", status, loanId);

        return ApiResponse.success(new LoanResponseDto(loan.getId(), loan.getUser().getId(), loan.getAmount(), loan.getTenure(), loan.getInterestRate(), loan.getStatus(), loan.getTotalAmountToPay()));
    }

    @Override
    public ApiResponse<List<LoanResponseDto>> getLoansByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            logger.warn("Cannot fetch loans, user with id {} does not exist", userId);
            throw new IllegalArgumentException("User does not exist");
        }
        logger.info("Fetching loans for userId {}", userId);
        return ApiResponse.success(loanRepository.findByUserId(userId)
                .stream()
                .map(loan -> new LoanResponseDto(loan.getId(), loan.getUser().getId(), loan.getAmount(), loan.getTenure(), loan.getInterestRate(), loan.getStatus(), loan.getTotalAmountToPay()))
                .collect(Collectors.toList()));
    }

    private boolean isValidLoanStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals("PENDING")) {
            return newStatus.equals("APPROVED") || newStatus.equals("REJECTED");
        }
        if (currentStatus.equals("APPROVED")) {
            return newStatus.equals("DISBURSED");
        }
        if (currentStatus.equals("DISBURSED")) {
            return newStatus.equals("REPAID");
        }
        return false;
    }

    private double calculateInterest(double loanAmount, int tenure) {
        double interestRate = 0.0;

        if (tenure >= 30 && tenure <= 90) {
            interestRate = (double) interest0to90 /100;
        } else if (tenure >= 91 && tenure <= 180) {
            interestRate = (double) interest91To180 /100;
        } else if (tenure >= 181 && tenure <= 365) {
            interestRate = (double) interest181To365 /100;
        } else {
            throw new IllegalArgumentException("Tenure must be between 30 and 365 days");
        }

        return loanAmount * interestRate * tenure / 365;
    }

}

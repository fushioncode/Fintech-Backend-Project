package com.fintech.bepc.services.serviceImpl;

import com.fintech.bepc.exceptions.DatabaseException;
import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.LoanResponseDto;
import com.fintech.bepc.model.dtos.TransactionRequestDto;
import com.fintech.bepc.model.dtos.TransactionResponseDto;
import com.fintech.bepc.model.entities.Loan;
import com.fintech.bepc.model.entities.Transaction;
import com.fintech.bepc.repositories.LoanRepository;
import com.fintech.bepc.repositories.TransactionRepository;
import com.fintech.bepc.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import static com.fintech.bepc.model.entities.Transaction.TransactionType.DISBURSEMENT;
import static com.fintech.bepc.model.entities.Transaction.TransactionType.REPAYMENT;

@Service
@RequiredArgsConstructor
public class ITransactionService implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(ITransactionService.class);
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;

    @Override
    public ApiResponse<LoanResponseDto> recordTransaction(TransactionRequestDto transactionRequestDto) {
        Loan loan = loanRepository.findById(transactionRequestDto.getLoanId())
                .orElseThrow(() -> {
                    logger.error("Loan with ID {} not found", transactionRequestDto.getLoanId());
                    return new IllegalArgumentException("Loan not found");
                });

        Transaction.TransactionType transactionType;
        try {
            transactionType = Transaction.TransactionType.valueOf(transactionRequestDto.getType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionRequestDto.getType());
        }

        switch (transactionType) {
            case DISBURSEMENT:
                return ApiResponse.success(disburseLoan(loan, transactionRequestDto));
            case REPAYMENT:
                return ApiResponse.success(makeRepayment(loan, transactionRequestDto));
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + transactionRequestDto.getType());
        }


    }

    private void storeTransaction(TransactionRequestDto transactionRequestDto, Loan loan) {
        try{
        Transaction transaction = new Transaction();
        transaction.setLoan(loan);
        transaction.setAmount(BigDecimal.valueOf(transactionRequestDto.getAmount()));

        Transaction.TransactionType type = transactionRequestDto.getType().equals(DISBURSEMENT.name()) ?
                DISBURSEMENT : REPAYMENT;

        transaction.setType(type);

        transaction = transactionRepository.save(transaction);
        logger.info("Transaction of type {} recorded for loan ID {}", transaction.getType(), loan.getId());
        } catch (Exception e) {
            logger.error("Error occurred while storing transaction:::: \n ERROR:::::::::::{}", e.getMessage());
            throw new DatabaseException("Error while processing transaction");
        }

    }

    private LoanResponseDto disburseLoan(Loan loan, TransactionRequestDto transaction) {
        if (loan.getStatus().equals("REPAID") || loan.getStatus().equals("REJECTED")){
            throw new IllegalArgumentException(String.format("Loan account is close. Status is %s", loan.getStatus()));
        }

        if (loan.getStatus().equals("DISBURSED")){
            throw new IllegalArgumentException("Loan already disburse..");
        }

        if (loan.getStatus().equals("PENDING")){
            throw new IllegalArgumentException("Loan is pending approval.. Disburse on hold until approved");
        }

        if (!loan.getAmount().equals(transaction.getAmount())){
            throw new IllegalArgumentException("Disbursement amount varies from actual loan amount");
        }

        storeTransaction(transaction, loan);
        loan.setStatus("DISBURSED");
        loanRepository.save(loan);
        return new LoanResponseDto(loan.getId(), loan.getUser().getId(), loan.getAmount(),loan.getTenure(), loan.getInterestRate(),loan.getStatus(),loan.getTotalAmountToPay());
    }

    private LoanResponseDto makeRepayment(Loan loan, TransactionRequestDto transaction) {

        if (loan.getStatus().equals("REPAID") || loan.getStatus().equals("REJECTED")){
            throw new IllegalArgumentException(String.format("Loan account is close. Status is %s", loan.getStatus()));
        }

        if (loan.getStatus().equals("APPROVED") || loan.getStatus().equals("PENDING")){
            throw new IllegalArgumentException("Loan not disburse. Repayment suspended.");
        }

        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Repayment amount must be greater than zero");
        }

        double remainingAmount = loan.getTotalAmountToPay() - transaction.getAmount();
        if (remainingAmount < 0) {
            throw new IllegalArgumentException("Repayment amount exceeds remaining balance");
        }

        storeTransaction(transaction, loan);
        loan.setTotalAmountToPay(remainingAmount);
        loanRepository.save(loan);

        logger.info("Repayment of {} recorded for loan ID {}", transaction.getAmount(), loan.getId());

        if (remainingAmount == 0) {
            loan.setStatus("REPAID");
            loanRepository.save(loan);
            logger.info("Loan ID {} has been marked as REPAID", loan.getId());
        }

        return new LoanResponseDto(loan.getId(), loan.getUser().getId(), loan.getAmount(), loan.getTenure(), loan.getInterestRate(), loan.getStatus(), loan.getTotalAmountToPay());
    }


    @Override
    public <T>ApiResponse<T> getTransactionsByLoan(Long loanId) {
        logger.info("Fetching transactions for loan ID {}", loanId);
        return ApiResponse.success((T)transactionRepository.findByLoanId(loanId).stream()
                .map(transaction -> new TransactionResponseDto(
                        transaction.getId(), transaction.getLoan().getId(),
                        transaction.getAmount().doubleValue(), transaction.getType().name()))
                .collect(Collectors.toList()));
    }
}

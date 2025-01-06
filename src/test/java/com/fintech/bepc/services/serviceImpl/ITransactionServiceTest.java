package com.fintech.bepc.services.serviceImpl;


import com.fintech.bepc.exception.DatabaseException;
import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.LoanResponseDto;
import com.fintech.bepc.model.dtos.TransactionRequestDto;
import com.fintech.bepc.model.dtos.TransactionResponseDto;
import com.fintech.bepc.model.entities.Loan;
import com.fintech.bepc.model.entities.Transaction;
import com.fintech.bepc.model.entities.User;
import com.fintech.bepc.repositories.LoanRepository;
import com.fintech.bepc.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.fintech.bepc.model.entities.Transaction.TransactionType.DISBURSEMENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ITransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ITransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void recordTransaction_disbursement_success() {
        User user = new User();
        user.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(user);
        loan.setStatus("APPROVED");
        loan.setAmount(50000.0);
        loan.setTenure(12);
        loan.setInterestRate(5.0);
        loan.setTotalAmountToPay(55000.0);

        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 50000.0, "DISBURSEMENT");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);
            return transaction;
        });
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        ApiResponse<LoanResponseDto> response = transactionService.recordTransaction(requestDto);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        LoanResponseDto loanResponse = response.getData();
        assertEquals(1L, loanResponse.getId());
        assertEquals("DISBURSED", loanResponse.getStatus());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(loanRepository, times(1)).save(any(Loan.class));
    }


    @Test
    void recordTransaction_disbursementWithInvalidStatus() {
        Loan loan = createLoan("DISBURSED", 5000.0);
        TransactionRequestDto request = new TransactionRequestDto(1L, 5000.0, "DISBURSEMENT");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionService.recordTransaction(request));
        assertEquals("Loan already disburse..", exception.getMessage());
    }

    @Test
    void recordTransaction_repayment_success() {
        User user = new User();
        user.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(user);
        loan.setStatus("DISBURSED");
        loan.setAmount(50000.0);
        loan.setTenure(12);
        loan.setInterestRate(5.0);
        loan.setTotalAmountToPay(55000.0);

        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 5000.0, "REPAYMENT");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);
            return transaction;
        });
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        ApiResponse<LoanResponseDto> response = transactionService.recordTransaction(requestDto);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        LoanResponseDto loanResponse = response.getData();
        assertEquals(1L, loanResponse.getId());
        assertEquals("DISBURSED", loanResponse.getStatus());
        assertEquals(50000.0, loanResponse.getTotalAmountToPay()); // Reduced balance
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(loanRepository, times(1)).save(any(Loan.class));
    }


    @Test
    void recordTransaction_repaymentExceedsBalance() {
        Loan loan = createLoan("DISBURSED", 5000.0);
        loan.setTotalAmountToPay(3000.0);
        TransactionRequestDto request = new TransactionRequestDto(1L, 4000.0, "REPAYMENT");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionService.recordTransaction(request));
        assertEquals("Repayment amount exceeds remaining balance", exception.getMessage());
    }

    @Test
    void recordTransaction_invalidTransactionType() {
        User user = new User();
        user.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(user);
        loan.setStatus("DISBURSED");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        TransactionRequestDto request = new TransactionRequestDto(1L, 5000.0, "INVALID_TYPE");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionService.recordTransaction(request));
        assertEquals("Invalid transaction type: INVALID_TYPE", exception.getMessage());
    }


    @Test
    void storeTransaction_databaseError() {
        Loan loan = createLoan("DISBURSED", 5000.0);
        when(loanRepository.findById(anyLong())).thenReturn(Optional.of(loan));

        TransactionRequestDto request = new TransactionRequestDto(1L, 2000.0, "REPAYMENT");
        when(transactionRepository.save(any(Transaction.class))).thenThrow(new DataAccessException("DB error") {});

        DatabaseException exception = assertThrows(DatabaseException.class, () -> transactionService.recordTransaction(request));
        assertEquals("Error while processing transaction", exception.getMessage());
    }


    @Test
    void getTransactionsByLoan_success() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(5000.0));
        transaction.setType(DISBURSEMENT);
        Loan loan = createLoan("DISBURSED", 5000.0);
        transaction.setLoan(loan);

        when(transactionRepository.findByLoanId(1L)).thenReturn(List.of(transaction));

        ApiResponse<List<TransactionResponseDto>> response = transactionService.getTransactionsByLoan(1L);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals(5000.0, response.getData().get(0).getAmount().doubleValue());
    }

    private Loan createLoan(String status, double totalAmountToPay) {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(status);
        loan.setTotalAmountToPay(totalAmountToPay);
        return loan;
    }
}

package com.fintech.bepc.services.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.LoanRequestDto;
import com.fintech.bepc.model.dtos.LoanResponseDto;
import com.fintech.bepc.model.entities.Loan;
import com.fintech.bepc.model.entities.User;
import com.fintech.bepc.repositories.LoanRepository;
import com.fintech.bepc.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ILoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ILoanService loanService;

    @Value("${zero-to-90-days-interest-rate}")
    private int interest0to90 = 10;

    @Value("${91-to-180-days-interest-rate}")
    private int interest91To180 = 15;

    @Value("${181-to-365-days-interest-rate}")
    private int interest181To365 = 20;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void applyForLoan_ValidRequest_ReturnsSuccessResponse() {
        LoanRequestDto loanRequestDto = new LoanRequestDto(1L, 5000.0, 120);

        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepository.findById(loanRequestDto.getUserId())).thenReturn(Optional.of(user));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setId(1L);
            return loan;
        });

        ApiResponse<LoanResponseDto> response = loanService.applyForLoan(loanRequestDto);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(5000.0, response.getData().getAmount());
        assertEquals(120, response.getData().getTenure());
        verify(userRepository, times(1)).findById(loanRequestDto.getUserId());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void applyForLoan_InvalidUser_ThrowsException() {
        LoanRequestDto loanRequestDto = new LoanRequestDto(99L, 5000.0, 120);

        when(userRepository.findById(loanRequestDto.getUserId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.applyForLoan(loanRequestDto);
        });

        assertEquals("User does not exist", exception.getMessage());
        verify(userRepository, times(1)).findById(loanRequestDto.getUserId());
    }

    @Test
    void updateLoanStatus_ValidTransition_ReturnsSuccessResponse() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus("PENDING");
        loan.setTotalAmountToPay(0.0);
        loan.setUser(new User());

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        ApiResponse<LoanResponseDto> response = loanService.updateLoanStatus(1L, "APPROVED");

        assertTrue(response.isSuccess());
        assertEquals("APPROVED", response.getData().getStatus());
        verify(loanRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).save(loan);
    }

    @Test
    void updateLoanStatus_InvalidTransition_ThrowsException() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus("DISBURSED");
        loan.setTotalAmountToPay(1000.0);
        loan.setUser(new User());

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.updateLoanStatus(1L, "APPROVED");
        });

        assertEquals("Invalid loan status transition", exception.getMessage());
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    void getLoansByUser_ValidUser_ReturnsLoans() {
        User user = new User();
        user.setId(1L);

        Loan loan1 = new Loan();
        loan1.setId(1L);
        loan1.setUser(user);
        loan1.setAmount(5000.0);
        loan1.setTenure(120);
        loan1.setStatus("APPROVED");
        loan1.setTotalAmountToPay(6000.0);

        Loan loan2 = new Loan();
        loan2.setId(2L);
        loan2.setUser(user);
        loan2.setAmount(2000.0);
        loan2.setTenure(60);
        loan2.setStatus("DISBURSED");
        loan2.setTotalAmountToPay(2200.0);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(loanRepository.findByUserId(1L)).thenReturn(Arrays.asList(loan1, loan2));

        ApiResponse<List<LoanResponseDto>> response = loanService.getLoansByUser(1L);

        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().size());
        verify(userRepository, times(1)).existsById(1L);
        verify(loanRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getLoansByUser_InvalidUser_ThrowsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.getLoansByUser(99L);
        });

        assertEquals("User does not exist", exception.getMessage());
        verify(userRepository, times(1)).existsById(99L);
    }
}

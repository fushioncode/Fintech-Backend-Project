package com.fintech.bepc.repositories;

import com.fintech.bepc.model.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByLoanId(Long loanId);
}

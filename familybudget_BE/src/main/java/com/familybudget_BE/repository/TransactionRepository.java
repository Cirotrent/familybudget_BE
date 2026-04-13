package com.familybudget_BE.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.familybudget_BE.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByUserUsername(String username);

    List<Transaction> findByUserUsernameAndDateBetween(String username, LocalDate start, LocalDate end);

    List<Transaction> findByUserUsernameAndType(String username, Transaction.Type type);
}

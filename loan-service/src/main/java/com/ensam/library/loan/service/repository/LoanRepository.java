package com.ensam.library.loan.service.repository;

import com.ensam.library.loan.service.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);

    Optional<Loan> findByBookIdAndReturnedFalse(Long bookId);

    List<Loan> findByReturnedFalse();
}
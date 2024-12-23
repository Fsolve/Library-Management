package com.ensam.library.loan.service.controller;

import com.ensam.library.loan.service.dto.Book;
import com.ensam.library.loan.service.dto.LoanRequest;
import com.ensam.library.loan.service.model.Loan;
import com.ensam.library.loan.service.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Slf4j
public class LoanController {
    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanRequest request) {
        Loan loan = loanService.createLoan(request.getUserId(), request.getBookId());
        return new ResponseEntity<>(loan, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        try {
            log.info("Attempting to fetch all loans");
            List<Loan> loans = loanService.getAllLoans();
            log.info("Successfully retrieved {} loans", loans.size());
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            log.error("Error fetching all loans", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/return/{loanId}")
    public ResponseEntity<Loan> returnBook(@PathVariable Long loanId) {
        Loan loan = loanService.returnBook(loanId);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getUserLoans(@PathVariable Long userId) {
        List<Loan> loans = loanService.getUserLoans(userId);
        return ResponseEntity.ok(loans);
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
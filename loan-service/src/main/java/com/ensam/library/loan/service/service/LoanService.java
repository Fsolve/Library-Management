package com.ensam.library.loan.service.service;

import com.ensam.library.loan.service.dto.Book;
import com.ensam.library.loan.service.model.Loan;
import com.ensam.library.loan.service.repository.LoanRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public LoanService(LoanRepository loanRepository, RestTemplate restTemplate) {
        this.loanRepository = loanRepository;
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "loanService", fallbackMethod = "createLoanFallback")
    public Loan createLoan(Long userId, Long bookId) {
        log.info("Creating loan for user {} and book {}", userId, bookId);

        // Check if the book is already loaned
        Optional<Loan> existingLoan = loanRepository.findByBookIdAndReturnedFalse(bookId);
        if (existingLoan.isPresent()) {
            throw new RuntimeException("Book is already loaned");
        }

        // Check if book is available through Book Service
        ResponseEntity<Book> bookResponse = restTemplate.getForEntity(
                "http://book-service/api/books/" + bookId,
                Book.class);

        if (bookResponse.getBody() == null || !bookResponse.getBody().isAvailable()) {
            throw new RuntimeException("Book is not available");
        }

        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setBookId(bookId);
        loan.setLoanDate(LocalDateTime.now());
        loan.setReturned(false);

        // Update book availability
        bookResponse.getBody().setAvailable(false);
        restTemplate.put("http://book-service/api/books/" + bookId, bookResponse.getBody());

        // Notify user through Notification Service
        try {
            restTemplate.postForEntity(
                    "http://notification-service/api/notifications/loan",
                    loan,
                    Void.class);
        } catch (Exception e) {
            log.error("Failed to send notification for loan creation", e);
            // Continue with loan creation even if notification fails
        }

        Loan savedLoan = loanRepository.save(loan);
        log.info("Loan created successfully with id: {}", savedLoan.getId());
        return savedLoan;
    }

    public Loan createLoanFallback(Long userId, Long bookId, Exception ex) {
        log.error("Fallback: Error creating loan for user {} and book {}", userId, bookId, ex);
        throw new RuntimeException("Service is currently unavailable. Please try again later.");
    }

    public Loan returnBook(Long loanId) {
        log.info("Processing return for loan {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found with id: " + loanId));

        if (loan.isReturned()) {
            throw new RuntimeException("Book is already returned");
        }

        loan.setReturnDate(LocalDateTime.now());
        loan.setReturned(true);

        // Update book availability
        try {
            restTemplate.put(
                    "http://book-service/api/books/" + loan.getBookId() + "/return",
                    null);
        } catch (Exception e) {
            log.error("Failed to update book availability", e);
            throw new RuntimeException("Failed to process return. Please try again.");
        }

        // Notify user
        try {
            restTemplate.postForEntity(
                    "http://notification-service/api/notifications/return",
                    loan,
                    Void.class);
        } catch (Exception e) {
            log.error("Failed to send notification for book return", e);
            // Continue with return process even if notification fails
        }

        Loan returnedLoan = loanRepository.save(loan);
        log.info("Book successfully returned for loan {}", loanId);
        return returnedLoan;
    }

    public List<Loan> getUserLoans(Long userId) {
        log.info("Retrieving loans for user {}", userId);
        return loanRepository.findByUserId(userId);
    }

    public Optional<Loan> getLoanById(Long loanId) {
        log.info("Retrieving loan with id {}", loanId);
        return loanRepository.findById(loanId);
    }

    public List<Loan> getAllActiveLoans() {
        log.info("Retrieving all active loans");
        return loanRepository.findByReturnedFalse();
    }

    public List<Loan> getAllLoans() {
        log.info("Retrieving all loans");
        return loanRepository.findAll();
    }

    public void deleteLoan(Long loanId) {
        log.info("Deleting loan {}", loanId);
        loanRepository.deleteById(loanId);
    }
}
package com.ensam.library.loan.service.service;

import com.ensam.library.loan.service.dto.Book;
import com.ensam.library.loan.service.dto.NotificationRequest;
import com.ensam.library.loan.service.dto.UserDTO;
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

        // Check if book is already loaned
        Optional<Loan> existingLoan = loanRepository.findByBookIdAndReturnedFalse(bookId);
        if (existingLoan.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is already loaned");
        }

        // Get book details
        ResponseEntity<Book> bookResponse;
        try {
            bookResponse = restTemplate.getForEntity(
                    "http://book-service/api/books/" + bookId,
                    Book.class);

            if (bookResponse.getBody() == null || !bookResponse.getBody().isAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is not available");
            }
        } catch (Exception e) {
            log.error("Error retrieving book details: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Book service is currently unavailable");
        }

        Book book = bookResponse.getBody();

        // Get user details with error handling
        UserDTO user = null;
        try {
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(
                    "http://user-service/api/users/" + userId,
                    UserDTO.class);
            user = userResponse.getBody();
            log.info("Successfully retrieved user information for userId: {}", userId);
        } catch (Exception e) {
            log.warn("Could not retrieve user information: {}. Proceeding with loan creation.", e.getMessage());
            // Continue with loan creation even if user service is unavailable
        }

        // Create loan
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setBookId(bookId);
        loan.setLoanDate(LocalDateTime.now());
        loan.setReturned(false);

        // Update book availability
        try {
            book.setAvailable(false);
            restTemplate.put("http://book-service/api/books/" + bookId, book);
        } catch (Exception e) {
            log.error("Error updating book availability: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Could not update book availability");
        }

        // Create notification if user information is available
        if (user != null && user.getEmail() != null) {
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(userId);
            notificationRequest.setUserEmail(user.getEmail());
            notificationRequest.setBookId(bookId);
            notificationRequest.setBookTitle(book.getTitle());
            notificationRequest.setType("LOAN_CREATED");
            notificationRequest.setMessage(String.format("You have borrowed the book: %s. Due date: %s",
                    book.getTitle(),
                    loan.getLoanDate().plusDays(14).toString()));

            try {
                // Send notification
                restTemplate.postForEntity(
                        "http://notification-service/api/notifications/send",
                        notificationRequest,
                        Void.class);
                log.info("Notification sent successfully for loan creation");
            } catch (Exception e) {
                log.error("Failed to send notification for loan creation: {}", e.getMessage());
                // Continue with loan creation even if notification fails
            }
        } else {
            log.warn("Skipping notification - user information not available");
        }

        Loan savedLoan = loanRepository.save(loan);
        log.info("Loan created successfully with id: {}", savedLoan.getId());
        return savedLoan;
    }

    public Loan returnBook(Long loanId) {
        log.info("Processing return for loan {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found with id: " + loanId));

        if (loan.isReturned()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is already returned");
        }

        // Get book details with error handling
        Book book = null;
        try {
            ResponseEntity<Book> bookResponse = restTemplate.getForEntity(
                    "http://book-service/api/books/" + loan.getBookId(),
                    Book.class);
            book = bookResponse.getBody();
        } catch (Exception e) {
            log.error("Error retrieving book details: {}", e.getMessage());
            // Continue with return process even if book service is unavailable
        }

        // Get user details with error handling
        UserDTO user = null;
        try {
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(
                    "http://user-service/api/users/" + loan.getUserId(),
                    UserDTO.class);
            user = userResponse.getBody();
        } catch (Exception e) {
            log.warn("Could not retrieve user information: {}. Proceeding with return.", e.getMessage());
            // Continue with return process even if user service is unavailable
        }

        loan.setReturnDate(LocalDateTime.now());
        loan.setReturned(true);

        // Update book availability
        if (book != null) {
            try {
                book.setAvailable(true);
                restTemplate.put("http://book-service/api/books/" + loan.getBookId(), book);

                // Create return notification if user information is available
                if (user != null && user.getEmail() != null) {
                    NotificationRequest notificationRequest = new NotificationRequest();
                    notificationRequest.setUserId(loan.getUserId());
                    notificationRequest.setUserEmail(user.getEmail());
                    notificationRequest.setBookId(loan.getBookId());
                    notificationRequest.setBookTitle(book.getTitle());
                    notificationRequest.setType("LOAN_RETURNED");
                    notificationRequest.setMessage(String.format("You have successfully returned the book: %s",
                            book.getTitle()));

                    try {
                        restTemplate.postForEntity(
                                "http://notification-service/api/notifications/send",
                                notificationRequest,
                                Void.class);
                        log.info("Notification sent successfully for book return");
                    } catch (Exception e) {
                        log.error("Failed to send notification for book return: {}", e.getMessage());
                    }
                } else {
                    log.warn("Skipping return notification - user information not available");
                }
            } catch (Exception e) {
                log.error("Error updating book availability: {}", e.getMessage());
                // Continue with return process even if book update fails
            }
        } else {
            log.warn("Could not update book availability - book information not available");
        }

        Loan returnedLoan = loanRepository.save(loan);
        log.info("Book successfully returned for loan {}", loanId);
        return returnedLoan;
    }

    // Fallback method for circuit breaker
    public Loan createLoanFallback(Long userId, Long bookId, Exception e) {
        log.error("Fallback: Error creating loan for user {} and book {}: {}", userId, bookId, e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
            "Service is temporarily unavailable. Please try again later.");
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
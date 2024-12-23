package com.ensam.library.loan.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.ensam.library.loan.service.dto.Book;
import com.ensam.library.loan.service.dto.LoanRequest;
import com.ensam.library.loan.service.model.Loan;
import com.ensam.library.loan.service.service.LoanService;

@Controller
@RequestMapping("/loans")
public class LoanViewController {
    private final LoanService loanService;
    private final RestTemplate restTemplate;

    @Autowired
    public LoanViewController(LoanService loanService, RestTemplate restTemplate) {
        this.loanService = loanService;
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public String getAllLoans(Model model) {
        List<Loan> loans = loanService.getAllLoans();
        model.addAttribute("loans", loans);
        return "loan/list";
    }

    @GetMapping("/user/{userId}")
    public String getUserLoans(@PathVariable Long userId, Model model) {
        List<Loan> loans = loanService.getUserLoans(userId);
        model.addAttribute("loans", loans);
        return "loan/list";
    }

    @GetMapping("/add")
    public String showAddLoanForm(Model model) {
        // Get available books from book service
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                "http://book-service/api/books",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {
                });

        List<Book> books = response.getBody();
        model.addAttribute("availableBooks", books);
        return "loan/add";
    }

    @PostMapping
    public String createLoan(@ModelAttribute LoanRequest loanRequest) {
        loanService.createLoan(loanRequest.getUserId(), loanRequest.getBookId());
        return "redirect:/loans";
    }

    @PostMapping("/return/{id}")
    public String returnBook(@PathVariable Long id) {
        loanService.returnBook(id);
        return "redirect:/loans";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return "redirect:/books"; // Redirige vers la liste des livres après suppression
    }
}
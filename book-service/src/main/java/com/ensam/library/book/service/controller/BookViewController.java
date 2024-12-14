package com.ensam.library.book.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ensam.library.book.service.model.Book;
import com.ensam.library.book.service.service.BookService;

@Controller
@RequestMapping("/books")
public class BookViewController {
    private final BookService bookService;

    @Autowired
    public BookViewController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String getAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "book/list"; // Nom du fichier Thymeleaf (list.html)
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book/add"; // Nom du fichier Thymeleaf (add.html)
    }

    @PostMapping
    public String addBook(@ModelAttribute Book book) {
        bookService.addBook(book);
        return "redirect:/books"; // Redirect to the list of books after adding
    }

    @GetMapping("/{id}")
    public String getBookById(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id).orElse(null);
        model.addAttribute("book", book);
        return "book/detail"; // Nom du fichier Thymeleaf (detail.html)
    }

    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id).orElse(null);
        model.addAttribute("book", book);
        return "book/edit"; // Nom du fichier Thymeleaf (edit.html)
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id, Book bookDetails) {
        bookService.updateBook(id, bookDetails);
        return "redirect:/books"; // Redirige vers la liste des livres après mise à jour
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books"; // Redirige vers la liste des livres après suppression
    }
}
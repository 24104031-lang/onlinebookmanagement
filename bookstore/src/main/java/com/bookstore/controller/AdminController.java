package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.service.OrderService;
import com.bookstore.service.ReviewService;
import com.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private BookService bookService;
    @Autowired private OrderService orderService;
    @Autowired private ReviewService reviewService;
    @Autowired private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBooks", bookService.getAllBooks().size());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("recentOrders", orderService.getAllOrders().stream().limit(5).toList());
        model.addAttribute("categories", bookService.getAllCategories());
        return "admin/dashboard";
    }

    @GetMapping("/books")
    public String books(Model model,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String category) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("books", bookService.search(search));
            model.addAttribute("search", search);
        } else if (category != null && !category.isBlank()) {
            model.addAttribute("books", bookService.getByCategory(category));
            model.addAttribute("selectedCategory", category);
        } else {
            model.addAttribute("books", bookService.getAllBooks());
        }
        model.addAttribute("categories", bookService.getAllCategories());
        return "admin/books";
    }

    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "admin/book-form";
    }

    @PostMapping("/books/add")
    public String addBook(@RequestParam String bookId,
                          @RequestParam String title,
                          @RequestParam String author,
                          @RequestParam double price,
                          @RequestParam int stock,
                          @RequestParam(defaultValue = "false") boolean ebook,
                          RedirectAttributes ra) {
        if (bookService.findByBookId(bookId).isPresent()) {
            ra.addFlashAttribute("error", "Book ID already exists.");
            return "redirect:/admin/books/add";
        }
        Book book = new Book(bookId, title, author, price, stock, ebook);
        bookService.addBook(book);
        ra.addFlashAttribute("success", "Book added successfully.");
        return "redirect:/admin/books";
    }

    @GetMapping("/books/edit/{bookId}")
    public String editBookForm(@PathVariable String bookId, Model model) {
        bookService.findByBookId(bookId).ifPresent(b -> model.addAttribute("book", b));
        return "admin/book-edit";
    }

    @PostMapping("/books/edit/{bookId}")
    public String editBook(@PathVariable String bookId,
                           @RequestParam String title,
                           @RequestParam String author,
                           @RequestParam double price,
                           @RequestParam int stock,
                           @RequestParam(defaultValue = "false") boolean ebook,
                           RedirectAttributes ra) {
        bookService.findByBookId(bookId).ifPresent(book -> {
            book.setTitle(title);
            book.setAuthor(author);
            book.setPrice(price);
            book.setStock(stock);
            book.setEbook(ebook);
            bookService.updateBook(book);
        });
        ra.addFlashAttribute("success", "Book updated.");
        return "redirect:/admin/books";
    }

    @PostMapping("/books/delete/{bookId}")
    public String deleteBook(@PathVariable String bookId, RedirectAttributes ra) {
        bookService.deleteBook(bookId);
        ra.addFlashAttribute("success", "Book deleted.");
        return "redirect:/admin/books";
    }

    @PostMapping("/books/stock/{bookId}")
    public String updateStock(@PathVariable String bookId, @RequestParam int stock, RedirectAttributes ra) {
        bookService.updateStock(bookId, stock);
        ra.addFlashAttribute("success", "Stock updated.");
        return "redirect:/admin/books";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }

    @PostMapping("/orders/status/{orderId}")
    public String updateOrderStatus(@PathVariable String orderId,
                                    @RequestParam String status,
                                    RedirectAttributes ra) {
        orderService.updateStatus(orderId, status);
        ra.addFlashAttribute("success", "Order status updated.");
        return "redirect:/admin/orders";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
}

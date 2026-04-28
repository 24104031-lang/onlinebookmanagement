package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() { return bookRepository.findAll(); }

    public List<Book> getByCategory(String category) { return bookRepository.findByCategory(category); }

    public List<Book> search(String query) { return bookRepository.search(query); }

    public Optional<Book> findByBookId(String bookId) { return bookRepository.findByBookId(bookId); }

    public List<String> getAllCategories() { return bookRepository.findAllCategories(); }

    public void addBook(Book book) { bookRepository.save(book); }

    public void updateBook(Book book) { bookRepository.update(book); }

    public void updateStock(String bookId, int stock) { bookRepository.updateStock(bookId, stock); }

    public void deleteBook(String bookId) { bookRepository.deleteByBookId(bookId); }
}

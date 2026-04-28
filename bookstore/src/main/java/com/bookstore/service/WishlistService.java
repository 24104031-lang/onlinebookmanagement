package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private BookRepository bookRepository;

    public void add(String customerId, String bookId) {
        wishlistRepository.add(customerId, bookId);
    }

    public void remove(String customerId, String bookId) {
        wishlistRepository.remove(customerId, bookId);
    }

    public List<Book> getWishlist(String customerId) {
        List<String> bookIds = wishlistRepository.getBookIds(customerId);
        return bookIds.stream()
            .map(bookRepository::findByBookId)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    public boolean isInWishlist(String customerId, String bookId) {
        return wishlistRepository.exists(customerId, bookId);
    }
}

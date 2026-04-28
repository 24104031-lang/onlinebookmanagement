package com.bookstore.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishlistRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public void add(String customerId, String bookId) {
        jdbc.update("INSERT OR IGNORE INTO wishlist (customer_id, book_id) VALUES (?,?)", customerId, bookId);
    }

    public void remove(String customerId, String bookId) {
        jdbc.update("DELETE FROM wishlist WHERE customer_id=? AND book_id=?", customerId, bookId);
    }

    public List<String> getBookIds(String customerId) {
        return jdbc.queryForList("SELECT book_id FROM wishlist WHERE customer_id=?", String.class, customerId);
    }

    public boolean exists(String customerId, String bookId) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM wishlist WHERE customer_id=? AND book_id=?", Integer.class, customerId, bookId);
        return count != null && count > 0;
    }
}

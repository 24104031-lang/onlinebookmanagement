package com.bookstore.repository;

import com.bookstore.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class ReviewRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RowMapper<Review> reviewMapper = (rs, rowNum) -> {
        Review r = new Review();
        r.setId(rs.getLong("id"));
        r.setBookId(rs.getString("book_id"));
        r.setBookTitle(rs.getString("book_title"));
        r.setCustomerId(rs.getString("customer_id"));
        r.setCustomerName(rs.getString("customer_name"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        String dateStr = rs.getString("created_at");
        if (dateStr != null) {
            try { r.setCreatedAt(LocalDateTime.parse(dateStr, fmt)); } catch (Exception e) { r.setCreatedAt(LocalDateTime.now()); }
        }
        return r;
    };

    public void save(Review review) {
        jdbc.update(
            "INSERT INTO reviews (book_id, book_title, customer_id, customer_name, rating, comment) VALUES (?,?,?,?,?,?)",
            review.getBookId(), review.getBookTitle(), review.getCustomerId(),
            review.getCustomerName(), review.getRating(), review.getComment());
    }

    public List<Review> findByBookId(String bookId) {
        return jdbc.query("SELECT * FROM reviews WHERE book_id=? ORDER BY created_at DESC", reviewMapper, bookId);
    }

    public List<Review> findAll() {
        return jdbc.query("SELECT * FROM reviews ORDER BY created_at DESC", reviewMapper);
    }

    public Double avgRatingForBook(String bookId) {
        return jdbc.queryForObject("SELECT AVG(rating) FROM reviews WHERE book_id=?", Double.class, bookId);
    }
}

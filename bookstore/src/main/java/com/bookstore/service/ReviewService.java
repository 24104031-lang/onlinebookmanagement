package com.bookstore.service;

import com.bookstore.model.Review;
import com.bookstore.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public void addReview(String bookId, String bookTitle, String customerId, String customerName, int rating, String comment) {
        Review r = new Review();
        r.setBookId(bookId);
        r.setBookTitle(bookTitle);
        r.setCustomerId(customerId);
        r.setCustomerName(customerName);
        r.setRating(rating);
        r.setComment(comment);
        r.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(r);
    }

    public List<Review> getReviewsForBook(String bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Double getAvgRating(String bookId) {
        Double avg = reviewRepository.avgRatingForBook(bookId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : null;
    }
}

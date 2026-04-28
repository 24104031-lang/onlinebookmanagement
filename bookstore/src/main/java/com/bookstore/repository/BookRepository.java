package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<Book> bookMapper = (rs, rowNum) -> {
        Book b = new Book();
        b.setId(rs.getLong("id"));
        b.setBookId(rs.getString("book_id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setPrice(rs.getDouble("price"));
        b.setStock(rs.getInt("stock"));
        b.setEbook(rs.getInt("ebook") == 1);
        b.setCategory(rs.getString("category"));
        b.setCoverUrl(rs.getString("cover_url"));
        return b;
    };

    public List<Book> findAll() {
        return jdbc.query("SELECT * FROM books ORDER BY category, title", bookMapper);
    }

    public List<Book> findByCategory(String category) {
        return jdbc.query("SELECT * FROM books WHERE category = ? ORDER BY title", bookMapper, category);
    }

    public List<Book> search(String query) {
        String q = "%" + query.toLowerCase() + "%";
        return jdbc.query(
            "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? OR LOWER(category) LIKE ? ORDER BY title",
            bookMapper, q, q, q);
    }

    public Optional<Book> findByBookId(String bookId) {
        List<Book> results = jdbc.query("SELECT * FROM books WHERE book_id = ?", bookMapper, bookId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<String> findAllCategories() {
        return jdbc.queryForList("SELECT DISTINCT category FROM books ORDER BY category", String.class);
    }

    public void save(Book book) {
        String category = Book.getCategoryFromBookId(book.getBookId());
        String coverUrl = "https://ui-avatars.com/api/?name=" + book.getTitle().replace(" ", "+")
            + "&size=200&background=random&color=fff&bold=true&length=2";
        jdbc.update(
            "INSERT INTO books (book_id, title, author, price, stock, ebook, category, cover_url) VALUES (?,?,?,?,?,?,?,?)",
            book.getBookId(), book.getTitle(), book.getAuthor(),
            book.getPrice(), book.getStock(), book.isEbook() ? 1 : 0, category, coverUrl);
    }

    public void update(Book book) {
        jdbc.update(
            "UPDATE books SET title=?, author=?, price=?, stock=?, ebook=? WHERE book_id=?",
            book.getTitle(), book.getAuthor(), book.getPrice(),
            book.getStock(), book.isEbook() ? 1 : 0, book.getBookId());
    }

    public void updateStock(String bookId, int stock) {
        jdbc.update("UPDATE books SET stock=? WHERE book_id=?", stock, bookId);
    }

    public void deleteByBookId(String bookId) {
        jdbc.update("DELETE FROM books WHERE book_id=?", bookId);
    }
}

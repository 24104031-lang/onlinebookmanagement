package com.bookstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbc;

    @PostConstruct
    public void init() {
        createTables();
        seedData();
    }

    private void createTables() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id TEXT UNIQUE NOT NULL,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                price REAL NOT NULL,
                stock INTEGER DEFAULT 0,
                ebook INTEGER DEFAULT 0,
                category TEXT,
                cover_url TEXT
            )
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL DEFAULT 'customer'
            )
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id TEXT UNIQUE NOT NULL,
                customer_id TEXT NOT NULL,
                total_amount REAL NOT NULL,
                status TEXT DEFAULT 'Pending',
                payment_method TEXT,
                created_at TEXT DEFAULT (datetime('now'))
            )
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS order_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER NOT NULL,
                book_id TEXT NOT NULL,
                book_title TEXT,
                quantity INTEGER NOT NULL,
                price_at_purchase REAL NOT NULL,
                FOREIGN KEY (order_id) REFERENCES orders(id)
            )
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS reviews (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id TEXT NOT NULL,
                book_title TEXT,
                customer_id TEXT NOT NULL,
                customer_name TEXT,
                rating INTEGER NOT NULL,
                comment TEXT,
                created_at TEXT DEFAULT (datetime('now'))
            )
        """);

        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS wishlist (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id TEXT NOT NULL,
                book_id TEXT NOT NULL,
                UNIQUE(customer_id, book_id)
            )
        """);
    }

    private void seedData() {
        // Seed admin if not exists
        Integer adminCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM users WHERE user_id = 'admin'", Integer.class);
        if (adminCount == null || adminCount == 0) {
            jdbc.update("INSERT INTO users (user_id, name, password, role) VALUES (?,?,?,?)",
                "admin", "Administrator", "admin", "admin");
        }

        // Seed books if empty
        Integer bookCount = jdbc.queryForObject("SELECT COUNT(*) FROM books", Integer.class);
        if (bookCount == null || bookCount == 0) {
            insertBook("F001", "The Great Gatsby", "F. Scott Fitzgerald", 10.99, 50, false);
            insertBook("F002", "To Kill a Mockingbird", "Harper Lee", 12.50, 40, false);
            insertBook("F003", "1984", "George Orwell", 11.20, 35, false);
            insertBook("F004", "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", 15.99, 60, false);
            insertBook("F005", "The Hobbit", "J.R.R. Tolkien", 14.25, 45, false);
            insertBook("F006", "Pride and Prejudice", "Jane Austen", 9.99, 30, false);
            insertBook("F007", "The Catcher in the Rye", "J.D. Salinger", 10.50, 25, false);
            insertBook("F008", "The Alchemist", "Paulo Coelho", 13.00, 55, false);
            insertBook("NF001", "Sapiens", "Yuval Noah Harari", 18.99, 20, false);
            insertBook("NF002", "Educated", "Tara Westover", 14.99, 15, false);
            insertBook("NF003", "Becoming", "Michelle Obama", 19.99, 18, false);
            insertBook("NF004", "Outliers", "Malcolm Gladwell", 13.75, 22, false);
            insertBook("NF005", "Thinking Fast and Slow", "Daniel Kahneman", 17.25, 10, false);
            insertBook("A001", "Introduction to Algorithms", "Thomas H. Cormen", 45.00, 10, false);
            insertBook("A002", "Operating System Concepts", "Abraham Silberschatz", 50.00, 8, false);
            insertBook("A003", "Database System Concepts", "Henry Korth", 48.50, 12, false);
            insertBook("A004", "Computer Networks", "Andrew S. Tanenbaum", 42.00, 9, false);
            insertBook("C001", "Charlotte's Web", "E.B. White", 8.99, 25, false);
            insertBook("C002", "Matilda", "Roald Dahl", 7.99, 30, false);
            insertBook("C003", "The Hunger Games", "Suzanne Collins", 12.99, 20, false);
            insertBook("T001", "Effective Java", "Joshua Bloch", 40.00, 10, false);
            insertBook("T002", "Clean Code", "Robert C. Martin", 38.50, 15, false);
            insertBook("T003", "The Pragmatic Programmer", "Andrew Hunt", 42.00, 12, false);
            insertBook("T004", "Python Crash Course", "Eric Matthes", 29.99, 18, false);
            insertBook("T005", "Artificial Intelligence: A Modern Approach", "Stuart Russell", 55.00, 8, false);
            insertBook("L001", "Atomic Habits", "James Clear", 16.99, 22, false);
            insertBook("L002", "The Power of Habit", "Charles Duhigg", 14.50, 18, false);
            insertBook("L003", "You Are a Badass", "Jen Sincero", 13.99, 20, false);
            insertBook("E001", "The Digital Revolution", "Ebook Author", 9.99, 0, true);
            insertBook("E002", "Guide to Success", "Another Author", 14.99, 0, true);
            insertBook("E003", "Online Learning Made Easy", "Tech Writer", 19.99, 0, true);
        }

        // Seed sample customer
        Integer custCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM users WHERE user_id = 'cust1'", Integer.class);
        if (custCount == null || custCount == 0) {
            jdbc.update("INSERT INTO users (user_id, name, password, role) VALUES (?,?,?,?)",
                "cust1", "Customer One", "pass", "customer");
        }
    }

    private void insertBook(String bookId, String title, String author, double price, int stock, boolean ebook) {
        String category = com.bookstore.model.Book.getCategoryFromBookId(bookId);
        String coverUrl = "https://ui-avatars.com/api/?name=" + title.replace(" ", "+")
            + "&size=200&background=random&color=fff&bold=true&length=2";
        jdbc.update(
            "INSERT INTO books (book_id, title, author, price, stock, ebook, category, cover_url) VALUES (?,?,?,?,?,?,?,?)",
            bookId, title, author, price, stock, ebook ? 1 : 0, category, coverUrl);
    }
}

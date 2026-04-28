package com.bookstore.model;

public class Book {
    private Long id;
    private String bookId;
    private String title;
    private String author;
    private double price;
    private int stock;
    private boolean ebook;
    private String category;
    private String coverUrl;

    public Book() {}

    public Book(String bookId, String title, String author, double price, int stock, boolean ebook) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.ebook = ebook;
        this.category = getCategoryFromBookId(bookId);
        this.coverUrl = generateCoverUrl(title);
    }

    public static String getCategoryFromBookId(String bookId) {
        if (bookId == null) return "Other";
        String id = bookId.toUpperCase();
        if (id.startsWith("NF")) return "Non-Fiction";
        if (id.startsWith("A"))  return "Academic";
        if (id.startsWith("C"))  return "Children";
        if (id.startsWith("T"))  return "Technology";
        if (id.startsWith("L"))  return "Lifestyle";
        if (id.startsWith("F"))  return "Fiction";
        if (id.startsWith("E"))  return "E-Book";
        return "Other";
    }

    private static String generateCoverUrl(String title) {
        return "https://ui-avatars.com/api/?name=" + title.replace(" ", "+") + "&size=200&background=random&color=fff&bold=true&length=2";
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) {
        this.bookId = bookId;
        this.category = getCategoryFromBookId(bookId);
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public boolean isEbook() { return ebook; }
    public void setEbook(boolean ebook) { this.ebook = ebook; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getStockDisplay() {
        return ebook ? "Unlimited" : String.valueOf(stock);
    }
}

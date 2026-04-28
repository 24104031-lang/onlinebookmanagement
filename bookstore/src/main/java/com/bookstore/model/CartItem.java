package com.bookstore.model;

public class CartItem {
    private String bookId;
    private String title;
    private String author;
    private double price;
    private int quantity;
    private boolean ebook;

    public CartItem() {}

    public CartItem(Book book, int quantity) {
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.price = book.getPrice();
        this.quantity = quantity;
        this.ebook = book.isEbook();
    }

    public double getSubtotal() { return price * quantity; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isEbook() { return ebook; }
    public void setEbook(boolean ebook) { this.ebook = ebook; }
}

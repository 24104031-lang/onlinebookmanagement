package com.bookstore.model;

public class OrderItem {
    private Long id;
    private Long orderId;
    private String bookId;
    private String bookTitle;
    private int quantity;
    private double priceAtPurchase;

    public OrderItem() {}

    public OrderItem(Long orderId, String bookId, String bookTitle, int quantity, double priceAtPurchase) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public double getSubtotal() { return quantity * priceAtPurchase; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(double priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
}

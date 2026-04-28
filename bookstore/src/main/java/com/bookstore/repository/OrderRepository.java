package com.bookstore.repository;

import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RowMapper<Order> orderMapper = (rs, rowNum) -> {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setOrderId(rs.getString("order_id"));
        o.setCustomerId(rs.getString("customer_id"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setPaymentMethod(rs.getString("payment_method"));
        String dateStr = rs.getString("created_at");
        if (dateStr != null) {
            try { o.setCreatedAt(LocalDateTime.parse(dateStr, fmt)); } catch (Exception e) { o.setCreatedAt(LocalDateTime.now()); }
        }
        // customer_name from join (may be null)
        try { o.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}
        return o;
    };

    private final RowMapper<OrderItem> itemMapper = (rs, rowNum) -> {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setBookId(rs.getString("book_id"));
        item.setBookTitle(rs.getString("book_title"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPriceAtPurchase(rs.getDouble("price_at_purchase"));
        return item;
    };

    public Long saveOrder(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO orders (order_id, customer_id, total_amount, status, payment_method) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getOrderId());
            ps.setString(2, order.getCustomerId());
            ps.setDouble(3, order.getTotalAmount());
            ps.setString(4, order.getStatus());
            ps.setString(5, order.getPaymentMethod());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void saveOrderItem(OrderItem item) {
        jdbc.update(
            "INSERT INTO order_items (order_id, book_id, book_title, quantity, price_at_purchase) VALUES (?,?,?,?,?)",
            item.getOrderId(), item.getBookId(), item.getBookTitle(), item.getQuantity(), item.getPriceAtPurchase());
    }

    public List<Order> findByCustomerId(String customerId) {
        return jdbc.query("SELECT * FROM orders WHERE customer_id=? ORDER BY created_at DESC", orderMapper, customerId);
    }

    public List<Order> findAll() {
        return jdbc.query("""
            SELECT o.*, u.name as customer_name 
            FROM orders o LEFT JOIN users u ON o.customer_id = u.user_id 
            ORDER BY o.created_at DESC
        """, orderMapper);
    }

    public List<OrderItem> findItemsByOrderId(Long orderId) {
        return jdbc.query("SELECT * FROM order_items WHERE order_id=?", itemMapper, orderId);
    }

    public void updateStatus(String orderId, String status) {
        jdbc.update("UPDATE orders SET status=? WHERE order_id=?", status, orderId);
    }
}

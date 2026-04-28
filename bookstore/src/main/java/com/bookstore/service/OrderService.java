package com.bookstore.service;

import com.bookstore.model.*;
import com.bookstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        for (Order o : orders) {
            o.setItems(orderRepository.findItemsByOrderId(o.getId()));
        }
        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        for (Order o : orders) {
            o.setItems(orderRepository.findItemsByOrderId(o.getId()));
        }
        return orders;
    }

    public boolean placeOrder(String customerId, List<CartItem> cartItems, String paymentMethod) {
        double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        String orderId = "ORD" + System.currentTimeMillis();

        Order order = new Order(orderId, customerId, total, paymentMethod);
        order.setStatus("Completed");

        Long generatedId = orderRepository.saveOrder(order);

        for (CartItem item : cartItems) {
            OrderItem oi = new OrderItem(generatedId, item.getBookId(), item.getTitle(), item.getQuantity(), item.getPrice());
            orderRepository.saveOrderItem(oi);
            // Reduce stock for physical books
            if (!item.isEbook()) {
                bookRepository.findByBookId(item.getBookId()).ifPresent(book -> {
                    int newStock = Math.max(0, book.getStock() - item.getQuantity());
                    bookRepository.updateStock(item.getBookId(), newStock);
                });
            }
        }
        return true;
    }

    public void updateStatus(String orderId, String status) {
        orderRepository.updateStatus(orderId, status);
    }
}

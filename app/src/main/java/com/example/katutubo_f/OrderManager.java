package com.example.katutubo_f;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private static OrderManager instance;
    private List<Order> orders;

    private OrderManager() {
        orders = new ArrayList<>();
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void addOrder(Order order) {
        orders.add(0, order); // Add to beginning
    }

    public void cancelOrder(String orderId, String reason, String time) {
        for (Order order : orders) {
            if (order.orderId.equals(orderId)) {
                order.status = "Cancelled";
                order.cancellationReason = reason;
                order.cancellationTime = time;
                break;
            }
        }
    }

    public List<Order> getOrders() {
        return orders;
    }

    public static class Order {
        public String orderId;
        public String status; // "To Ship", "To Receive", "Completed", "Cancelled"
        public String total;
        public String paymentMethod;
        public String timestamp;
        
        // Cancellation details
        public String cancellationReason;
        public String cancellationTime;
        public List<OrderItem> items = new ArrayList<>();

        public Order(String orderId, String status, String total, String paymentMethod, String timestamp) {
            this.orderId = orderId;
            this.status = status;
            this.total = total;
            this.paymentMethod = paymentMethod;
            this.timestamp = timestamp;
        }
    }

    public static class OrderItem {
        public String name;
        public String variant;
        public String price;
        public int quantity;
        public int imageResId;

        public OrderItem(String name, String variant, String price, int quantity, int imageResId) {
            this.name = name;
            this.variant = variant;
            this.price = price;
            this.quantity = quantity;
            this.imageResId = imageResId;
        }
    }
}
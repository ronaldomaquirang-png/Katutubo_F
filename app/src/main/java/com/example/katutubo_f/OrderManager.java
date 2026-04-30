package com.example.katutubo_f;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private static OrderManager instance;
    private List<Order> orders;

    private OrderManager() {
        orders = new ArrayList<>();
        // Adding some dummy data for testing if needed
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

    public void cancelOrder(String orderId) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).orderId.equals(orderId)) {
                orders.get(i).status = "Cancelled";
                // Optionally remove it or keep it with 'Cancelled' status
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

        public Order(String orderId, String status, String total, String paymentMethod, String timestamp) {
            this.orderId = orderId;
            this.status = status;
            this.total = total;
            this.paymentMethod = paymentMethod;
            this.timestamp = timestamp;
        }
    }
}
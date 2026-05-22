package com.example.katutubo_f;

import java.util.ArrayList;
import java.util.List;

public class Order {
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

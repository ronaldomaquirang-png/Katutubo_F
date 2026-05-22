package com.example.katutubo_f;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private static OrderManager instance;
    private final List<Order> orders;

    private OrderManager(Context context) {
        orders = new ArrayList<>();
    }

    public static synchronized OrderManager getInstance(Context context) {
        if (instance == null) {
            instance = new OrderManager(context.getApplicationContext());
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
}

package com.example.katutubo_f;

public class OrderItem {
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

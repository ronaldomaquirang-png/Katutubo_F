package com.example.katutubo_f;

public class CartItem {
    public String title;
    public String price;
    public int imageResource;
    public int quantity;
    public boolean isSelected;

    public CartItem(String title, String price, int imageResource, int quantity) {
        this.title = title;
        this.price = price;
        this.imageResource = imageResource;
        this.quantity = quantity;
        this.isSelected = true; // Default to selected when added to cart
    }
}

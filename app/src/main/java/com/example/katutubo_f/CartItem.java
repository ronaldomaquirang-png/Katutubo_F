package com.example.katutubo_f;

public class CartItem {
    public String title;
    public String price;
    public int imageResource;
    public int quantity;

    public CartItem(String title, String price, int imageResource, int quantity) {
        this.title = title;
        this.price = price;
        this.imageResource = imageResource;
        this.quantity = quantity;
    }
}

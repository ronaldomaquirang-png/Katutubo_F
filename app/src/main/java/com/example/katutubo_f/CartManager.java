package com.example.katutubo_f;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(CartItem item) {
        for (CartItem existingItem : cartItems) {
            if (existingItem.title.equals(item.title)) {
                existingItem.quantity += item.quantity;
                return;
            }
        }
        cartItems.add(item);
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
    }

    public void incrementQuantity(CartItem item) {
        item.quantity++;
    }

    public void decrementQuantity(CartItem item) {
        if (item.quantity > 1) {
            item.quantity--;
        } else {
            removeItem(item);
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : cartItems) {
            try {
                String priceStr = item.price.replace("₱", "").replace(",", "").trim();
                total += Double.parseDouble(priceStr) * item.quantity;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public double getShippingFee() {
        return cartItems.isEmpty() ? 0 : 50.0;
    }

    public double getGrandTotal() {
        return getTotalAmount() + getShippingFee();
    }

    public void clearCart() {
        cartItems.clear();
    }

    public static class CartItem {
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
}
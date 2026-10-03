package com.example.katutubo_f;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems;

    private CartManager(Context context) {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
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
            if (item.isSelected) {
                try {
                    String priceStr = item.price.replace("₱", "").replace(",", "").trim();
                    total += Double.parseDouble(priceStr) * item.quantity;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return total;
    }

    public double getShippingFee() {
        boolean hasSelected = false;
        for (CartItem item : cartItems) {
            if (item.isSelected) {
                hasSelected = true;
                break;
            }
        }
        return hasSelected ? 50.0 : 0;
    }

    public double getGrandTotal() {
        return getTotalAmount() + getShippingFee();
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<CartItem> getSelectedItems() {
        List<CartItem> selected = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected) {
                selected.add(item);
            }
        }
        return selected;
    }

    public void removeSelectedItems() {
        java.util.Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.isSelected) {
                iterator.remove();
            }
        }
    }
}

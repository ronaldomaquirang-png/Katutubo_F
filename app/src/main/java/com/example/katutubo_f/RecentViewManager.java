package com.example.katutubo_f;

import java.util.ArrayList;
import java.util.List;

public class RecentViewManager {
    private static RecentViewManager instance;
    private List<Product> recentProducts;
    private static final int MAX_RECENT_PRODUCTS = 10;

    private RecentViewManager() {
        recentProducts = new ArrayList<>();
    }

    public static synchronized RecentViewManager getInstance() {
        if (instance == null) {
            instance = new RecentViewManager();
        }
        return instance;
    }

    public void addProduct(Product product) {
        // Remove if already exists to move it to the top
        for (int i = 0; i < recentProducts.size(); i++) {
            if (recentProducts.get(i).title.equals(product.title)) {
                recentProducts.remove(i);
                break;
            }
        }
        
        // Add to the beginning of the list
        recentProducts.add(0, product);
        
        // Keep only the most recent ones
        if (recentProducts.size() > MAX_RECENT_PRODUCTS) {
            recentProducts.remove(recentProducts.size() - 1);
        }
    }

    public List<Product> getRecentProducts() {
        return recentProducts;
    }

    public static class Product {
        public String title;
        public String price;
        public String description;
        public int imageResource;

        public Product(String title, String price, String description, int imageResource) {
            this.title = title;
            this.price = price;
            this.description = description;
            this.imageResource = imageResource;
        }
    }
}
package com.example.katutubo_f;

import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static FavoriteManager instance;
    private List<Product> favoriteProducts;

    private FavoriteManager() {
        favoriteProducts = new ArrayList<>();
    }

    public static synchronized FavoriteManager getInstance() {
        if (instance == null) {
            instance = new FavoriteManager();
        }
        return instance;
    }

    public void toggleFavorite(Product product) {
        if (isFavorite(product)) {
            removeFavorite(product);
        } else {
            addFavorite(product);
        }
    }

    private void addFavorite(Product product) {
        if (!isFavorite(product)) {
            favoriteProducts.add(product);
        }
    }

    private void removeFavorite(Product product) {
        for (int i = 0; i < favoriteProducts.size(); i++) {
            if (favoriteProducts.get(i).title.equals(product.title)) {
                favoriteProducts.remove(i);
                break;
            }
        }
    }

    public boolean isFavorite(Product product) {
        for (Product p : favoriteProducts) {
            if (p.title.equals(product.title)) {
                return true;
            }
        }
        return false;
    }

    public List<Product> getFavoriteProducts() {
        return favoriteProducts;
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
package com.example.katutubo_f;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static FavoriteManager instance;
    private final List<FavoriteProduct> favoriteProducts;

    private FavoriteManager(Context context) {
        favoriteProducts = new ArrayList<>();
    }

    public static synchronized FavoriteManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoriteManager(context.getApplicationContext());
        }
        return instance;
    }

    public void toggleFavorite(FavoriteProduct product) {
        if (isFavorite(product)) {
            removeFavorite(product);
        } else {
            addFavorite(product);
        }
    }

    private void addFavorite(FavoriteProduct product) {
        if (!isFavorite(product)) {
            favoriteProducts.add(product);
        }
    }

    private void removeFavorite(FavoriteProduct product) {
        for (int i = 0; i < favoriteProducts.size(); i++) {
            if (favoriteProducts.get(i).title.equals(product.title)) {
                favoriteProducts.remove(i);
                break;
            }
        }
    }

    public boolean isFavorite(FavoriteProduct product) {
        for (FavoriteProduct p : favoriteProducts) {
            if (p.title.equals(product.title)) {
                return true;
            }
        }
        return false;
    }

    public List<FavoriteProduct> getFavoriteProducts() {
        return favoriteProducts;
    }
}

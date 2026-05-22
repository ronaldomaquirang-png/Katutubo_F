package com.example.katutubo_f;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class RecentViewManager {
    private static RecentViewManager instance;
    private List<RecentProduct> recentProducts;
    private static final int MAX_RECENT_PRODUCTS = 10;

    private RecentViewManager(Context context) {
        recentProducts = new ArrayList<>();
    }

    public static synchronized RecentViewManager getInstance(Context context) {
        if (instance == null) {
            instance = new RecentViewManager(context.getApplicationContext());
        }
        return instance;
    }

    public void addProduct(RecentProduct product) {
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

    public List<RecentProduct> getRecentProducts() {
        return recentProducts;
    }
}

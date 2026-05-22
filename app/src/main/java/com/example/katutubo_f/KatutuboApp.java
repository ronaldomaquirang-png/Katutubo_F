package com.example.katutubo_f;

import android.app.Application;

public class KatutuboApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize managers with application context
        CartManager.getInstance(this);
        OrderManager.getInstance(this);
        RecentViewManager.getInstance(this);
        FavoriteManager.getInstance(this);
    }
}

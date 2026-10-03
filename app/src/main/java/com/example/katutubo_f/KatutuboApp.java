package com.example.katutubo_f;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class KatutuboApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Force Light Mode so dark system theme on devices does not distort app colors
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize managers with application context
        CartManager.getInstance(this);
        OrderManager.getInstance(this);
        RecentViewManager.getInstance(this);
        FavoriteManager.getInstance(this);
    }
}

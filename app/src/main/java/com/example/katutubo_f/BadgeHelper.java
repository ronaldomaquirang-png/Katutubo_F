package com.example.katutubo_f;

import androidx.core.content.ContextCompat;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BadgeHelper {
    public static void setupBadges(BottomNavigationView bottomNavigationView) {
        if (bottomNavigationView == null) return;

        // Setup Cart Badge
        BadgeDrawable cartBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
        int cartCount = 0;
        for (CartItem item : CartManager.getInstance(bottomNavigationView.getContext()).getCartItems()) {
            cartCount += item.quantity;
        }
        
        if (cartCount > 0) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(cartCount);
            cartBadge.setBackgroundColor(ContextCompat.getColor(bottomNavigationView.getContext(), android.R.color.holo_red_dark));
            cartBadge.setBadgeTextColor(ContextCompat.getColor(bottomNavigationView.getContext(), android.R.color.white));
        } else {
            bottomNavigationView.removeBadge(R.id.nav_cart);
        }

        // Setup Profile Badge (Orders)
        BadgeDrawable profileBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_profile);
        int orderCount = OrderManager.getInstance(bottomNavigationView.getContext()).getOrders().size();
        
        if (orderCount > 0) {
            profileBadge.setVisible(true);
            profileBadge.setNumber(orderCount);
            profileBadge.setBackgroundColor(ContextCompat.getColor(bottomNavigationView.getContext(), android.R.color.holo_red_dark));
            profileBadge.setBadgeTextColor(ContextCompat.getColor(bottomNavigationView.getContext(), android.R.color.white));
            
            // Also show badge for Notifications
            BadgeDrawable notificationBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_notifications);
            notificationBadge.setVisible(true);
            notificationBadge.setNumber(orderCount);
            notificationBadge.setBackgroundColor(ContextCompat.getColor(bottomNavigationView.getContext(), android.R.color.holo_red_dark));
            notificationBadge.setBadgeTextColor(ContextCompat.getColor(bottomNavigationView.getContext(), android.R.color.white));
        } else {
            profileBadge.setVisible(false);
            bottomNavigationView.removeBadge(R.id.nav_notifications);
        }
    }
}
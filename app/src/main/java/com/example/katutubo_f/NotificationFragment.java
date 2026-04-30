package com.example.katutubo_f;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.List;

public class NotificationFragment extends Fragment {

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        LinearLayout notificationContainer = view.findViewById(R.id.notification_container);
        TextView tvEmpty = view.findViewById(R.id.tv_empty_notifications);
        
        // Mark Notifications as the selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_notifications);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                
                if (id == R.id.nav_home) {
                    switchFragment(new HomeFragment());
                    return true;
                } else if (id == R.id.nav_cart) {
                    switchFragment(new CartFragment());
                    return true;
                } else if (id == R.id.nav_notifications) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    switchFragment(new ProfileFragment());
                    return true;
                }
                return false;
            }
        });

        // Load orders as notifications
        List<OrderManager.Order> orders = OrderManager.getInstance().getOrders();
        if (orders.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            for (OrderManager.Order order : orders) {
                addNotificationItem(notificationContainer, order);
            }
        }

        return view;
    }

    private void addNotificationItem(LinearLayout container, OrderManager.Order order) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(40, 40, 40, 40);
        itemLayout.setBackgroundResource(R.drawable.rounded_white_bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        itemLayout.setLayoutParams(params);
        itemLayout.setElevation(4f);

        TextView title = new TextView(getContext());
        title.setText("📦 " + order.status);
        title.setTextSize(18);
        title.setTextColor(getResources().getColor(R.color.katutubo_brown));
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView details = new TextView(getContext());
        String detailText = "Order ID: " + order.orderId + "\nTotal: " + order.total + "\nPayment: " + order.paymentMethod;
        details.setText(detailText);
        details.setTextColor(getResources().getColor(R.color.black));
        details.setPadding(0, 12, 0, 12);
        details.setLineSpacing(0, 1.2f);

        TextView time = new TextView(getContext());
        time.setText(order.timestamp);
        time.setTextSize(12);
        time.setTextColor(android.graphics.Color.GRAY);
        time.setGravity(android.view.Gravity.END);

        itemLayout.addView(title);
        itemLayout.addView(details);
        itemLayout.addView(time);
        
        container.addView(itemLayout);
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
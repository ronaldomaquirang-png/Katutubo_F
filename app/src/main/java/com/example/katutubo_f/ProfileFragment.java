package com.example.katutubo_f;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // --- ORDER STATUS CLICKS ---
        view.findViewById(R.id.to_pay).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("To Pay", "You have no pending payments.")));
        
        view.findViewById(R.id.to_ship).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("To Ship", "No items are currently being prepared for shipment.")));
        
        view.findViewById(R.id.to_receive).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("To Receive", "No incoming deliveries at the moment.")));
        
        view.findViewById(R.id.to_rate).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("To Rate", "No items waiting for your review.")));

        // --- MENU LIST CLICKS ---
        view.findViewById(R.id.my_favorites).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("My Favorites", "Your favorite items list is empty.")));
        
        view.findViewById(R.id.recently_viewed).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("Recently Viewed", "No recently viewed products.")));
        
        view.findViewById(R.id.my_account).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("My Account", "Account details and settings management.")));
        
        view.findViewById(R.id.help_centre).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("Help Centre", "How can we help you today?\nContact: support@katutubo.com")));

        // --- OTHER CLICKS ---
        view.findViewById(R.id.btn_view_history).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("Purchase History", "You haven't made any purchases yet.")));

        view.findViewById(R.id.logout_button).setOnClickListener(v -> {
            // Simple logout: return to home
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
        });

        // --- BOTTOM NAVIGATION SETUP ---
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_cart) {
                switchFragment(new CartFragment());
                return true;
            } else if (id == R.id.nav_notifications) {
                switchFragment(new NotificationFragment());
                return true;
            }
            return false;
        });

        return view;
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
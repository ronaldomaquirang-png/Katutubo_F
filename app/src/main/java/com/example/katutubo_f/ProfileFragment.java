package com.example.katutubo_f;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        LinearLayout loggedInLayout = view.findViewById(R.id.logged_in_layout);
        LinearLayout guestLayout = view.findViewById(R.id.guest_layout);
        Button logoutBtn = view.findViewById(R.id.logout_button);
        TextView profileName = view.findViewById(R.id.profile_name);

        if (currentUser != null) {
            loggedInLayout.setVisibility(View.VISIBLE);
            guestLayout.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
            profileName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getEmail());
        } else {
            loggedInLayout.setVisibility(View.GONE);
            guestLayout.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
        }

        view.findViewById(R.id.btn_login_profile).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        view.findViewById(R.id.btn_signup_profile).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SignUpActivity.class));
        });

        view.findViewById(R.id.to_pay).setOnClickListener(v -> 
            switchFragment(OrdersFragment.newInstance("To Pay")));
        
        view.findViewById(R.id.to_ship).setOnClickListener(v -> 
            switchFragment(OrdersFragment.newInstance("To Ship")));
        
        view.findViewById(R.id.to_receive).setOnClickListener(v -> 
            switchFragment(OrdersFragment.newInstance("To Receive")));

        view.findViewById(R.id.to_rate).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("To Rate", "No items waiting for your review.")));

        view.findViewById(R.id.my_favorites).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("My Favorites", "Your favorite items list is empty.")));
        
        view.findViewById(R.id.recently_viewed).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("Recently Viewed", "No recently viewed products.")));
        
        view.findViewById(R.id.my_account).setOnClickListener(v -> 
            switchFragment(new MyAccountFragment()));
        
        view.findViewById(R.id.help_centre).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("Help Centre", "How can we help you today?\nContact: support@katutubo.com")));

        view.findViewById(R.id.btn_view_history).setOnClickListener(v -> 
            switchFragment(SimpleDetailFragment.newInstance("Purchase History", "You haven't made any purchases yet.")));

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            switchFragment(new ProfileFragment());
        });

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

        updateBadges(view);

        return view;
    }

    private void updateBadges(View view) {
        TextView badgeToPay = view.findViewById(R.id.badge_to_pay);
        TextView badgeToShip = view.findViewById(R.id.badge_to_ship);
        TextView badgeToReceive = view.findViewById(R.id.badge_to_receive);
        TextView badgeToRate = view.findViewById(R.id.badge_to_rate);

        int toPayCount = 0;
        int toShipCount = 0;
        int toReceiveCount = 0;
        int toRateCount = 0;

        List<OrderManager.Order> orders = OrderManager.getInstance().getOrders();
        for (OrderManager.Order order : orders) {
            if (order.status.equalsIgnoreCase("To Pay")) toPayCount++;
            else if (order.status.equalsIgnoreCase("To Ship")) toShipCount++;
            else if (order.status.equalsIgnoreCase("To Receive")) toReceiveCount++;
            else if (order.status.equalsIgnoreCase("Completed")) toRateCount++;
        }

        setupBadge(badgeToPay, toPayCount);
        setupBadge(badgeToShip, toShipCount);
        setupBadge(badgeToReceive, toReceiveCount);
        setupBadge(badgeToRate, toRateCount);
    }

    private void setupBadge(TextView badge, int count) {
        if (badge != null) {
            if (count > 0) {
                badge.setText(String.valueOf(count));
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

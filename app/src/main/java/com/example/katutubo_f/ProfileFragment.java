package com.example.katutubo_f;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

        view.findViewById(R.id.btn_login_profile).setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), LoginActivity.class)));

        view.findViewById(R.id.btn_signup_profile).setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), SignUpActivity.class)));

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
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        BadgeHelper.setupBadges(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchFragmentNav(new HomeFragment());
                return true;
            } else if (id == R.id.nav_cart) {
                switchFragmentNav(new CartFragment());
                return true;
            } else if (id == R.id.nav_notifications) {
                switchFragmentNav(new NotificationFragment());
                return true;
            }
            return false;
        });

        updateBadges(view);
        setupPromotions(view);
        setupRecentViews(view);
        setupRecommendations(view);

        return view;
    }

    private void setupPromotions(View view) {
        LinearLayout promoContainer = view.findViewById(R.id.profile_promo_container);
        if (promoContainer == null) return;
        promoContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        String[] names = {"Ompák", "Sonnod", "Inabal", "Bukag"};
        int[] images = {R.drawable.ompak, R.drawable.sonnod, R.drawable.inaball, R.drawable.bukag};
        String[] prices = {"₱750.00", "₱600.00", "₱1,400.00", "₱150.00"};
        String[] original = {"₱1,500", "₱1,200", "₱2,800", "₱300"};
        String[] descriptions = {
                "Bagobo Tagabawa — Bansalan, Digos City",
                "Bagobo Tagabawa — Bansalan, Digos City",
                "Bagobo Tagabawa — Bansalan, Digos City",
                "Tagakaolo"
        };
        String[] artisans = {"Artisan Handcrafted", "Master Weaver", "Traditional Weavers", "Basket Weavers Guild"};
        String[] materials = {"Abaca fiber", "Handwoven cotton", "Hand-dyed Abaca", "Bamboo, Rattan"};
        String[] inspirations = {"Ancestral patterns", "Celebration attire", "Sacred designs", "Harvest tradition"};

        for (int i = 0; i < names.length; i++) {
            View itemView = inflater.inflate(R.layout.item_discounted_product, promoContainer, false);
            ImageView img = itemView.findViewById(R.id.discount_image);
            TextView name = itemView.findViewById(R.id.discount_name);
            TextView price = itemView.findViewById(R.id.discount_price);
            TextView orig = itemView.findViewById(R.id.original_price);
            TextView tag = itemView.findViewById(R.id.discount_tag);

            final String finalName = names[i];
            final String finalPrice = prices[i];
            final String finalDesc = descriptions[i];
            final int finalImage = images[i];
            final String finalArtisan = artisans[i];
            final String finalMaterial = materials[i];
            final String finalInspiration = inspirations[i];

            if (img != null) img.setImageResource(images[i]);
            if (name != null) name.setText(names[i]);
            if (price != null) price.setText(prices[i]);
            if (orig != null) {
                orig.setText(original[i]);
                orig.setPaintFlags(orig.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (tag != null) tag.setText("-50%");

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(finalName, finalPrice, finalDesc, finalImage, finalArtisan, finalMaterial, finalInspiration)));

            promoContainer.addView(itemView);
        }
    }

    private void setupRecentViews(View view) {
        LinearLayout recentViewsSection = view.findViewById(R.id.profile_recent_views_section);
        LinearLayout recentViewsContainer = view.findViewById(R.id.profile_recent_views_container);
        
        if (recentViewsSection == null || recentViewsContainer == null) return;

        List<RecentProduct> recentProducts = RecentViewManager.getInstance(requireContext()).getRecentProducts();
        
        if (recentProducts == null || recentProducts.isEmpty()) {
            recentViewsSection.setVisibility(View.GONE);
            return;
        }

        recentViewsSection.setVisibility(View.VISIBLE);
        recentViewsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (RecentProduct p : recentProducts) {
            View itemView = inflater.inflate(R.layout.item_discounted_product, recentViewsContainer, false);
            ImageView img = itemView.findViewById(R.id.discount_image);
            TextView name = itemView.findViewById(R.id.discount_name);
            TextView price = itemView.findViewById(R.id.discount_price);
            TextView orig = itemView.findViewById(R.id.original_price);
            TextView tag = itemView.findViewById(R.id.discount_tag);

            if (img != null) img.setImageResource(p.imageResource);
            if (name != null) name.setText(p.title);
            if (price != null) price.setText(p.price);
            if (orig != null) orig.setVisibility(View.GONE);
            if (tag != null) tag.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(p.title, p.price, p.description, p.imageResource, "Traditional Artisan", "Natural Materials", "Indigenous Culture")));

            recentViewsContainer.addView(itemView);
        }
    }

    private void setupRecommendations(View view) {
        LinearLayout recContainer = view.findViewById(R.id.profile_recommendations_container);
        if (recContainer == null) return;
        recContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        String[] names = {"Bong an tidas", "Malong", "Dagmay cloth", "Suwat", "Balyog"};
        int[] images = {R.drawable.bongantidas, R.drawable.malong, R.drawable.dagmayy, R.drawable.suwat, R.drawable.balyog};
        String[] prices = {"₱150.00", "₱850.00", "₱2,500.00", "₱350.00", "₱550.00"};
        String[] descriptions = {
                "Tagakaolo — Malalag, Sta. Maria",
                "Tagakaolo — Malalag, Sta. Maria",
                "Tagakaolo — Malalag, Sta. Maria",
                "Tagakaolo",
                "Tagakaolo"
        };
        String[] artisans = {"Local Community", "Indigenous Artists", "Elder Weavers", "Wood Artisans", "Beadwork Specialists"};
        String[] materials = {"Traditional fabric", "Woven cotton", "Manila Hemp (Abaca)", "Bamboo, Beads", "Seed beads"};
        String[] inspirations = {"Tribal symbols", "Daily utility", "Mandaya dreams", "Grooming rituals", "Protection charms"};

        for (int i = 0; i < names.length; i++) {
            View itemView = inflater.inflate(R.layout.item_discounted_product, recContainer, false);
            ImageView img = itemView.findViewById(R.id.discount_image);
            TextView name = itemView.findViewById(R.id.discount_name);
            TextView price = itemView.findViewById(R.id.discount_price);
            TextView orig = itemView.findViewById(R.id.original_price);
            TextView tag = itemView.findViewById(R.id.discount_tag);

            final String finalName = names[i];
            final String finalPrice = prices[i];
            final String finalDesc = descriptions[i];
            final int finalImage = images[i];
            final String finalArtisan = artisans[i];
            final String finalMaterial = materials[i];
            final String finalInspiration = inspirations[i];

            if (img != null) img.setImageResource(images[i]);
            if (name != null) name.setText(names[i]);
            if (price != null) price.setText(prices[i]);
            if (orig != null) orig.setVisibility(View.GONE);
            if (tag != null) tag.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(finalName, finalPrice, finalDesc, finalImage, finalArtisan, finalMaterial, finalInspiration)));

            recContainer.addView(itemView);
        }
    }

    private void updateBadges(View view) {
        TextView badgeToPay = view.findViewById(R.id.badge_to_pay);
        TextView badgeToShip = view.findViewById(R.id.badge_to_ship);
        TextView badgeToReceive = view.findViewById(R.id.badge_to_receive);
        TextView badgeToRate = view.findViewById(R.id.badge_to_rate);
        TextView badgeMyPurchases = view.findViewById(R.id.badge_my_purchases);

        int toPayCount = 0;
        int toShipCount = 0;
        int toReceiveCount = 0;
        int toRateCount = 0;

        List<Order> orders = OrderManager.getInstance(requireContext()).getOrders();
        for (Order order : orders) {
            if (order.status.equalsIgnoreCase("To Pay")) toPayCount++;
            else if (order.status.equalsIgnoreCase("To Ship")) toShipCount++;
            else if (order.status.equalsIgnoreCase("To Receive")) toReceiveCount++;
            else if (order.status.equalsIgnoreCase("Completed")) toRateCount++;
        }

        setupBadge(badgeToPay, toPayCount);
        setupBadge(badgeToShip, toShipCount);
        setupBadge(badgeToReceive, toReceiveCount);
        setupBadge(badgeToRate, toRateCount);
        setupBadge(badgeMyPurchases, orders.size());
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

    private void switchFragmentNav(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

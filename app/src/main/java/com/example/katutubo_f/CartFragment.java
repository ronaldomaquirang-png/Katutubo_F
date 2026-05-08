package com.example.katutubo_f;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.List;

public class CartFragment extends Fragment {

    private LinearLayout cartContainer;
    private TextView tvTotalAmount;
    private TextView tvEmptyCart;
    private BottomNavigationView bottomNavigationView;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartContainer = view.findViewById(R.id.cart_items_container);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        tvEmptyCart = view.findViewById(R.id.tv_empty_cart);
        Button btnCheckout = view.findViewById(R.id.btn_checkout);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        
        // Mark Cart as the selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        BadgeHelper.setupBadges(bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    switchFragment(new HomeFragment());
                    return true;
                } else if (id == R.id.nav_cart) {
                    return true;
                } else if (id == R.id.nav_notifications) {
                    switchFragment(new NotificationFragment());
                    return true;
                } else if (id == R.id.nav_profile) {
                    switchFragment(new ProfileFragment());
                    return true;
                }
                return false;
            }
        });

        loadCartItems();
        setupPromotions(view);
        setupRecentViews(view);
        setupRecommendations(view);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartManager.getInstance().getCartItems().isEmpty()) {
                    return;
                }
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CheckoutFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void loadCartItems() {
        cartContainer.removeAllViews();
        List<CartManager.CartItem> items = CartManager.getInstance().getCartItems();
        
        if (items.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            tvTotalAmount.setText("₱0.00");
            return;
        }

        tvEmptyCart.setVisibility(View.GONE);
        for (CartManager.CartItem item : items) {
            View itemView = getLayoutInflater().inflate(R.layout.item_cart, cartContainer, false);
            
            ImageView img = itemView.findViewById(R.id.cart_item_image);
            TextView title = itemView.findViewById(R.id.cart_item_title);
            TextView price = itemView.findViewById(R.id.cart_item_price);
            TextView qty = itemView.findViewById(R.id.cart_item_qty);
            Button btnPlus = itemView.findViewById(R.id.btn_plus);
            Button btnMinus = itemView.findViewById(R.id.btn_minus);
            ImageButton btnDelete = itemView.findViewById(R.id.btn_delete);
            
            img.setImageResource(item.imageResource);
            title.setText(item.title);
            price.setText(item.price);
            qty.setText(String.valueOf(item.quantity));

            btnPlus.setOnClickListener(v -> {
                CartManager.getInstance().incrementQuantity(item);
                loadCartItems();
            });

            btnMinus.setOnClickListener(v -> {
                CartManager.getInstance().decrementQuantity(item);
                loadCartItems();
            });

            btnDelete.setOnClickListener(v -> {
                CartManager.getInstance().removeItem(item);
                loadCartItems();
            });
            
            cartContainer.addView(itemView);
        }

        tvTotalAmount.setText(String.format("₱%,.2f", CartManager.getInstance().getTotalAmount()));
        
        // Update badges to reflect quantity changes
        if (bottomNavigationView != null) {
            BadgeHelper.setupBadges(bottomNavigationView);
        }
    }

    private void setupPromotions(View view) {
        LinearLayout promotionSection = view.findViewById(R.id.promotion_section);
        LinearLayout promoContainer = view.findViewById(R.id.promo_container);
        if (promotionSection != null) promotionSection.setVisibility(View.VISIBLE);
        if (promoContainer == null) return;
        promoContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        String[] names = {"Ompák", "Sonnod", "Inabal", "Bukag"};
        int[] images = {R.drawable.ompak, R.drawable.sonnod, R.drawable.inaball, R.drawable.bukag};
        String[] prices = {"₱750.00", "₱600.00", "₱1,400.00", "₱325.00"};
        String[] original = {"₱1,500", "₱1,200", "₱2,800", "₱650"};
        String[] descriptions = {
                "Bagobo Tagabawa — Bansalan, Digos City",
                "Bagobo Tagabawa — Bansalan, Digos City",
                "Bagobo Tagabawa — Bansalan, Digos City",
                "Tagakaolo"
        };

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

            if (img != null) img.setImageResource(images[i]);
            if (name != null) name.setText(names[i]);
            if (price != null) price.setText(prices[i]);
            if (orig != null) {
                orig.setText(original[i]);
                orig.setPaintFlags(orig.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (tag != null) tag.setText("-50%");

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(finalName, finalPrice, finalDesc, finalImage)));

            promoContainer.addView(itemView);
        }
    }

    private void setupRecentViews(View view) {
        LinearLayout recentViewsSection = view.findViewById(R.id.recent_views_section);
        LinearLayout recentViewsContainer = view.findViewById(R.id.recent_views_container);
        
        if (recentViewsSection == null || recentViewsContainer == null) return;

        List<RecentViewManager.Product> recentProducts = RecentViewManager.getInstance().getRecentProducts();
        
        if (recentProducts == null || recentProducts.isEmpty()) {
            recentViewsSection.setVisibility(View.GONE);
            return;
        }

        recentViewsSection.setVisibility(View.VISIBLE);
        recentViewsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (RecentViewManager.Product p : recentProducts) {
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

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(p.title, p.price, p.description, p.imageResource)));

            recentViewsContainer.addView(itemView);
        }
    }

    private void setupRecommendations(View view) {
        LinearLayout recommendationsSection = view.findViewById(R.id.recommendations_section);
        LinearLayout recContainer = view.findViewById(R.id.recommendations_container);
        if (recommendationsSection != null) recommendationsSection.setVisibility(View.VISIBLE);
        if (recContainer == null) return;
        recContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        // Different set of products for recommendations
        String[] names = {"Bong an tidas", "Malong", "Dagmay cloth", "Suwat", "Balyog"};
        int[] images = {R.drawable.bongantidas, R.drawable.malong, R.drawable.dagmayy, R.drawable.suwat, R.drawable.balyog};
        String[] prices = {"₱950.00", "₱850.00", "₱2,500.00", "₱350.00", "₱550.00"};
        String[] descriptions = {
                "Tagakaolo — Malalag, Sta. Maria",
                "Tagakaolo — Malalag, Sta. Maria",
                "Tagakaolo — Malalag, Sta. Maria",
                "Tagakaolo",
                "Tagakaolo"
        };

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

            if (img != null) img.setImageResource(images[i]);
            if (name != null) name.setText(names[i]);
            if (price != null) price.setText(prices[i]);
            if (orig != null) orig.setVisibility(View.GONE);
            if (tag != null) tag.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(finalName, finalPrice, finalDesc, finalImage)));

            recContainer.addView(itemView);
        }
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
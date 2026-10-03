package com.example.katutubo_f;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.Locale;

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

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchFragmentNav(new HomeFragment());
                return true;
            } else if (id == R.id.nav_cart) {
                return true;
            } else if (id == R.id.nav_notifications) {
                switchFragmentNav(new NotificationFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                switchFragmentNav(new ProfileFragment());
                return true;
            }
            return false;
        });

        loadCartItems(view);
        setupPromotions(view);
        setupRecentViews(view);
        setupRecommendations(view);

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance(requireContext()).getSelectedItems().isEmpty()) {
                android.widget.Toast.makeText(getContext(), "Please select items to checkout", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CheckoutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadCartItems(View rootView) {
        cartContainer.removeAllViews();
        List<CartItem> items = CartManager.getInstance(requireContext()).getCartItems();
        
        View view = rootView != null ? rootView : getView();
        if (view != null) {
            View promoSection = view.findViewById(R.id.promotion_section);
            View recSection = view.findViewById(R.id.recommendations_section);
            if (items.isEmpty()) {
                if (promoSection != null) promoSection.setVisibility(View.GONE);
                if (recSection != null) recSection.setVisibility(View.GONE);
            } else {
                if (promoSection != null) promoSection.setVisibility(View.VISIBLE);
                if (recSection != null) recSection.setVisibility(View.VISIBLE);
            }
        }

        if (items.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            tvTotalAmount.setText(R.string.price_zero);
            return;
        }

        tvEmptyCart.setVisibility(View.GONE);
        for (CartItem item : items) {
            View itemView = getLayoutInflater().inflate(R.layout.item_cart, cartContainer, false);
            
            android.widget.CheckBox checkBox = itemView.findViewById(R.id.cart_item_checkbox);
            ImageView img = itemView.findViewById(R.id.cart_item_image);
            TextView title = itemView.findViewById(R.id.cart_item_title);
            TextView price = itemView.findViewById(R.id.cart_item_price);
            TextView qty = itemView.findViewById(R.id.cart_item_qty);
            TextView btnPlus = itemView.findViewById(R.id.btn_plus);
            TextView btnMinus = itemView.findViewById(R.id.btn_minus);
            ImageButton btnDelete = itemView.findViewById(R.id.btn_delete);
            
            checkBox.setChecked(item.isSelected);
            img.setImageResource(item.imageResource);
            title.setText(item.title);
            price.setText(item.price);
            qty.setText(String.format(Locale.getDefault(), "%d", item.quantity));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.isSelected = isChecked;
                updateTotalAmountUI();
            });

            btnPlus.setOnClickListener(v -> {
                CartManager.getInstance(requireContext()).incrementQuantity(item);
                loadCartItems(rootView);
            });

            btnMinus.setOnClickListener(v -> {
                CartManager.getInstance(requireContext()).decrementQuantity(item);
                loadCartItems(rootView);
            });

            btnDelete.setOnClickListener(v -> {
                CartManager.getInstance(requireContext()).removeItem(item);
                loadCartItems(rootView);
            });
            
            cartContainer.addView(itemView);
        }

        updateTotalAmountUI();
        
        // Update badges to reflect quantity changes
        if (bottomNavigationView != null) {
            BadgeHelper.setupBadges(bottomNavigationView);
        }
    }

    private void updateTotalAmountUI() {
        double total = CartManager.getInstance(requireContext()).getGrandTotal();
        tvTotalAmount.setText(String.format(Locale.getDefault(), "₱%,.2f", total));
    }

    private void setupPromotions(View view) {
        LinearLayout promotionSection = view.findViewById(R.id.promotion_section);
        LinearLayout promoContainer = view.findViewById(R.id.promo_container);
        
        if (CartManager.getInstance(requireContext()).getCartItems().isEmpty()) {
            if (promotionSection != null) promotionSection.setVisibility(View.GONE);
            return;
        }
        
        if (promotionSection != null) promotionSection.setVisibility(View.VISIBLE);
        if (promoContainer == null) return;
        promoContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        String[] names = {"Ompák", "Sonnod", "Inabal", "Bukag"};
        int[] images = {R.drawable.ompak, R.drawable.sonnod, R.drawable.inaball, R.drawable.bukag};
        double[] originalPrices = {1500, 1200, 2800, 300};
        int[] discounts = {50, 40, 30, 25};
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
            TextView priceText = itemView.findViewById(R.id.discount_price);
            TextView orig = itemView.findViewById(R.id.original_price);
            TextView tag = itemView.findViewById(R.id.discount_tag);

            double original = originalPrices[i];
            int discountPercent = discounts[i];
            double discountedAmount = original * (discountPercent / 100.0);
            double finalPriceValue = original - discountedAmount;

            final String finalName = names[i];
            final String finalPrice = String.format(java.util.Locale.getDefault(), "₱%,.2f", finalPriceValue);
            final String finalDesc = descriptions[i];
            final int finalImage = images[i];
            final String finalArtisan = artisans[i];
            final String finalMaterial = materials[i];
            final String finalInspiration = inspirations[i];

            if (img != null) img.setImageResource(images[i]);
            if (name != null) name.setText(names[i]);
            if (priceText != null) priceText.setText(finalPrice);
            if (orig != null) {
                orig.setText(String.format(java.util.Locale.getDefault(), "₱%,.0f", original));
                orig.setPaintFlags(orig.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (tag != null) {
                tag.setText(String.format(java.util.Locale.getDefault(), "-%d%%", discountPercent));
            }

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(finalName, finalPrice, String.format(java.util.Locale.getDefault(), "₱%,.0f", original), finalDesc, finalImage, finalArtisan, finalMaterial, finalInspiration, null)));

            promoContainer.addView(itemView);
        }
    }

    private void setupRecentViews(View view) {
        LinearLayout recentViewsSection = view.findViewById(R.id.recent_views_section);
        LinearLayout recentViewsContainer = view.findViewById(R.id.recent_views_container);
        
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

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(p.title, p.price, null, p.description, p.imageResource, "Traditional Artisan", "Natural Materials", "Cultural Heritage", null)));

            recentViewsContainer.addView(itemView);
        }
    }

    private void setupRecommendations(View view) {
        LinearLayout recommendationsSection = view.findViewById(R.id.recommendations_section);
        LinearLayout recContainer = view.findViewById(R.id.recommendations_container);
        
        if (CartManager.getInstance(requireContext()).getCartItems().isEmpty()) {
            if (recommendationsSection != null) recommendationsSection.setVisibility(View.GONE);
            return;
        }

        if (recommendationsSection != null) recommendationsSection.setVisibility(View.VISIBLE);
        if (recContainer == null) return;
        recContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        // Different set of products for recommendations
        String[] names = {"Bong an tidas", "Malong", "Dagmay cloth", "Suwat", "Balyog"};
        int[] images = {R.drawable.bongantidas, R.drawable.malong, R.drawable.dagmayy, R.drawable.suwat, R.drawable.balyog};
        String[] prices = {"₱450.00", "₱450.00", "₱1,250.00", "₱250.00", "₱275.00"};
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
            TextView priceText = itemView.findViewById(R.id.discount_price);
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
            if (priceText != null) priceText.setText(prices[i]);
            if (orig != null) orig.setVisibility(View.GONE);
            if (tag != null) tag.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(finalName, finalPrice, null, finalDesc, finalImage, finalArtisan, finalMaterial, finalInspiration, null)));

            recContainer.addView(itemView);
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

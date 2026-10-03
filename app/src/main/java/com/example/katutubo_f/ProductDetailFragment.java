package com.example.katutubo_f;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ProductDetailFragment extends Fragment {

    private String title, price, originalPrice, description, artisan, materials, origin, history;
    private int imageResource;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(String title, String price, String description, int imageResource) {
        return newInstance(title, price, null, description, imageResource, null, null, null, null);
    }

    public static ProductDetailFragment newInstance(String title, String price, String description, int imageResource, String artisan, String materials, String origin) {
        return newInstance(title, price, null, description, imageResource, artisan, materials, origin, null);
    }

    public static ProductDetailFragment newInstance(String title, String price, String description, int imageResource, String artisan, String materials, String origin, String history) {
        return newInstance(title, price, null, description, imageResource, artisan, materials, origin, history);
    }

    public static ProductDetailFragment newInstance(String title, String price, String originalPrice, String description, int imageResource, String artisan, String materials, String origin, String history) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("price", price);
        args.putString("originalPrice", originalPrice);
        args.putString("description", description);
        args.putInt("imageResource", imageResource);
        args.putString("artisan", artisan);
        args.putString("materials", materials);
        args.putString("origin", origin);
        args.putString("history", history);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            price = getArguments().getString("price");
            originalPrice = getArguments().getString("originalPrice");
            description = getArguments().getString("description");
            imageResource = getArguments().getInt("imageResource");
            artisan = getArguments().getString("artisan");
            materials = getArguments().getString("materials");
            origin = getArguments().getString("origin");
            history = getArguments().getString("history");

            // Add to Recent Views
            RecentViewManager.getInstance(requireContext()).addProduct(
                new RecentProduct(title, price, description, imageResource)
            );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        TextView titleTxt = view.findViewById(R.id.productTitle);
        TextView priceTxt = view.findViewById(R.id.productPrice);
        TextView originalPriceTxt = view.findViewById(R.id.productOriginalPrice);
        TextView descTxt = view.findViewById(R.id.productDescription);
        TextView historyTxt = view.findViewById(R.id.productHistory);
        TextView artisanTxt = view.findViewById(R.id.productArtisan);
        TextView materialsTxt = view.findViewById(R.id.productMaterials);
        TextView originTxt = view.findViewById(R.id.productOrigin);
        ImageView productImg = view.findViewById(R.id.productImage);
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        Button addToCartBtn = view.findViewById(R.id.btn_add_to_cart);
        Button buyNowBtn = view.findViewById(R.id.btn_buy_now);
        CheckBox btnFavorite = view.findViewById(R.id.btn_favorite_detail);

        titleTxt.setText(title);
        priceTxt.setText(price);

        if (originalPriceTxt != null) {
            if (originalPrice != null && !originalPrice.isEmpty()) {
                originalPriceTxt.setText(originalPrice);
                originalPriceTxt.setPaintFlags(originalPriceTxt.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                originalPriceTxt.setVisibility(View.VISIBLE);
            } else {
                originalPriceTxt.setVisibility(View.GONE);
            }
        }

        descTxt.setText(description);
        
        if (historyTxt != null) {
            historyTxt.setText(history != null ? history : "This product carries the rich heritage of its makers, passed down through generations as a symbol of cultural identity and traditional craftsmanship.");
        }
        if (artisanTxt != null) {
            artisanTxt.setText(artisan != null ? artisan : "Traditional Artisan");
        }
        if (materialsTxt != null) {
            materialsTxt.setText(materials != null ? materials : "Natural materials");
        }
        if (originTxt != null) {
            originTxt.setText(origin != null ? origin : "Philippines");
        }

        if (imageResource != 0) {
            productImg.setImageResource(imageResource);
        }

        if (btnFavorite != null) {
            FavoriteProduct favProduct = new FavoriteProduct(title, price, description, imageResource);
            btnFavorite.setChecked(FavoriteManager.getInstance(requireContext()).isFavorite(favProduct));
            btnFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> FavoriteManager.getInstance(requireContext()).toggleFavorite(favProduct));
        }

        backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            BadgeHelper.setupBadges(bottomNavigationView);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    getParentFragmentManager().popBackStack();
                    return true;
                } else if (id == R.id.nav_cart) {
                    switchFragmentNav(new CartFragment());
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
        }

        addToCartBtn.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                showLoginPrompt("add items to your cart");
            } else {
                CartManager.getInstance(requireContext()).addToCart(
                    new CartItem(title, price, imageResource, 1)
                );
                
                // Update badges on the BottomNavigationView to reflect the change
                if (bottomNavigationView != null) {
                    BadgeHelper.setupBadges(bottomNavigationView);
                }
            }
        });

        buyNowBtn.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                showLoginPrompt("proceed with your purchase");
            } else {
                CartManager.getInstance(requireContext()).clearCart();
                CartManager.getInstance(requireContext()).addToCart(new CartItem(title, price, imageResource, 1));
                
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CheckoutFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void switchFragmentNav(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showLoginPrompt(String action) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Login Required")
                .setMessage("Please sign in to " + action + ".")
                .setPositiveButton("LOG IN", (dialog, which) -> {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }
}

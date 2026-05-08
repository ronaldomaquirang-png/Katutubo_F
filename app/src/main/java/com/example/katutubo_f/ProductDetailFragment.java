package com.example.katutubo_f;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
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

public class ProductDetailFragment extends Fragment {

    private String title, price, description;
    private int imageResource;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(String title, String price, String description, int imageResource) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("price", price);
        args.putString("description", description);
        args.putInt("imageResource", imageResource);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            price = getArguments().getString("price");
            description = getArguments().getString("description");
            imageResource = getArguments().getInt("imageResource");

            // Add to Recent Views
            RecentViewManager.getInstance().addProduct(
                new RecentViewManager.Product(title, price, description, imageResource)
            );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        TextView titleTxt = view.findViewById(R.id.productTitle);
        TextView priceTxt = view.findViewById(R.id.productPrice);
        TextView descTxt = view.findViewById(R.id.productDescription);
        ImageView productImg = view.findViewById(R.id.productImage);
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        Button addToCartBtn = view.findViewById(R.id.btn_add_to_cart);
        Button buyNowBtn = view.findViewById(R.id.btn_buy_now);
        CheckBox btnFavorite = view.findViewById(R.id.btn_favorite_detail);

        titleTxt.setText(title);
        priceTxt.setText(price);
        descTxt.setText(description);
        if (imageResource != 0) {
            productImg.setImageResource(imageResource);
        }

        if (btnFavorite != null) {
            FavoriteManager.Product favProduct = new FavoriteManager.Product(title, price, description, imageResource);
            btnFavorite.setChecked(FavoriteManager.getInstance().isFavorite(favProduct));
            btnFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
                FavoriteManager.getInstance().toggleFavorite(favProduct);
            });
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
                    switchFragment(new CartFragment());
                    return true;
                } else if (id == R.id.nav_notifications) {
                    switchFragment(new NotificationFragment());
                    return true;
                } else if (id == R.id.nav_profile) {
                    switchFragment(new ProfileFragment());
                    return true;
                }
                return false;
            });
        }

        addToCartBtn.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                showLoginPrompt("add items to your cart");
            } else {
                CartManager.getInstance().addToCart(
                    new CartManager.CartItem(title, price, imageResource, 1)
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
                CartManager.getInstance().clearCart();
                CartManager.getInstance().addToCart(new CartManager.CartItem(title, price, imageResource, 1));
                
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CheckoutFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
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
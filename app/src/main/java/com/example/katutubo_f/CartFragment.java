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
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        
        // Mark Cart as the selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);

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
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
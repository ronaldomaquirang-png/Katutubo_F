package com.example.katutubo_f;

import androidx.appcompat.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SimpleDetailFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";
    private String title;
    private String content;

    public static SimpleDetailFragment newInstance(String title, String content) {
        SimpleDetailFragment fragment = new SimpleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            content = getArguments().getString(ARG_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_detail, container, false);
        
        TextView titleTextView = view.findViewById(R.id.detail_title);
        TextView contentTextView = view.findViewById(R.id.detail_content);
        LinearLayout dynamicContainer = view.findViewById(R.id.dynamic_container);
        
        titleTextView.setText(title);
        contentTextView.setText(content);
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        if ("Recently Viewed".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayRecentProducts(dynamicContainer);
        } 
        else if ("My Favorites".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayFavoriteProducts(dynamicContainer);
        }
        else if ("To Pay".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, "To Pay");
        }
        else if ("To Ship".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, "To Ship");
        }
        else if ("To Receive".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, "To Receive");
        }
        else if ("Cancelled".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, "Cancelled");
        }
        else if ("Purchase History".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, ""); // Empty string means all orders
        }
        
        return view;
    }

    private void displayOrdersByStatus(LinearLayout container, String filterStatus) {
        container.removeAllViews();
        List<Order> orders = OrderManager.getInstance(requireContext()).getOrders();
        boolean found = false;
        
        for (Order order : orders) {
            if (filterStatus.isEmpty() || order.status.toLowerCase().contains(filterStatus.toLowerCase())) {
                found = true;
                View itemView = getLayoutInflater().inflate(R.layout.item_order, container, false);
                
                TextView tvId = itemView.findViewById(R.id.order_id);
                TextView tvStatus = itemView.findViewById(R.id.order_status);
                TextView tvTotal = itemView.findViewById(R.id.order_total);
                TextView tvDate = itemView.findViewById(R.id.order_date);
                Button btnCancel = itemView.findViewById(R.id.btn_cancel_order);
                Button btnBuyAgain = itemView.findViewById(R.id.btn_buy_again_item);

                tvId.setText("Order #" + order.orderId);
                tvStatus.setText(order.status);
                tvTotal.setText("Total: " + order.total);
                tvDate.setText("Date: " + order.timestamp);

                if (order.status.equalsIgnoreCase("To Pay") || order.status.equalsIgnoreCase("To Ship")) {
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setOnClickListener(v -> showCancelReasonDialog(order.orderId, container, filterStatus));
                } else if (order.status.equalsIgnoreCase("Cancelled") || order.status.equalsIgnoreCase("Completed")) {
                    btnBuyAgain.setVisibility(View.VISIBLE);
                    btnBuyAgain.setOnClickListener(v -> handleBuyAgain(order));
                }
                
                itemView.setOnClickListener(v -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, OrderDetailFragment.newInstance(order.orderId))
                            .addToBackStack(null)
                            .commit();
                });

                container.addView(itemView);
            }
        }
        
        if (!found) {
            showEmptyMessage(container, "No orders found.");
        }
    }

    private void handleBuyAgain(Order order) {
        if (order.items != null && !order.items.isEmpty()) {
            for (OrderItem item : order.items) {
                CartItem cartItem = new CartItem(
                        item.name,
                        item.price,
                        item.imageResId,
                        item.quantity
                );
                CartManager.getInstance(requireContext()).addToCart(cartItem);
            }
            Toast.makeText(getContext(), "Added " + order.items.size() + " items to cart", Toast.LENGTH_SHORT).show();
            
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CartFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(), "No items found in this order", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCancelReasonDialog(String orderId, LinearLayout container, String filterStatus) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cancel_reason, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        RadioGroup rgReasons = dialogView.findViewById(R.id.rg_cancel_reasons);
        EditText etOtherReason = dialogView.findViewById(R.id.et_other_reason);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit_cancel);
        TextView btnClose = dialogView.findViewById(R.id.btn_close_dialog);

        rgReasons.setOnCheckedChangeListener((group, checkedId) -> {
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.katutubo_orange)));
            btnSubmit.setTextColor(getResources().getColor(R.color.white));

            if (checkedId == R.id.reason_other) {
                etOtherReason.setVisibility(View.VISIBLE);
            } else {
                etOtherReason.setVisibility(View.GONE);
            }
        });

        btnSubmit.setOnClickListener(v -> {
            int selectedId = rgReasons.getCheckedRadioButtonId();
            RadioButton rb = dialogView.findViewById(selectedId);
            String reason;

            if (selectedId == R.id.reason_other) {
                reason = etOtherReason.getText().toString().trim();
                if (reason.isEmpty()) {
                    Toast.makeText(getContext(), "Please type your reason", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                reason = rb != null ? rb.getText().toString() : "No reason provided";
            }

            // Show confirmation dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Order")
                    .setMessage("Are you sure you want to cancel this order?")
                    .setPositiveButton("YES, CANCEL", (confirmDialog, which) -> {
                        String currentTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
                        OrderManager.getInstance(requireContext()).cancelOrder(orderId, reason, currentTime);

                        // Send Push Notification for Cancellation
                        NotificationHelper.sendOrderNotification(getContext(), "Order Cancelled ❌",
                                "Order #" + orderId + " has been successfully cancelled.");

                        displayOrdersByStatus(container, filterStatus);
                        dialog.dismiss();
                    })
                    .setNegativeButton("NO", null)
                    .show();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void displayRecentProducts(LinearLayout container) {
        List<RecentProduct> recentProducts = RecentViewManager.getInstance(requireContext()).getRecentProducts();
        if (recentProducts.isEmpty()) {
            showEmptyMessage(container, "No recently viewed products.");
            return;
        }
        for (RecentProduct product : recentProducts) {
            addProductItem(container, product.title, product.price, product.description, product.imageResource);
        }
    }

    private void displayFavoriteProducts(LinearLayout container) {
        List<FavoriteProduct> favoriteProducts = FavoriteManager.getInstance(requireContext()).getFavoriteProducts();
        if (favoriteProducts.isEmpty()) {
            showEmptyMessage(container, "Your favorites list is empty.");
            return;
        }
        for (FavoriteProduct product : favoriteProducts) {
            addProductItem(container, product.title, product.price, product.description, product.imageResource);
        }
    }

    private void addProductItem(LinearLayout container, String title, String price, String desc, int imgRes) {
        View itemView = getLayoutInflater().inflate(R.layout.item_recent_product, container, false);
        ImageView img = itemView.findViewById(R.id.recent_img);
        TextView tvTitle = itemView.findViewById(R.id.recent_title);
        TextView tvPrice = itemView.findViewById(R.id.recent_price);
        
        img.setImageResource(imgRes);
        tvTitle.setText(title);
        tvPrice.setText(price);
        
        itemView.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ProductDetailFragment.newInstance(title, price, desc, imgRes))
                .addToBackStack(null)
                .commit();
        });
        container.addView(itemView);
    }

    private void showEmptyMessage(LinearLayout container, String message) {
        TextView emptyMsg = new TextView(getContext());
        emptyMsg.setText(message);
        emptyMsg.setPadding(0, 100, 0, 0);
        emptyMsg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyMsg.setTextColor(getResources().getColor(R.color.katutubo_brown));
        container.addView(emptyMsg);
    }
}
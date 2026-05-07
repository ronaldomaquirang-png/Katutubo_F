package com.example.katutubo_f;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class OrdersFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private String title;
    private LinearLayout ordersContainer;
    private View emptyLayout;

    public static OrdersFragment newInstance(String title) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        
        TextView titleTextView = view.findViewById(R.id.orders_title);
        titleTextView.setText(title);
        
        ordersContainer = view.findViewById(R.id.orders_container);
        emptyLayout = view.findViewById(R.id.empty_orders_layout);
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        loadOrders();
        
        return view;
    }

    private void loadOrders() {
        ordersContainer.removeAllViews();
        List<OrderManager.Order> allOrders = OrderManager.getInstance().getOrders();
        boolean hasItems = false;

        for (OrderManager.Order order : allOrders) {
            // Flexible matching for status
            if (order.status.equalsIgnoreCase(title) || 
                (title.equals("Orders") && !order.status.isEmpty()) ||
                (title.equals("Purchase History")) ||
                (order.status.toLowerCase().contains(title.toLowerCase()))) {
                hasItems = true;
                addOrderView(order);
            }
        }

        if (hasItems) {
            emptyLayout.setVisibility(View.GONE);
            ordersContainer.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
            ordersContainer.setVisibility(View.GONE);
        }
    }

    private void addOrderView(OrderManager.Order order) {
        View orderView = getLayoutInflater().inflate(R.layout.item_order, ordersContainer, false);

        TextView tvId = orderView.findViewById(R.id.order_id);
        TextView tvStatus = orderView.findViewById(R.id.order_status);
        TextView tvTotal = orderView.findViewById(R.id.order_total);
        TextView tvDate = orderView.findViewById(R.id.order_date);
        Button btnCancel = orderView.findViewById(R.id.btn_cancel_order);
        Button btnBuyAgain = orderView.findViewById(R.id.btn_buy_again_item);

        tvId.setText("Order #" + order.orderId);
        tvStatus.setText(order.status);
        tvTotal.setText("Total: " + order.total);
        tvDate.setText("Date: " + order.timestamp);

        // Show "Cancel" only for pending orders
        if (order.status.equalsIgnoreCase("To Ship") || order.status.equalsIgnoreCase("To Pay")) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(v -> showCancelReasonDialog(order.orderId));
            btnBuyAgain.setVisibility(View.GONE);
        } 
        // Show "Buy Again" for Cancelled or Completed orders
        else if (order.status.equalsIgnoreCase("Cancelled") || order.status.equalsIgnoreCase("Completed")) {
            btnCancel.setVisibility(View.GONE);
            btnBuyAgain.setVisibility(View.VISIBLE);
            btnBuyAgain.setOnClickListener(v -> handleBuyAgain(order));
        } else {
            btnCancel.setVisibility(View.GONE);
            btnBuyAgain.setVisibility(View.GONE);
        }
        
        orderView.setOnClickListener(v -> {
            if (order.status.equalsIgnoreCase("Cancelled")) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, CancellationDetailFragment.newInstance(order.orderId))
                        .addToBackStack(null)
                        .commit();
            }
        });

        ordersContainer.addView(orderView);
    }

    private void handleBuyAgain(OrderManager.Order order) {
        if (order.items != null && !order.items.isEmpty()) {
            for (OrderManager.OrderItem item : order.items) {
                CartManager.CartItem cartItem = new CartManager.CartItem(
                        item.name,
                        item.price,
                        item.imageResId,
                        item.quantity
                );
                CartManager.getInstance().addToCart(cartItem);
            }
            Toast.makeText(getContext(), "Added " + order.items.size() + " items to cart", Toast.LENGTH_SHORT).show();
            
            // Navigate to Cart
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CartFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(), "No items found in this order", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCancelReasonDialog(String orderId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cancel_reason, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        RadioGroup rgReasons = dialogView.findViewById(R.id.rg_cancel_reasons);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit_cancel);
        TextView btnClose = dialogView.findViewById(R.id.btn_close_dialog);

        rgReasons.setOnCheckedChangeListener((group, checkedId) -> {
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.katutubo_orange)));
            btnSubmit.setTextColor(getResources().getColor(R.color.white));
        });

        btnSubmit.setOnClickListener(v -> {
            int selectedId = rgReasons.getCheckedRadioButtonId();
            RadioButton rb = dialogView.findViewById(selectedId);
            String reason = rb != null ? rb.getText().toString() : "No reason provided";
            
            String currentTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
            
            OrderManager.getInstance().cancelOrder(orderId, reason, currentTime);
            
            // Send Push Notification for Cancellation
            NotificationHelper.sendOrderNotification(getContext(), "Order Cancelled ❌", 
                "Order #" + orderId + " has been successfully cancelled.");

            loadOrders();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
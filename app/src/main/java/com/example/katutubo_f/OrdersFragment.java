package com.example.katutubo_f;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.List;

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
            if (order.status.contains(title)) {
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

        tvId.setText("Order #" + order.orderId);
        tvStatus.setText(order.status);
        tvTotal.setText("Total: " + order.total);
        tvDate.setText("Date: " + order.timestamp);

        if (order.status.contains("To Ship") || order.status.contains("To Pay")) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(v -> showCancelReasonDialog(order.orderId));
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        ordersContainer.addView(orderView);
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
            OrderManager.getInstance().cancelOrder(orderId);
            loadOrders();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
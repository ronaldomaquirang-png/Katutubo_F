package com.example.katutubo_f;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.List;

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
        
        return view;
    }

    private void displayOrdersByStatus(LinearLayout container, String filterStatus) {
        container.removeAllViews();
        List<OrderManager.Order> orders = OrderManager.getInstance().getOrders();
        boolean found = false;
        
        for (OrderManager.Order order : orders) {
            if (order.status.contains(filterStatus)) {
                found = true;
                View itemView = getLayoutInflater().inflate(R.layout.item_recent_product, container, false);
                TextView tvTitle = itemView.findViewById(R.id.recent_title);
                TextView tvPrice = itemView.findViewById(R.id.recent_price);
                ImageView img = itemView.findViewById(R.id.recent_img);

                img.setImageResource(R.drawable.logoooo);
                tvTitle.setText("Order #" + order.orderId);
                tvPrice.setText(order.total);
                
                LinearLayout parentLayout = (LinearLayout)tvTitle.getParent();
                
                TextView statusInfo = new TextView(getContext());
                statusInfo.setText(order.status);
                statusInfo.setTextColor(getResources().getColor(R.color.katutubo_orange));
                statusInfo.setTextSize(12);
                statusInfo.setPadding(0, 4, 0, 0);
                parentLayout.addView(statusInfo);

                if (order.status.contains("To Pay") || order.status.contains("To Ship")) {
                    Button btnCancel = new Button(getContext());
                    btnCancel.setText("Cancel Order");
                    btnCancel.setAllCaps(false);
                    btnCancel.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    btnCancel.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    btnCancel.setOnClickListener(v -> showCancelReasonDialog(order.orderId, container, filterStatus));
                    parentLayout.addView(btnCancel);
                }

                container.addView(itemView);
            }
        }
        
        if (!found) {
            showEmptyMessage(container, "No orders found in " + filterStatus);
        }
    }

    private void showCancelReasonDialog(String orderId, LinearLayout container, String filterStatus) {
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
            btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.katutubo_orange)));
            btnSubmit.setTextColor(getResources().getColor(R.color.white));
        });

        btnSubmit.setOnClickListener(v -> {
            OrderManager.getInstance().cancelOrder(orderId);
            displayOrdersByStatus(container, filterStatus);
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void displayRecentProducts(LinearLayout container) {
        List<RecentViewManager.Product> recentProducts = RecentViewManager.getInstance().getRecentProducts();
        if (recentProducts.isEmpty()) {
            showEmptyMessage(container, "No recently viewed products.");
            return;
        }
        for (RecentViewManager.Product product : recentProducts) {
            addProductItem(container, product.title, product.price, product.description, product.imageResource);
        }
    }

    private void displayFavoriteProducts(LinearLayout container) {
        List<FavoriteManager.Product> favoriteProducts = FavoriteManager.getInstance().getFavoriteProducts();
        if (favoriteProducts.isEmpty()) {
            showEmptyMessage(container, "Your favorites list is empty.");
            return;
        }
        for (FavoriteManager.Product product : favoriteProducts) {
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
package com.example.katutubo_f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CancellationDetailFragment extends Fragment {

    private String orderId;

    public static CancellationDetailFragment newInstance(String orderId) {
        CancellationDetailFragment fragment = new CancellationDetailFragment();
        Bundle args = new Bundle();
        args.putString("order_id", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString("order_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancellation_detail, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        TextView tvCancelTime = view.findViewById(R.id.tv_cancel_time);
        TextView tvCancelReason = view.findViewById(R.id.tv_cancel_reason);
        TextView tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        LinearLayout itemsContainer = view.findViewById(R.id.items_container);
        Button btnBuyAgain = view.findViewById(R.id.btn_buy_again);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Find the order from OrderManager
        Order order = null;
        for (Order o : OrderManager.getInstance(requireContext()).getOrders()) {
            if (o.orderId.equals(orderId)) {
                order = o;
                break;
            }
        }

        if (order != null) {
            final Order currentOrder = order;
            tvCancelTime.setText("Cancelled on " + (order.cancellationTime != null ? order.cancellationTime : order.timestamp));
            tvCancelReason.setText(order.cancellationReason != null ? order.cancellationReason : "No reason provided.");
            tvTotalAmount.setText(order.total);

            // Display all items in the order
            itemsContainer.removeAllViews();
            if (order.items != null && !order.items.isEmpty()) {
                for (OrderItem item : order.items) {
                    View itemView = inflater.inflate(R.layout.item_cancelled_product, itemsContainer, false);
                    
                    ImageView ivProductImage = itemView.findViewById(R.id.iv_product_image);
                    TextView tvProductName = itemView.findViewById(R.id.tv_product_name);
                    TextView tvProductVariant = itemView.findViewById(R.id.tv_product_variant);
                    TextView tvProductPrice = itemView.findViewById(R.id.tv_product_price);
                    TextView tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);

                    tvProductName.setText(item.name);
                    tvProductVariant.setText("Variation: " + item.variant);
                    tvProductPrice.setText(item.price);
                    tvProductQuantity.setText("x" + item.quantity);
                    ivProductImage.setImageResource(item.imageResId);

                    itemsContainer.addView(itemView);
                }
            } else {
                // Fallback if no items (shouldn't happen with real orders)
                TextView emptyMsg = new TextView(getContext());
                emptyMsg.setText("No item details available.");
                itemsContainer.addView(emptyMsg);
            }

            // Implement Buy Again functionality
            btnBuyAgain.setOnClickListener(v -> {
                if (currentOrder.items != null && !currentOrder.items.isEmpty()) {
                    for (OrderItem item : currentOrder.items) {
                        CartItem cartItem = new CartItem(
                                item.name,
                                item.price,
                                item.imageResId,
                                item.quantity
                        );
                        CartManager.getInstance(requireContext()).addToCart(cartItem);
                    }
                    Toast.makeText(getContext(), "Items added to cart", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to Cart Fragment
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new CartFragment())
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "No items found to buy again", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }
}
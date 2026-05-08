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

public class OrderDetailFragment extends Fragment {

    private String orderId;

    public static OrderDetailFragment newInstance(String orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        TextView tvStatus = view.findViewById(R.id.tv_order_status);
        TextView tvOrderId = view.findViewById(R.id.tv_order_id);
        TextView tvOrderTime = view.findViewById(R.id.tv_order_time);
        LinearLayout cardCancellation = view.findViewById(R.id.card_cancellation);
        TextView tvCancelReason = view.findViewById(R.id.tv_cancel_reason);
        LinearLayout itemsContainer = view.findViewById(R.id.items_container);
        TextView tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = view.findViewById(R.id.tv_payment_method);
        Button btnAction = view.findViewById(R.id.btn_action);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        OrderManager.Order order = null;
        for (OrderManager.Order o : OrderManager.getInstance().getOrders()) {
            if (o.orderId.equals(orderId)) {
                order = o;
                break;
            }
        }

        if (order != null) {
            final OrderManager.Order currentOrder = order;
            tvStatus.setText(order.status);
            tvOrderId.setText("Order #" + order.orderId);
            tvOrderTime.setText("Placed on " + order.timestamp);
            tvTotalAmount.setText(order.total);
            tvPaymentMethod.setText(order.paymentMethod != null ? order.paymentMethod : "Not Specified");

            if (order.status.equalsIgnoreCase("Cancelled")) {
                cardCancellation.setVisibility(View.VISIBLE);
                tvCancelReason.setText(order.cancellationReason != null ? order.cancellationReason : "No reason provided.");
                btnAction.setText("Buy Again");
            } else {
                cardCancellation.setVisibility(View.GONE);
                btnAction.setText("Order Again");
            }

            itemsContainer.removeAllViews();
            if (order.items != null && !order.items.isEmpty()) {
                for (OrderManager.OrderItem item : order.items) {
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
            }

            btnAction.setOnClickListener(v -> {
                if (currentOrder.items != null && !currentOrder.items.isEmpty()) {
                    for (OrderManager.OrderItem item : currentOrder.items) {
                        CartManager.CartItem cartItem = new CartManager.CartItem(
                                item.name,
                                item.price,
                                item.imageResId,
                                item.quantity
                        );
                        CartManager.getInstance().addToCart(cartItem);
                    }
                    Toast.makeText(getContext(), "Items added to cart", Toast.LENGTH_SHORT).show();
                    
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new CartFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        return view;
    }
}
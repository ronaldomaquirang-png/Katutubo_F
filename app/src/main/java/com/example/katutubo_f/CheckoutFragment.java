package com.example.katutubo_f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutFragment extends Fragment {

    public CheckoutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        Button btnPlaceOrder = view.findViewById(R.id.btn_place_order);
        RadioGroup paymentMethods = view.findViewById(R.id.payment_methods);
        
        TextView tvSubtotal = view.findViewById(R.id.checkout_subtotal);
        TextView tvShippingFee = view.findViewById(R.id.checkout_shipping_fee);
        TextView tvTotalAmount = view.findViewById(R.id.checkout_total_amount);

        CartManager cartManager = CartManager.getInstance(requireContext());
        double subtotal = cartManager.getTotalAmount();
        double shippingFee = cartManager.getShippingFee();
        double total = cartManager.getGrandTotal();

        if (tvSubtotal != null) {
            tvSubtotal.setText(String.format(Locale.getDefault(), "₱%,.2f", subtotal));
        }
        if (tvShippingFee != null) {
            tvShippingFee.setText(String.format(Locale.getDefault(), "₱%,.2f", shippingFee));
        }
        if (tvTotalAmount != null) {
            tvTotalAmount.setText(String.format(Locale.getDefault(), "₱%,.2f", total));
        }

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        view.findViewById(R.id.address_container).setOnClickListener(v -> 
            switchFragment(new NewAddressFragment()));

        btnPlaceOrder.setOnClickListener(v -> {
            int selectedId = paymentMethods.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedPayment = view.findViewById(selectedId);
            String paymentMethod = selectedPayment.getText().toString();

            processOrder(paymentMethod, total);
        });

        return view;
    }

    private void processOrder(String paymentMethod, double totalAmount) {
        String orderId = "ORD" + (System.currentTimeMillis() % 100000);
        String timestamp = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(new Date());

        String initialStatus;
        if (paymentMethod.equalsIgnoreCase("Cash on Delivery")) {
            initialStatus = "To Pay";
        } else {
            initialStatus = "To Ship";
        }

        String formattedTotal = String.format(Locale.getDefault(), "₱%,.2f", totalAmount);
        Order newOrder = new Order(
            orderId, initialStatus, formattedTotal, paymentMethod, timestamp
        );
        
        // Populate items from cart
        List<OrderItem> orderItems = new ArrayList<>();
        List<CartItem> cartItems = CartManager.getInstance(requireContext()).getCartItems();
        
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                cartItem.title,
                "Standard", // Default variant
                cartItem.price,
                cartItem.quantity,
                cartItem.imageResource
            );
            orderItems.add(orderItem);
        }
        newOrder.items = orderItems;

        OrderManager.getInstance(requireContext()).addOrder(newOrder);

        // Send Push Notification
        NotificationHelper.sendOrderNotification(requireContext(), "Order Confirmed! 📦", 
            "Order " + orderId + " has been placed successfully. Total: " + formattedTotal);

        CartManager.getInstance(requireContext()).clearCart();
        switchFragmentNav(new ProfileFragment());
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

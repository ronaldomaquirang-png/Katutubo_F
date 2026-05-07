package com.example.katutubo_f;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckoutFragment extends Fragment {

    private static final String PAYMONGO_SECRET_KEY = "sk_test_6XvX56857Z6A4E3D5C0D44E9";
    private static final String TWILIO_ACCOUNT_SID = "ACc5b2a2595856c00d44e9572d4136a4e3";
    private static final String TWILIO_AUTH_TOKEN = "aeaa32fc082ff6d9cd7029c8d58d6f77";
    private static final String TWILIO_FROM_NUMBER = "+12513135896";

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

        double subtotal = CartManager.getInstance().getTotalAmount();
        double shippingFee = 50.0; // Fixed shipping fee
        double total = subtotal + shippingFee;

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
        String orderId = "ORD" + System.currentTimeMillis() % 100000;
        String timestamp = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 3);
        String arrivalStart = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 2);
        String arrivalEnd = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(cal.getTime());
        String estimatedArrival = arrivalStart + " - " + arrivalEnd;

        String initialStatus;
        if (paymentMethod.equals("Cash on Delivery")) {
            initialStatus = "To Pay";
        } else {
            initialStatus = "To Ship";
        }

        String formattedTotal = String.format(Locale.getDefault(), "₱%,.2f", totalAmount);
        OrderManager.Order newOrder = new OrderManager.Order(
            orderId, initialStatus, formattedTotal, paymentMethod, timestamp
        );
        
        // Populate items from cart
        List<OrderManager.OrderItem> orderItems = new ArrayList<>();
        for (CartManager.CartItem cartItem : CartManager.getInstance().getCartItems()) {
            orderItems.add(new OrderManager.OrderItem(
                cartItem.title,
                "Standard", // Default variant if not specified in cart
                cartItem.price,
                cartItem.quantity,
                cartItem.imageResource
            ));
        }
        newOrder.items = orderItems;

        OrderManager.getInstance().addOrder(newOrder);

        if (!paymentMethod.equals("Cash on Delivery")) {
            createPayMongoIntent((int) (totalAmount * 100)); 
        }

        // Send SMS Notification
        sendSmsNotification("+639918237465", "Katutubo Market: Order " + orderId + " placed via " + paymentMethod + "! Total: " + formattedTotal + ". Estimated arrival: " + estimatedArrival + ". Thank you!");

        // Send Push Notification
        NotificationHelper.sendOrderNotification(getContext(), "Order Confirmed! 📦", 
            "Order " + orderId + " has been placed successfully. Total: " + formattedTotal);

        Toast.makeText(getContext(), "Order Placed! Check status in Profile.", Toast.LENGTH_LONG).show();
        
        CartManager.getInstance().clearCart();
        switchFragment(new ProfileFragment());
    }

    private void createPayMongoIntent(int amountCents) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.paymongo.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        PayMongoService service = retrofit.create(PayMongoService.class);
        String auth = "Basic " + Base64.encodeToString((PAYMONGO_SECRET_KEY + ":").getBytes(), Base64.NO_WRAP);

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("amount", amountCents);
        attributes.put("currency", "PHP");
        attributes.put("payment_method_allowed", new String[]{"gcash", "paymaya"});
        data.put("attributes", attributes);
        Map<String, Object> body = new HashMap<>();
        body.put("data", data);

        service.createPaymentIntent(auth, body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) Log.d("API_SUCCESS", "PayMongo Created");
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("API_FAILURE", t.getMessage());
            }
        });
    }

    private void sendSmsNotification(String to, String message) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.twilio.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        TwilioService service = retrofit.create(TwilioService.class);
        String auth = "Basic " + Base64.encodeToString((TWILIO_ACCOUNT_SID + ":" + TWILIO_AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

        service.sendSms(TWILIO_ACCOUNT_SID, auth, to, TWILIO_FROM_NUMBER, message).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) Log.d("API_SUCCESS", "Twilio Sent");
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("API_FAILURE", t.getMessage());
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
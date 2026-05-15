package com.example.katutubo_f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UploadProductFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public UploadProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_product, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        EditText etName = view.findViewById(R.id.et_product_name);
        EditText etDesc = view.findViewById(R.id.et_product_desc);
        EditText etHistory = view.findViewById(R.id.et_product_history);
        EditText etPrice = view.findViewById(R.id.et_product_price);
        EditText etStock = view.findViewById(R.id.et_product_stock);
        Button btnPublish = view.findViewById(R.id.btn_publish);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnPublish.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String history = etHistory.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String stock = etStock.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty() || desc.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            String sellerId = mAuth.getCurrentUser().getUid();

            Map<String, Object> product = new HashMap<>();
            product.put("name", name);
            product.put("description", desc);
            product.put("history", history);
            product.put("price", "₱" + price);
            product.put("stock", stock);
            product.put("category", "Crafts"); // Default for now
            product.put("sellerId", sellerId);
            product.put("imageResource", R.drawable.logoooo); // Default placeholder

            db.collection("Products").add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Product Published Successfully!", Toast.LENGTH_LONG).show();
                        getParentFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        return view;
    }
}
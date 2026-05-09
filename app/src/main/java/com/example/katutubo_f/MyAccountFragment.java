package com.example.katutubo_f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyAccountFragment extends Fragment {

    private FirebaseAuth mAuth;

    public MyAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        TextView nameTv = view.findViewById(R.id.account_name);
        TextView emailTv = view.findViewById(R.id.account_email);
        TextView phoneTv = view.findViewById(R.id.account_phone);

        if (currentUser != null) {
            nameTv.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Katutubo User");
            emailTv.setText(currentUser.getEmail());
            phoneTv.setText(currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().isEmpty() 
                    ? currentUser.getPhoneNumber() : "Not Provided");
        }

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        view.findViewById(R.id.btn_my_addresses).setOnClickListener(v -> 
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new NewAddressFragment())
                .addToBackStack(null)
                .commit());

        view.setOnClickListener(v -> {
            // This is just to prevent clicks from passing through to fragments underneath
        });

        return view;
    }
}
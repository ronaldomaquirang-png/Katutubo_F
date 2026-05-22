package com.example.katutubo_f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MyAccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView nameTv, emailTv, phoneTv;

    public MyAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        nameTv = view.findViewById(R.id.account_name);
        emailTv = view.findViewById(R.id.account_email);
        phoneTv = view.findViewById(R.id.account_phone);

        updateUI(currentUser);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v -> showEditProfileDialog());

        view.findViewById(R.id.btn_my_addresses).setOnClickListener(v -> 
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new NewAddressFragment())
                .addToBackStack(null)
                .commit());

        view.findViewById(R.id.btn_change_password).setOnClickListener(v -> showChangePasswordDialog());

        view.setOnClickListener(v -> {
            // This is just to prevent clicks from passing through to fragments underneath
        });

        return view;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            nameTv.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty() 
                    ? user.getDisplayName() : "Katutubo User");
            emailTv.setText(user.getEmail());
            phoneTv.setText(user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() 
                    ? user.getPhoneNumber() : "Not Provided");
        }
    }

    private void showEditProfileDialog() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.et_edit_name);
        etName.setText(user.getDisplayName());

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    if (newName.isEmpty()) {
                        Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                            updateUI(mAuth.getCurrentUser());
                        } else {
                            Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        EditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String pass = etNewPassword.getText().toString();
                    String confirm = etConfirmPassword.getText().toString();

                    if (pass.length() < 6) {
                        Toast.makeText(getContext(), "Password too short", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!pass.equals(confirm)) {
                        Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.updatePassword(pass).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to change password. Re-login may be required.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
package com.example.katutubo_f;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText fullNameEdit, emailEdit, numberEdit, passwordEdit;
    private Button signUpBtn;
    private TextView backToLogin;
    private RelativeLayout loadingOverlay;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        
        // If already logged in, go to MainActivity
        if (mAuth.getCurrentUser() != null) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_signup);

        mFirestore = FirebaseFirestore.getInstance();

        fullNameEdit = findViewById(R.id.fullName);
        emailEdit = findViewById(R.id.signUpEmail);
        numberEdit = findViewById(R.id.signUpNumber);
        passwordEdit = findViewById(R.id.signUpPassword);
        signUpBtn = findViewById(R.id.signUpBtn);
        backToLogin = findViewById(R.id.backToLogin);
        loadingOverlay = findViewById(R.id.loading_overlay);

        signUpBtn.setOnClickListener(v -> registerUser());

        backToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = fullNameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String number = numberEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            fullNameEdit.setError("Full name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Email is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Enter a valid email");
            return;
        }
        if (TextUtils.isEmpty(number)) {
            numberEdit.setError("Phone number is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            passwordEdit.setError("Password must be at least 6 characters");
            return;
        }

        loadingOverlay.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update Firebase Profile with Name
                        com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            com.google.firebase.auth.UserProfileChangeRequest profileUpdates = new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates);
                        }
                        saveUserToFirestore(name, email, number);
                    } else {
                        loadingOverlay.setVisibility(View.GONE);
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication Failed";
                        Toast.makeText(SignUpActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String name, String email, String number) {
        if (mAuth.getCurrentUser() == null) {
            loadingOverlay.setVisibility(View.GONE);
            return;
        }
        
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", name);
        user.put("email", email);
        user.put("phoneNumber", number);
        user.put("userId", userId);

        mFirestore.collection("Users").document(userId).set(user)
                .addOnCompleteListener(task -> {
                    loadingOverlay.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        startMainActivity();
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Firestore Error";
                        Toast.makeText(SignUpActivity.this, "Account created, but profile save failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        // Still go to MainActivity since account is created
                        startMainActivity();
                    }
                });
    }

    private void startMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra("SKIP_ENTRANCE", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
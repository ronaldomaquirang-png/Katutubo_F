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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEdit, passwordEdit;
    private Button loginBtn;
    private View googleSignInBtn;
    private TextView signUpText;
    private FirebaseAuth mAuth;
    private RelativeLayout loadingOverlay;
    private Gmail gmailHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        
        // If already logged in, go to MainActivity
        if (mAuth.getCurrentUser() != null) {
            startMainActivity();
            return;
        }
        
        setContentView(R.layout.activity_login);

        loadingOverlay = findViewById(R.id.loading_overlay);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        signUpText = findViewById(R.id.signUpText);

        // Initialize Gmail helper with callbacks
        gmailHelper = new Gmail(this, new Gmail.GoogleCallback() {
            @Override
            public void onStart() {
                loadingOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess() {
                onLoginSuccess();
            }

            @Override
            public void onFailure(String error) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        loginBtn.setOnClickListener(v -> loginUser());
        googleSignInBtn.setOnClickListener(v -> gmailHelper.signIn());
        
        signUpText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gmailHelper.handleResult(requestCode, data);
    }

    private void onLoginSuccess() {
        loadingOverlay.setVisibility(View.GONE);
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Add flag to skip the entrance screen in MainActivity
        intent.putExtra("SKIP_ENTRANCE", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("Password is required");
            return;
        }

        loadingOverlay.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onLoginSuccess();
                    } else {
                        loadingOverlay.setVisibility(View.GONE);
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                        Toast.makeText(LoginActivity.this, "Login Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
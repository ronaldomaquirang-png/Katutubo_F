package com.example.katutubo_f;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";
    private EditText emailEdit, passwordEdit;
    private Button loginBtn;
    private TextView signUpText;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private RelativeLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Explicitly initialize Facebook SDK to prevent crash
        try {
            FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
            FacebookSdk.setClientToken(getString(R.string.facebook_client_token));
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Facebook SDK init failed", e);
        }

        mAuth = FirebaseAuth.getInstance();
        
        // Safer sign out
        try {
            mAuth.signOut();
            if (FacebookSdk.isInitialized()) {
                LoginManager.getInstance().logOut();
            }
        } catch (Exception e) {
            Log.e(TAG, "Initial sign out failed", e);
        }

        setContentView(R.layout.activity_login);

        loadingOverlay = findViewById(R.id.loading_overlay);
        mCallbackManager = CallbackManager.Factory.create();
        
        String webClientId = "11478091257-cilke8ufrotjqtqa19ibmtit14qhdskh.apps.googleusercontent.com";
        
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
                
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        try {
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull FacebookException error) {
                    loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Facebook Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Facebook callback registration failed", e);
        }

        LinearLayout customFacebookButton = findViewById(R.id.custom_facebook_button);
        customFacebookButton.setOnClickListener(v -> {
            try {
                loadingOverlay.setVisibility(View.VISIBLE);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, 
                        Arrays.asList("email", "public_profile"));
            } catch (Exception e) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(this, "Facebook Login currently unavailable", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout googleLoginButton = findViewById(R.id.google_login_button);
        googleLoginButton.setOnClickListener(v -> signInWithGoogle());

        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signUpText = findViewById(R.id.signUpText);

        loginBtn.setOnClickListener(v -> loginUser());
        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void signInWithGoogle() {
        loadingOverlay.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onLoginSuccess();
                    } else {
                        loadingOverlay.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Facebook Auth Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onLoginSuccess();
                    } else {
                        loadingOverlay.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Google Auth Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void onLoginSuccess() {
        loadingOverlay.setVisibility(View.GONE);
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                loadingOverlay.setVisibility(View.GONE);
                Log.e(TAG, "Google Sign In Failed. Status Code: " + e.getStatusCode(), e);
                Toast.makeText(this, "Google Login Failed (" + e.getStatusCode() + ")", Toast.LENGTH_LONG).show();
            }
        }
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
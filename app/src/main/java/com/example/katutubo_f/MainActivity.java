package com.example.katutubo_f;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private LinearLayout entranceScreen;
    private FirebaseAuth mAuth;
    private static final int PERMISSION_REQUEST_CODE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(this);

        // Request Notification Permission for Android 13+
        requestNotificationPermission();

        entranceScreen = findViewById(R.id.entrance_screen);
        Button enterBtn = findViewById(R.id.enterBtn);

        // Set initial fragment (Home)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT")
                    .commit();
        }

        // Check if we should skip the entrance screen (e.g., after login)
        boolean skipEntrance = getIntent().getBooleanExtra("SKIP_ENTRANCE", false);
        if (skipEntrance) {
            entranceScreen.setVisibility(View.GONE);
            showHomeAd();
        }

        enterBtn.setOnClickListener(v -> {
            // When clicking "ENTER MARKET", hide the entrance screen to reveal the home content (HomeFragment)
            entranceScreen.setVisibility(View.GONE);
            showHomeAd();
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void showHomeAd() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT");
            if (fragment instanceof HomeFragment) {
                ((HomeFragment) fragment).showAdPopup();
            }
        }, 500); // Wait for HomeFragment to settle
    }
}
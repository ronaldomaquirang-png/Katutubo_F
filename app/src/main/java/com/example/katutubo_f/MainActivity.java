package com.example.katutubo_f;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private LinearLayout entranceScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entranceScreen = findViewById(R.id.entrance_screen);
        Button enterBtn = findViewById(R.id.enterBtn);

        // Set initial fragment (Home)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT")
                    .commit();
        }

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the entrance/splash screen to reveal the market
                entranceScreen.setVisibility(View.GONE);
                
                // Show the ad popup now that we're entering the home screen
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT");
                if (fragment instanceof HomeFragment) {
                    ((HomeFragment) fragment).showAdPopup();
                }
            }
        });
    }
}
package com.example.katutubo_f;

import android.app.Application;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class KatutuboApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Facebook SDK once for the entire app
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
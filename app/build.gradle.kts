plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.katutubo_f"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.katutubo_f"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "PAYMONGO_SECRET_KEY", "\"sk_test_YOUR_KEY_HERE\"")
        buildConfigField("String", "TWILIO_ACCOUNT_SID", "\"ACc5b2a2595856c00d44e9572d4136a4e3\"")
        buildConfigField("String", "TWILIO_AUTH_TOKEN", "\"aeaa32fc082ff6d9cd7029c8d58d6f77\"")
        buildConfigField("String", "TWILIO_FROM_NUMBER", "\"+1234567890\"")
        buildConfigField("String", "FACEBOOK_APP_ID", "\"1870350397014009\"")
        buildConfigField("String", "FACEBOOK_APP_SECRET", "\"8bf62be37e45235d75ffaf91fff5b24c\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("com.facebook.android:facebook-login:latest.release")

    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
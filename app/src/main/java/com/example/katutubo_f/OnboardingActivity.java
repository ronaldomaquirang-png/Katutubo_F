package com.example.katutubo_f;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext, btnSkip;
    private LinearLayout indicatorContainer;
    private List<OnboardingSlide> slides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user has already seen onboarding
        if (hasSeenOnboarding()) {
            launchMainActivity();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        indicatorContainer = findViewById(R.id.indicatorContainer);

        setupSlides();
        setupIndicators();

        OnboardingAdapter adapter = new OnboardingAdapter(slides);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
                if (position == slides.size() - 1) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < slides.size()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                markOnboardingFinished();
                launchMainActivity();
            }
        });

        btnSkip.setOnClickListener(v -> {
            markOnboardingFinished();
            launchMainActivity();
        });
    }

    private void setupSlides() {
        slides = new ArrayList<>();
        slides.add(new OnboardingSlide("Welcome to Katutubo", "Discover authentic indigenous products from various tribes across the Philippines.", R.drawable.logoooo));
        slides.add(new OnboardingSlide("Cultural Heritage", "Every product tells a story of tradition, craftsmanship, and ancestral patterns.", R.drawable.inaball));
        slides.add(new OnboardingSlide("Support Artisans", "Directly support local weavers and craftsmen from Mindanao and beyond.", R.drawable.ompak));
        slides.add(new OnboardingSlide("Secure Shopping", "Easy checkout and reliable delivery of your favorite cultural treasures.", R.drawable.bukag));
        slides.add(new OnboardingSlide("Ready to Explore?", "Join our community in preserving and celebrating our rich cultural identity.", R.drawable.logoooo));
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[slides.size()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            indicatorContainer.addView(indicators[i]);
        }
    }

    private void updateIndicators(int index) {
        int childCount = indicatorContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicatorContainer.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            }
        }
    }

    private boolean hasSeenOnboarding() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return prefs.getBoolean("seen_onboarding", false);
    }

    private void markOnboardingFinished() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("seen_onboarding", true).apply();
    }

    private void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Inner Classes for Data and Adapter
    static class OnboardingSlide {
        String title, description;
        int image;
        OnboardingSlide(String title, String description, int image) {
            this.title = title; this.description = description; this.image = image;
        }
    }

    static class OnboardingAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
        private List<OnboardingSlide> slides;
        OnboardingAdapter(List<OnboardingSlide> slides) { this.slides = slides; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_slide, parent, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OnboardingSlide slide = slides.get(position);
            holder.tvTitle.setText(slide.title);
            holder.tvDescription.setText(slide.description);
            holder.imgSlide.setImageResource(slide.image);
        }
        @Override public int getItemCount() { return slides.size(); }
        static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription; ImageView imgSlide;
            ViewHolder(View v) { super(v);
                tvTitle = v.findViewById(R.id.tvTitle);
                tvDescription = v.findViewById(R.id.tvDescription);
                imgSlide = v.findViewById(R.id.imgSlide);
            }
        }
    }
}
package com.example.katutubo_f;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView catAll, catCrafts, catClothing, catAccessories, catHomeDecor;
    private LinearLayout productListContainer, discountedContainer;
    private List<Product> allProducts = new ArrayList<>();
    private EditText searchBar;
    private String currentCategory = "All";

    private Handler adHandler = new Handler(Looper.getMainLooper());
    private Runnable adRunnable;
    private ViewPager2 adViewPager;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        catAll = view.findViewById(R.id.category_all);
        catCrafts = view.findViewById(R.id.category_crafts);
        catClothing = view.findViewById(R.id.category_clothing);
        catAccessories = view.findViewById(R.id.category_accessories);
        catHomeDecor = view.findViewById(R.id.category_home_decor);
        productListContainer = view.findViewById(R.id.product_list_container);
        discountedContainer = view.findViewById(R.id.discounted_container);
        searchBar = view.findViewById(R.id.SearchBar);

        initProducts();
        displayProducts("All", "");
        setupDiscountedProducts();

        catAll.setOnClickListener(v -> {
            currentCategory = "All";
            displayProducts(currentCategory, searchBar.getText().toString());
        });
        catCrafts.setOnClickListener(v -> {
            currentCategory = "Crafts";
            displayProducts(currentCategory, searchBar.getText().toString());
        });
        catClothing.setOnClickListener(v -> {
            currentCategory = "Clothing";
            displayProducts(currentCategory, searchBar.getText().toString());
        });
        catAccessories.setOnClickListener(v -> {
            currentCategory = "Accessories";
            displayProducts(currentCategory, searchBar.getText().toString());
        });
        catHomeDecor.setOnClickListener(v -> {
            currentCategory = "Home Decor";
            displayProducts(currentCategory, searchBar.getText().toString());
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayProducts(currentCategory, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        BadgeHelper.setupBadges(bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_cart) {
                switchFragment(new CartFragment());
                return true;
            } else if (id == R.id.nav_notifications) {
                switchFragment(new NotificationFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                switchFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        view.post(() -> {
            setupHotPicks(view);
        });

        return view;
    }

    public void showAdPopup() {
        if (getContext() == null) return;
        
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ad);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        adViewPager = dialog.findViewById(R.id.ad_viewpager);
        TabLayout tabLayout = dialog.findViewById(R.id.ad_indicator);
        ImageButton closeBtn = dialog.findViewById(R.id.btn_close_ad);

        List<AdItem> ads = new ArrayList<>();
        ads.add(new AdItem("Special Offer!", "Get 50% off on all Handwoven textiles this weekend only!", R.drawable.handwoven, "Clothing", "Textiles"));
        ads.add(new AdItem("Craft Sale!", "Up to 30% off on authentic baskets and wood carvings.", R.drawable.bukag, "Crafts", "Baskets"));
        ads.add(new AdItem("Beaded Beauty", "Exclusive collection of traditional necklaces and belts.", R.drawable.sinubla, "Accessories", "Beads"));
        ads.add(new AdItem("Traditional Hats", "Complete your look with our authentic head cloths.", R.drawable.tangkuloo, "Clothing", "Hats"));
        ads.add(new AdItem("Home Decor", "Beautiful handwoven mats for your living space.", R.drawable.banig, "Crafts", "Mats"));

        AdAdapter adapter = new AdAdapter(ads, dialog);
        adViewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, adViewPager, (tab, position) -> {}).attach();

        // Auto-slide every 1 second
        adRunnable = new Runnable() {
            @Override
            public void run() {
                if (adViewPager != null) {
                    int currentItem = adViewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % ads.size();
                    adViewPager.setCurrentItem(nextItem, true);
                    adHandler.postDelayed(this, 1000);
                }
            }
        };
        adHandler.postDelayed(adRunnable, 1000);

        closeBtn.setOnClickListener(v -> {
            adHandler.removeCallbacks(adRunnable);
            dialog.dismiss();
        });

        dialog.setOnDismissListener(d -> adHandler.removeCallbacks(adRunnable));
        dialog.show();
    }

    private void setupDiscountedProducts() {
        if (discountedContainer == null) return;
        discountedContainer.removeAllViews();
        
        LayoutInflater inflater = LayoutInflater.from(getContext());
        String[] names = {"Ompák", "Sonnod", "Inabal", "Bukag"};
        int[] images = {R.drawable.ompak, R.drawable.sonnod, R.drawable.inaball, R.drawable.bukag};
        String[] prices = {"₱750.00", "₱600.00", "₱1,400.00", "₱325.00"};
        String[] original = {"₱1,500", "₱1,200", "₱2,800", "₱650"};
        String[] descriptions = {
            "Bagobo Tagabawa — Bansalan, Digos City",
            "Bagobo Tagabawa — Bansalan, Digos City",
            "Bagobo Tagabawa — Bansalan, Digos City",
            "Tagakaolo"
        };

        for (int i = 0; i < names.length; i++) {
            View itemView = inflater.inflate(R.layout.item_discounted_product, discountedContainer, false);
            ImageView img = itemView.findViewById(R.id.discount_image);
            TextView name = itemView.findViewById(R.id.discount_name);
            TextView price = itemView.findViewById(R.id.discount_price);
            TextView orig = itemView.findViewById(R.id.original_price);
            
            final String finalName = names[i];
            final String finalPrice = prices[i];
            final String finalDesc = descriptions[i];
            final int finalImage = images[i];

            img.setImageResource(images[i]);
            name.setText(names[i]);
            price.setText(prices[i]);
            orig.setText(original[i]);
            orig.setPaintFlags(orig.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(finalName, finalPrice, finalDesc, finalImage)));
            
            discountedContainer.addView(itemView);
        }
    }

    private void setupHotPicks(View view) {
        android.widget.GridLayout grid = view.findViewById(R.id.hot_picks_grid);
        if (grid == null) return;

        grid.removeAllViews();
        String[] labels = {"Baskets", "Textiles", "Beads", "Metal", "Wood", "Dress", "Hats", "Mats"};
        int[] images = {R.drawable.bukag, R.drawable.inabal, R.drawable.sinubla, R.drawable.panday, R.drawable.ukir, R.drawable.malong, R.drawable.tangkulo, R.drawable.banig};
        
        // Map labels to categories for filtering
        String[] categoryMapping = {"Crafts", "Clothing", "Accessories", "Crafts", "Crafts", "Clothing", "Clothing", "Crafts"};

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < labels.length; i++) {
            View itemView = inflater.inflate(R.layout.item_hot_pick, grid, false);
            ImageView img = itemView.findViewById(R.id.hot_pick_image);
            TextView txt = itemView.findViewById(R.id.hot_pick_label);
            img.setImageResource(images[i]);
            txt.setText(labels[i]);
            
            final String category = categoryMapping[i];
            final String query = labels[i];

            itemView.setOnClickListener(v -> {
                currentCategory = category;
                searchBar.setText(query);
                displayProducts(category, query);
            });

            grid.addView(itemView);
        }
    }

    private void initProducts() {
        allProducts.clear();
        allProducts.add(new Product("Ompák", "₱1,500.00", "Bagobo Tagabawa — Bansalan, Digos City", "Clothing", R.drawable.ompak));
        allProducts.add(new Product("Sonnod", "₱1,200.00", "Bagobo Tagabawa — Bansalan, Digos City", "Clothing", R.drawable.sonnod));
        allProducts.add(new Product("Inabal (Cloth)", "₱2,800.00", "Bagobo Tagabawa — Bansalan, Digos City", "Clothing", R.drawable.inaball));
        allProducts.add(new Product("Bong an tidas", "₱950.00", "Tagakaolo — Malalag, Sta. Maria", "Clothing", R.drawable.bongantidas));
        allProducts.add(new Product("Malong / Patadyong", "₱850.00", "Tagakaolo — Malalag, Sta. Maria", "Clothing", R.drawable.malong));
        allProducts.add(new Product("Dagmay cloth", "₱2,500.00", "Tagakaolo — Malalag, Sta. Maria", "Clothing", R.drawable.dagmayy));
        allProducts.add(new Product("Embroidered garments", "₱1,800.00", "Mandaya — Coastal areas", "Clothing", R.drawable.garments));
        allProducts.add(new Product("Tangkulo (head cloth)", "₱750.00", "Bagobo Tagabawa", "Clothing", R.drawable.tangkuloo));
        allProducts.add(new Product("Ukir (wood carving)", "₱3,500.00", "Bagobo Tagabawa", "Crafts", R.drawable.ukir));
        allProducts.add(new Product("Panday (metal crafting)", "₱4,200.00", "Bagobo Tagabawa", "Crafts", R.drawable.panday));
        allProducts.add(new Product("Bukag (basket weaving)", "₱650.00", "Tagakaolo", "Crafts", R.drawable.bukag));
        allProducts.add(new Product("Banig (mat weaving)", "₱1,200.00", "Tagakaolo", "Crafts", R.drawable.banig));
        allProducts.add(new Product("Suwat (comb)", "₱350.00", "Tagakaolo", "Accessories", R.drawable.suwat));
        allProducts.add(new Product("Balyog (necklace)", "₱550.00", "Tagakaolo", "Accessories", R.drawable.balyog));
        allProducts.add(new Product("Sinubla (beaded items)", "₱650.00", "Bagobo Tagabawa", "Accessories", R.drawable.sinubla));
        allProducts.add(new Product("Ginamay (beaded belt)", "₱1,100.00", "Bagobo Tagabawa", "Accessories", R.drawable.ginamay));
    }

    private void displayProducts(String category, String query) {
        resetCategoryStyles();
        if (catAll != null && category.equals("All")) catAll.setTypeface(null, Typeface.BOLD);
        else if (catCrafts != null && category.equals("Crafts")) catCrafts.setTypeface(null, Typeface.BOLD);
        else if (catClothing != null && category.equals("Clothing")) catClothing.setTypeface(null, Typeface.BOLD);
        else if (catAccessories != null && category.equals("Accessories")) catAccessories.setTypeface(null, Typeface.BOLD);
        else if (catHomeDecor != null && category.equals("Home Decor")) catHomeDecor.setTypeface(null, Typeface.BOLD);

        if (productListContainer != null) {
            productListContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            for (Product p : allProducts) {
                boolean matchesCategory = category.equals("All") || p.category.equals(category);
                boolean matchesQuery = query.isEmpty() || p.name.toLowerCase().contains(query.toLowerCase()) || p.description.toLowerCase().contains(query.toLowerCase());

                if (matchesCategory && matchesQuery) {
                    View productView = inflater.inflate(R.layout.item_product_card, productListContainer, false);
                    ImageView image = productView.findViewById(R.id.item_image);
                    TextView name = productView.findViewById(R.id.item_name);
                    TextView price = productView.findViewById(R.id.item_price);
                    TextView desc = productView.findViewById(R.id.item_desc);
                    CheckBox btnFavorite = productView.findViewById(R.id.btn_favorite);

                    if (image != null) image.setImageResource(p.imageResource);
                    if (name != null) name.setText(p.name);
                    if (price != null) price.setText(p.price);
                    if (desc != null) desc.setText(p.description);
                    
                    if (btnFavorite != null) {
                        FavoriteManager.Product favProduct = new FavoriteManager.Product(p.name, p.price, p.description, p.imageResource);
                        btnFavorite.setChecked(FavoriteManager.getInstance().isFavorite(favProduct));
                        btnFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            FavoriteManager.getInstance().toggleFavorite(favProduct);
                        });
                    }

                    productView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(p.name, p.price, p.description, p.imageResource)));

                    productListContainer.addView(productView);
                }
            }
        }
    }

    private void resetCategoryStyles() {
        if (catAll != null) catAll.setTypeface(null, Typeface.NORMAL);
        if (catCrafts != null) catCrafts.setTypeface(null, Typeface.NORMAL);
        if (catClothing != null) catClothing.setTypeface(null, Typeface.NORMAL);
        if (catAccessories != null) catAccessories.setTypeface(null, Typeface.NORMAL);
        if (catHomeDecor != null) catHomeDecor.setTypeface(null, Typeface.NORMAL);
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private static class Product {
        String name, price, description, category;
        int imageResource;
        Product(String name, String price, String description, String category, int imageResource) {
            this.name = name;
            this.price = price;
            this.description = description;
            this.category = category;
            this.imageResource = imageResource;
        }
    }

    private static class AdItem {
        String title, description, category, query;
        int imageRes;
        AdItem(String title, String description, int imageRes, String category, String query) {
            this.title = title;
            this.description = description;
            this.imageRes = imageRes;
            this.category = category;
            this.query = query;
        }
    }

    private class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {
        private List<AdItem> ads;
        private Dialog dialog;

        AdAdapter(List<AdItem> ads, Dialog dialog) {
            this.ads = ads;
            this.dialog = dialog;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_slide, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AdItem ad = ads.get(position);
            holder.img.setImageResource(ad.imageRes);
            holder.title.setText(ad.title);
            holder.desc.setText(ad.description);
            holder.btn.setOnClickListener(v -> {
                dialog.dismiss();
                currentCategory = ad.category;
                searchBar.setText(ad.query);
                displayProducts(ad.category, ad.query);
            });
        }

        @Override
        public int getItemCount() { return ads.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView title, desc;
            Button btn;
            ViewHolder(View v) {
                super(v);
                img = v.findViewById(R.id.ad_image);
                title = v.findViewById(R.id.ad_title);
                desc = v.findViewById(R.id.ad_description);
                btn = v.findViewById(R.id.btn_ad_action);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adHandler != null && adRunnable != null) {
            adHandler.removeCallbacks(adRunnable);
        }
    }
}
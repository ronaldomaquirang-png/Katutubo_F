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
import androidx.recyclerview.widget.GridLayoutManager;
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
import java.util.Objects;

public class HomeFragment extends Fragment {

    private TextView catAll, catCrafts, catClothing, catAccessories, catHomeDecor;
    private ProductAdapter productAdapter;
    private final List<HomeProduct> filteredProducts = new ArrayList<>();
    private LinearLayout discountedContainer;
    private final List<HomeProduct> allProducts = new ArrayList<>();
    private EditText searchBar;
    private String currentCategory = "All";

    private final Handler adHandler = new Handler(Looper.getMainLooper());
    private Runnable adRunnable;
    private ViewPager2 adViewPager;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

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
        RecyclerView productRecyclerView = view.findViewById(R.id.product_list_container);
        discountedContainer = view.findViewById(R.id.discounted_container);
        searchBar = view.findViewById(R.id.SearchBar);

        // Setup RecyclerView
        productAdapter = new ProductAdapter(filteredProducts);
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productRecyclerView.setAdapter(productAdapter);

        view.findViewById(R.id.btn_search_categories).setOnClickListener(v -> switchFragment(new TribeSelectionFragment()));

        initProducts();
        
        // Stagger loading to prevent UI freeze
        view.postDelayed(() -> {
            displayProducts("All", "");
            setupDiscountedProducts();
        }, 100);

        view.postDelayed(() -> setupHotPicks(view), 300);

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
                // Debounce search to prevent lag while typing
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> displayProducts(currentCategory, s.toString());
                searchHandler.postDelayed(searchRunnable, 300);
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
                switchFragmentNav(new CartFragment());
                return true;
            } else if (id == R.id.nav_notifications) {
                switchFragmentNav(new NotificationFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                switchFragmentNav(new ProfileFragment());
                return true;
            }
            return false;
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

        // Auto-slide every 4 seconds
        adRunnable = new Runnable() {
            @Override
            public void run() {
                if (adViewPager != null) {
                    int currentItem = adViewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % ads.size();
                    adViewPager.setCurrentItem(nextItem, true);
                    adHandler.postDelayed(this, 4000);
                }
            }
        };
        adHandler.postDelayed(adRunnable, 4000);

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
        String[] prices = {"₱750.00", "₱600.00", "₱1,400.00", "₱150.00"};
        String[] original = {"₱1,500", "₱1,200", "₱2,800", "₱300"};
        String[] descriptions = {
            "Traditional Bagobo Tagabawa upper garment for women, featuring intricate beadwork and embroidery.",
            "A beautifully hand-embroidered Bagobo Tagabawa blouse, usually worn during special tribal ceremonies.",
            "Traditional handwoven abaca cloth, naturally dyed and patterned with sacred geometric designs.",
            "A sturdy, hand-woven basket used for harvesting and carrying agricultural products."
        };
        String[] artisans = {"Artisan Handcrafted", "Master Weaver", "Traditional Weavers", "Basket Weavers Guild"};
        String[] materials = {"Abaca fiber", "Handwoven cotton", "Hand-dyed Abaca", "Bamboo, Rattan"};
        String[] inspirations = {"Bansalan, Digos City", "Bansalan, Digos City", "Bansalan, Digos City", "Tagakaolo"};
        String[] histories = {
            "The Ompák represents the social status and identity of Bagobo Tagabawa women. Each bead pattern tells a story of their ancestral lineage and connection to nature.",
            "Sonnod has been passed down through generations, originally crafted using only natural dyes from barks and roots found in the foothills of Mt. Apo.",
            "Inabal is considered sacred. The patterns are often revealed to the weavers in dreams, making each piece a unique spiritual expression.",
            "The Bukag is an essential part of the Tagakaolo way of life, evolved from simple storage containers to symbolic items used in harvest rituals."
        };

        for (int k = 0; k < names.length; k++) {
            View itemView = inflater.inflate(R.layout.item_discounted_product, discountedContainer, false);
            ImageView img = itemView.findViewById(R.id.discount_image);
            TextView name = itemView.findViewById(R.id.discount_name);
            TextView price = itemView.findViewById(R.id.discount_price);
            TextView orig = itemView.findViewById(R.id.original_price);
            
            final String finalName = names[k];
            final String finalPrice = prices[k];
            final String finalDesc = descriptions[k];
            final int finalImage = images[k];
            final String finalArtisan = artisans[k];
            final String finalMaterial = materials[k];
            final String finalInspiration = inspirations[k];
            final String finalHistory = histories[k];

            img.setImageResource(images[k]);
            name.setText(names[k]);
            price.setText(prices[k]);
            orig.setText(original[k]);
            orig.setPaintFlags(orig.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(finalName, finalPrice, finalDesc, finalImage, finalArtisan, finalMaterial, finalInspiration, finalHistory)));
            
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
        allProducts.add(new HomeProduct("Ompák", "₱1,500.00",
                "Traditional Bagobo Tagabawa upper garment for women, featuring intricate beadwork and embroidery.",
                "Clothing", R.drawable.ompak, "Artisan Handcrafted", "Abaca fiber, Natural dyes", "Bansalan, Digos City",
                "The Ompák represents the social status and identity of Bagobo Tagabawa women. Each bead pattern tells a story of their ancestral lineage and connection to nature."));

        allProducts.add(new HomeProduct("Sonnod", "₱1,200.00",
                "A beautifully hand-embroidered Bagobo Tagabawa blouse, usually worn during special tribal ceremonies.",
                "Clothing", R.drawable.sonnod, "Master Weaver", "Handwoven cotton, Brass bells", "Bansalan, Digos City",
                "Sonnod has been passed down through generations, originally crafted using only natural dyes from barks and roots found in the foothills of Mt. Apo."));

        allProducts.add(new HomeProduct("Inabal (Cloth)", "₱2,800.00",
                "Traditional handwoven abaca cloth, naturally dyed and patterned with sacred geometric designs.",
                "Clothing", R.drawable.inaball, "Traditional Weavers", "Hand-dyed Abaca", "Bansalan, Digos City",
                "Inabal is considered sacred. The patterns are often revealed to the weavers in dreams, making each piece a unique spiritual expression."));

        allProducts.add(new HomeProduct("Bong an tidas", "₱950.00",
                "A traditional Tagakaolo blouse characterized by its unique stitching and vibrant colors.",
                "Clothing", R.drawable.bongantidas, "Local Community", "Traditional fabric", "Malalag, Sta. Maria",
                "Bong an tidas is a symbol of Tagakaolo identity, reflecting the tribe's resilience and artistic heritage through its meticulous construction."));

        allProducts.add(new HomeProduct("Malong / Patadyong", "₱850.00",
                "A multi-purpose tubular garment commonly used in Mindanao for daily wear or formal events.",
                "Clothing", R.drawable.malong, "Indigenous Artists", "Woven cotton", "Malalag, Sta. Maria",
                "The Malong has various uses, from a skirt to a blanket. Its patterns represent different Mindanaoan tribes, each with distinct cultural meanings."));

        allProducts.add(new HomeProduct("Dagmay cloth", "₱2,500.00",
                "A handwoven textile made from abaca fibers, known for its complex patterns and natural earthy tones.",
                "Clothing", R.drawable.dagmayy, "Elder Weavers", "Manila Hemp (Abaca)", "Malalag, Sta. Maria",
                "Dagmay weaving is a spiritual process for the Mandaya people. The designs are believed to be gifts from the spirits, meant to protect the wearer."));

        allProducts.add(new HomeProduct("Embroidered garments", "₱1,800.00",
                "Exquisite hand-embroidered clothing featuring motifs from nature and tribal folklore.",
                "Clothing", R.drawable.garments, "Coastal Artisans", "Silk thread, Cotton base", "Coastal areas",
                "These garments showcase the fusion of coastal influences and traditional tribal embroidery, reflecting the diverse cultural landscape of the region."));

        allProducts.add(new HomeProduct("Tangkulo (head cloth)", "₱750.00",
                "A traditional head cloth worn by tribal leaders and warriors as a sign of bravery and status.",
                "Clothing", R.drawable.tangkuloo, "Tribe Leaders", "Natural fiber, Embroidery", "Bagobo Tagabawa",
                "Tangkulo signifies leadership. The deep red colors were historically achieved using special bark dyes reserved for those who have achieved warrior status."));

        allProducts.add(new HomeProduct("Ukir (wood carving)", "₱3,500.00",
                "Artistic wood carving featuring the 'Ukir' motif, characterized by leaf and vine patterns.",
                "Crafts", R.drawable.ukir, "Master Carvers", "Molave wood", "Bagobo Tagabawa",
                "Ukir is a traditional art form found across many Mindanao tribes. It symbolizes the interconnectedness of life and the beauty of the natural world."));

        allProducts.add(new HomeProduct("Panday (metal crafting)", "₱4,200.00",
                "Hand-forged metalwork, including traditional blades and brass items used in tribal ceremonies.",
                "Crafts", R.drawable.panday, "Traditional Blacksmiths", "Brass, Iron", "Bagobo Tagabawa",
                "The Panday (blacksmiths) were highly respected in tribal society, as they forged the tools and weapons necessary for survival and protection."));

        allProducts.add(new HomeProduct("Bukag (basket weaving)", "₱650.00",
                "A sturdy, hand-woven basket used for harvesting and carrying agricultural products.",
                "Crafts", R.drawable.bukag, "Basket Weavers Guild", "Bamboo, Rattan", "Tagakaolo",
                "The Bukag is an essential part of the Tagakaolo way of life, evolved from simple storage containers to symbolic items used in harvest rituals."));

        allProducts.add(new HomeProduct("Banig (mat weaving)", "₱1,200.00",
                "A traditional handwoven mat made from dried pandan leaves, used for sleeping or sitting.",
                "Crafts", R.drawable.banig, "Community Weavers", "Dried Pandan leaves", "Tagakaolo",
                "Banig weaving is a communal activity. Each mat is unique, with patterns that tell stories of the family and community who made them."));

        allProducts.add(new HomeProduct("Suwat (comb)", "₱150.00",
                "A decorative bamboo comb adorned with beads, traditionally used by women to style their hair.",
                "Accessories", R.drawable.suwat, "Wood Artisans", "Bamboo, Beads", "Tagakaolo",
                "The Suwat is more than just a grooming tool; it is a symbol of beauty and grace, often given as a gift during courtship."));

        allProducts.add(new HomeProduct("Balyog (necklace)", "₱550.00",
                "A traditional beaded necklace featuring vibrant colors and symbolic protective charms.",
                "Accessories", R.drawable.balyog, "Beadwork Specialists", "Seed beads, Horsehair", "Tagakaolo",
                "Balyog necklaces are worn for protection and to invite good spirits. The specific bead colors represent different aspects of the environment."));

        allProducts.add(new HomeProduct("Sinubla (beaded items)", "₱650.00",
                "Intricately beaded accessories reflecting the rich artistic traditions of the Bagobo Tagabawa.",
                "Accessories", R.drawable.sinubla, "Traditional Bead-makers", "Glass beads, Thread", "Bagobo Tagabawa",
                "Sinubla beadwork is known for its geometric precision. It serves as a visual language, communicating the wearer's tribe and social standing."));

        allProducts.add(new HomeProduct("Ginamay (beaded belt)", "₱1,100.00",
                "A heavy, hand-beaded belt traditionally worn with the Ompák to complete the tribal attire.",
                "Accessories", R.drawable.ginamay, "Cultural Craft-makers", "Beads, Leather", "Bagobo Tagabawa",
                "The Ginamay belt is a labor of love, often taking weeks to complete. It symbolizes the strength and endurance of the artisan."));
    }

    private void displayProducts(String category, String query) {
        resetCategoryStyles();
        if (catAll != null && Objects.equals(category, "All")) catAll.setTypeface(null, Typeface.BOLD);
        else if (catCrafts != null && Objects.equals(category, "Crafts")) catCrafts.setTypeface(null, Typeface.BOLD);
        else if (catClothing != null && Objects.equals(category, "Clothing")) catClothing.setTypeface(null, Typeface.BOLD);
        else if (catAccessories != null && Objects.equals(category, "Accessories")) catAccessories.setTypeface(null, Typeface.BOLD);
        else if (catHomeDecor != null && Objects.equals(category, "Home Decor")) catHomeDecor.setTypeface(null, Typeface.BOLD);

        filteredProducts.clear();
        for (HomeProduct p : allProducts) {
            boolean matchesCategory = Objects.equals(category, "All") || Objects.equals(p.category, category);
            boolean matchesQuery = query.isEmpty() || p.name.toLowerCase().contains(query.toLowerCase()) || p.description.toLowerCase().contains(query.toLowerCase());

            if (matchesCategory && matchesQuery) {
                filteredProducts.add(p);
            }
        }
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        private final List<HomeProduct> products;

        ProductAdapter(List<HomeProduct> products) {
            this.products = products;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HomeProduct p = products.get(position);
            holder.name.setText(p.name);
            holder.price.setText(p.price);
            holder.desc.setText(p.description);
            holder.image.setImageResource(p.imageResource);

            FavoriteProduct favProduct = new FavoriteProduct(p.name, p.price, p.description, p.imageResource);
            holder.btnFavorite.setOnCheckedChangeListener(null);
            holder.btnFavorite.setChecked(FavoriteManager.getInstance(requireContext()).isFavorite(favProduct));
            holder.btnFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> FavoriteManager.getInstance(requireContext()).toggleFavorite(favProduct));

            holder.itemView.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(
                    p.name, p.price, p.description, p.imageResource, p.artisan, p.materials, p.inspiration, p.history)));
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView name, price, desc;
            CheckBox btnFavorite;

            ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.item_image);
                name = itemView.findViewById(R.id.item_name);
                price = itemView.findViewById(R.id.item_price);
                desc = itemView.findViewById(R.id.item_desc);
                btnFavorite = itemView.findViewById(R.id.btn_favorite);
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

    private void switchFragmentNav(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {
        private final List<AdItem> ads;
        private final Dialog dialog;

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
        if (adRunnable != null) {
            adHandler.removeCallbacks(adRunnable);
        }
    }
}

package com.example.katutubo_f;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView catAll, catCrafts, catClothing, catAccessories, catHomeDecor;
    private LinearLayout productListContainer;
    private List<Product> allProducts = new ArrayList<>();

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

        initProducts();
        displayProducts("All"); // Default category is now "All"

        catAll.setOnClickListener(v -> displayProducts("All"));
        catCrafts.setOnClickListener(v -> displayProducts("Crafts"));
        catClothing.setOnClickListener(v -> displayProducts("Clothing"));
        catAccessories.setOnClickListener(v -> displayProducts("Accessories"));
        catHomeDecor.setOnClickListener(v -> displayProducts("Home Decor"));

        TextView profileIcon = view.findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> switchFragment(new ProfileFragment()));

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_cart) {
                switchFragment(new CartFragment());
                return true;
            } else if (id == R.id.nav_notifications) {
                switchFragment(new NotificationFragment());
                return true;
            }
            return false;
        });

        return view;
    }

    private void initProducts() {
        allProducts.clear();

        // --- CLOTHING ---
        allProducts.add(new Product("Ompák", "₱1,500.00", "Bagobo Tagabawa — Bansalan, Digos City, Mt. Apo area (Davao del Sur)", "Clothing", R.drawable.ompak));
        allProducts.add(new Product("Sonnod", "₱1,200.00", "Bagobo Tagabawa — Bansalan, Digos City, Mt. Apo area (Davao del Sur)", "Clothing", R.drawable.sonnod));
        allProducts.add(new Product("Inabal (Cloth)", "₱2,800.00", "Bagobo Tagabawa — Bansalan, Digos City, Mt. Apo area (Davao del Sur)", "Clothing", R.drawable.inaball));
        allProducts.add(new Product("Bong an tidas", "₱950.00", "Tagakaolo — Malalag, Sta. Maria, Padada (Davao del Sur)", "Clothing", R.drawable.bongantidas));
        allProducts.add(new Product("Malong / Patadyong", "₱850.00", "Tagakaolo — Malalag, Sta. Maria, Padada (Davao del Sur)", "Clothing", R.drawable.malong));
        allProducts.add(new Product("Dagmay cloth", "₱2,500.00", "Tagakaolo — Malalag, Sta. Maria, Padada (Davao del Sur)", "Clothing", R.drawable.dagmayy));
        allProducts.add(new Product("Embroidered garments", "₱1,800.00", "Mandaya — Coastal and upland communities of Davao del Sur", "Clothing", R.drawable.garments));
        allProducts.add(new Product("Tangkulo (head cloth)", "₱750.00", "Bagobo Tagabawa — Bansalan, Digos City, Mt. Apo area", "Clothing", R.drawable.tangkuloo));

        // --- CRAFTS ---
        allProducts.add(new Product("Ukir (wood carving)", "₱3,500.00", "Bagobo Tagabawa — Bansalan, Digos City, Mt. Apo area", "Crafts", R.drawable.ukir));
        allProducts.add(new Product("Panday (metal/brass crafting)", "₱4,200.00", "Bagobo Tagabawa — Bansalan, Digos City, Mt. Apo area", "Crafts", R.drawable.panday));
        allProducts.add(new Product("Inabal (abaca weaving)", "₱3,000.00", "Bagobo Tagabawa — Bansalan, Digos City, Mt. Apo area", "Crafts", R.drawable.inabal));
        allProducts.add(new Product("Dagmay (woven craft)", "₱2,200.00", "Tagakaolo — Malalag, Sta. Maria, Padada", "Crafts", R.drawable.dagmay));
        allProducts.add(new Product("Bukag (basket weaving)", "₱650.00", "Tagakaolo — Malalag, Sta. Maria, Padada", "Crafts", R.drawable.bukag));
        allProducts.add(new Product("Banig (mat weaving)", "₱1,200.00", "Tagakaolo — Malalag, Sta. Maria, Padada", "Crafts", R.drawable.banig));
        allProducts.add(new Product("Mandaya Ukir", "₱3,800.00", "Mandaya — boundary areas near Davao Oriental", "Crafts", R.drawable.mandaya));
        allProducts.add(new Product("Panulâ (embroidery craft)", "₱1,400.00", "Mandaya — boundary areas near Davao Oriental", "Crafts", R.drawable.panula));

        // --- ACCESSORIES ---
        allProducts.add(new Product("Suwat (comb)", "₱350.00", "Tagakaolo — Malalag, Sta. Maria, Padada", "Accessories", R.drawable.suwat));
        allProducts.add(new Product("Balyog (necklace)", "₱550.00", "Tagakaolo — Malalag, Sta. Maria, Padada", "Accessories", R.drawable.balyog));
        allProducts.add(new Product("Lampad (ear/neck ornament)", "₱450.00", "Tagakaolo — Malalag, Sta. Maria, Padada", "Accessories", R.drawable.lampad));
        allProducts.add(new Product("Sinubla (beaded accessories)", "₱650.00", "Bagobo Tagabawa — Bansalan, Digos City", "Accessories", R.drawable.sinubla));
        allProducts.add(new Product("Tangkulo (head cloth)", "₱250.00", "Bagobo Tagabawa — Bansalan, Digos City", "Accessories", R.drawable.tangkulo));
        allProducts.add(new Product("Ginamay (beaded belt)", "₱1,100.00", "Bagobo Tagabawa — Bansalan, Digos City", "Accessories", R.drawable.ginamay));
        allProducts.add(new Product("Baliog (earrings)", "₱400.00", "Mandaya — boundary areas near Davao Oriental", "Accessories", R.drawable.baliog));
        allProducts.add(new Product("Singsing (rings)", "₱250.00", "Mandaya — boundary areas near Davao Oriental", "Accessories", R.drawable.singsing));
        allProducts.add(new Product("Kwintas (necklace)", "₱750.00", "Mandaya — boundary areas near Davao Oriental", "Accessories", R.drawable.kwintas));

        // --- HOME DECOR ---
        allProducts.add(new Product("Decorative Bukag", "₱650.00", "Traditional basket weaving — Tagakaolo", "Home Decor", R.drawable.handwoven));
        allProducts.add(new Product("Artisan Banig", "₱1,200.00", "Mat weaving — Tagakaolo", "Home Decor", R.drawable.logoooo));
        allProducts.add(new Product("Inabal Wall Hanging", "₱3,000.00", "Abaca weaving — Bagobo Tagabawa", "Home Decor", R.drawable.logoooo));
        allProducts.add(new Product("Wood Carved Panel", "₱3,500.00", "Ukir (wood carving) — Bagobo Tagabawa", "Home Decor", R.drawable.logoooo));
    }

    private void displayProducts(String category) {
        resetCategoryStyles();
        if (category.equals("All")) catAll.setTypeface(null, Typeface.BOLD);
        else if (category.equals("Crafts")) catCrafts.setTypeface(null, Typeface.BOLD);
        else if (category.equals("Clothing")) catClothing.setTypeface(null, Typeface.BOLD);
        else if (category.equals("Accessories")) catAccessories.setTypeface(null, Typeface.BOLD);
        else if (category.equals("Home Decor")) catHomeDecor.setTypeface(null, Typeface.BOLD);

        productListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (Product p : allProducts) {
            if (category.equals("All") || p.category.equals(category)) {
                View productView = inflater.inflate(R.layout.item_product_card, productListContainer, false);
                
                ImageView image = productView.findViewById(R.id.item_image);
                TextView name = productView.findViewById(R.id.item_name);
                TextView price = productView.findViewById(R.id.item_price);
                TextView desc = productView.findViewById(R.id.item_desc);
                Button btn = productView.findViewById(R.id.item_button);

                image.setImageResource(p.imageResource);
                name.setText(p.name);
                price.setText(p.price);
                desc.setText(p.description);
                
                btn.setOnClickListener(v -> switchFragment(ProductDetailFragment.newInstance(p.name, p.price, p.description, p.imageResource)));

                productListContainer.addView(productView);
            }
        }
    }

    private void resetCategoryStyles() {
        catAll.setTypeface(null, Typeface.NORMAL);
        catCrafts.setTypeface(null, Typeface.NORMAL);
        catClothing.setTypeface(null, Typeface.NORMAL);
        catAccessories.setTypeface(null, Typeface.NORMAL);
        catHomeDecor.setTypeface(null, Typeface.NORMAL);
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
}

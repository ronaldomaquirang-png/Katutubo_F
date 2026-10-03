package com.example.katutubo_f;

import androidx.appcompat.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SimpleDetailFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";
    private String title;
    private String content;

    public static SimpleDetailFragment newInstance(String title, String content) {
        SimpleDetailFragment fragment = new SimpleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            content = getArguments().getString(ARG_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_detail, container, false);
        
        TextView titleTextView = view.findViewById(R.id.detail_title);
        TextView contentTextView = view.findViewById(R.id.detail_content);
        LinearLayout dynamicContainer = view.findViewById(R.id.dynamic_container);
        
        titleTextView.setText(title);
        contentTextView.setText(content);
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        if ("Recently Viewed".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayRecentProducts(dynamicContainer);
        } 
        else if ("My Favorites".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayFavoriteProducts(dynamicContainer);
        }
        else if ("To Pay".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, "To Pay");
        }
        else if ("To Ship".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, "To Ship");
        }
        else if ("To Receive".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, "To Receive");
        }
        else if ("Cancelled".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, "Cancelled");
        }
        else if ("Flash Sale".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayFlashSale(dynamicContainer);
        }
        else if ("Purchase History".equals(title)) {
            contentTextView.setVisibility(View.GONE);
            displayOrdersByStatus(dynamicContainer, ""); // Empty string means all orders
        }
        
        return view;
    }

    private void displayOrdersByStatus(LinearLayout container, String filterStatus) {
        container.removeAllViews();
        List<Order> orders = OrderManager.getInstance(requireContext()).getOrders();
        boolean found = false;
        
        for (Order order : orders) {
            if (filterStatus.isEmpty() || order.status.toLowerCase().contains(filterStatus.toLowerCase())) {
                found = true;
                View itemView = getLayoutInflater().inflate(R.layout.item_order, container, false);
                
                TextView tvId = itemView.findViewById(R.id.order_id);
                TextView tvStatus = itemView.findViewById(R.id.order_status);
                TextView tvTotal = itemView.findViewById(R.id.order_total);
                TextView tvDate = itemView.findViewById(R.id.order_date);
                Button btnCancel = itemView.findViewById(R.id.btn_cancel_order);
                Button btnBuyAgain = itemView.findViewById(R.id.btn_buy_again_item);

                tvId.setText("Order #" + order.orderId);
                tvStatus.setText(order.status);
                tvTotal.setText("Total: " + order.total);
                tvDate.setText("Date: " + order.timestamp);

                if (order.status.equalsIgnoreCase("To Pay") || order.status.equalsIgnoreCase("To Ship")) {
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setOnClickListener(v -> showCancelReasonDialog(order.orderId, container, filterStatus));
                } else if (order.status.equalsIgnoreCase("Cancelled") || order.status.equalsIgnoreCase("Completed")) {
                    btnBuyAgain.setVisibility(View.VISIBLE);
                    btnBuyAgain.setOnClickListener(v -> handleBuyAgain(order));
                }
                
                itemView.setOnClickListener(v -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, OrderDetailFragment.newInstance(order.orderId))
                            .addToBackStack(null)
                            .commit();
                });

                container.addView(itemView);
            }
        }
        
        if (!found) {
            showEmptyMessage(container, "No orders found.");
        }
    }

    private void displayFlashSale(LinearLayout container) {
        container.removeAllViews();
        
        String[] names = {"Ompák", "Sonnod", "Inabal", "Bukag", "Baliog", "Kwintas", "Singsing", "Tangkulo", "Banig", "Panday"};
        int[] images = {R.drawable.ompak, R.drawable.sonnod, R.drawable.inaball, R.drawable.bukag, R.drawable.baliog, R.drawable.kwintas, R.drawable.singsing, R.drawable.tangkulo, R.drawable.banig, R.drawable.panday};
        String[] prices = {"₱750.00", "₱600.00", "₱1,400.00", "₱150.00", "₱350.00", "₱250.00", "₱150.00", "₱450.00", "₱1,200.00", "₱800.00"};
        String[] original = {"₱1,500", "₱1,200", "₱2,800", "₱300", "₱700", "₱500", "₱300", "₱900", "₱2,400", "₱1,600"};
        String[] descriptions = {
            "Traditional Bagobo Tagabawa upper garment for women, featuring intricate beadwork and embroidery.",
            "A beautifully hand-embroidered Bagobo Tagabawa blouse, usually worn during special tribal ceremonies.",
            "Traditional handwoven abaca cloth, naturally dyed and patterned with sacred geometric designs.",
            "A sturdy, hand-woven basket used for harvesting and carrying agricultural products.",
            "Traditional beaded necklace with intricate patterns and vibrant colors, symbolizing tribal identity.",
            "Handcrafted indigenous necklace made from natural beads and materials.",
            "Artisan-crafted tribal ring featuring traditional metalwork or beadwork designs.",
            "A sacred head cloth or scarf, often worn as a symbol of leadership or special status.",
            "Handwoven mat made from dried sea-grass or palm leaves, featuring complex geometric patterns.",
            "Hand-forged traditional blade or metalcraft, showcasing ancestral smithing techniques."
        };
        String[] artisans = {"Artisan Handcrafted", "Master Weaver", "Traditional Weavers", "Basket Weavers Guild", "Beadwork Specialists", "Local Artisans", "Metalwork Guild", "Tribal Elders", "Mat Weavers", "Master Smiths"};
        String[] materials = {"Abaca fiber", "Handwoven cotton", "Hand-dyed Abaca", "Bamboo, Rattan", "Glass beads", "Natural beads", "Brass, Silver", "Hand-dyed fabric", "Dried leaves", "Forged metal"};
        String[] inspirations = {"Bansalan, Digos City", "Bansalan, Digos City", "Bansalan, Digos City", "Tagakaolo", "Mindanao Tribes", "Indigenous Crafts", "Ancestral Metalwork", "Tribal Heritage", "Traditional Patterns", "Smithing Traditions"};
        String[] histories = {
            "The Ompák represents the social status and identity of Bagobo Tagabawa women. Each bead pattern tells a story of their ancestral lineage and connection to nature.",
            "Sonnod has been passed down through generations, originally crafted using only natural dyes from barks and roots found in the foothills of Mt. Apo.",
            "Inabal is considered sacred. The patterns are often revealed to the weavers in dreams, making each piece a unique spiritual expression.",
            "The Bukag is an essential part of the Tagakaolo way of life, evolved from simple storage containers to symbolic items used in harvest rituals.",
            "The Baliog is more than just jewelry; it's a wearable piece of history representing the wearer's tribe and social standing.",
            "Indigenous necklaces like the Kwintas have been used for centuries as both adornment and currency in trade.",
            "Tribal rings or Singsing often carry protective meanings or signify marital status within the community.",
            "The Tangkulo is a testament to the weaver's skill and the wearer's importance in tribal society.",
            "Banig weaving is a communal activity that brings generations together, preserving the art of intricate geometric storytelling.",
            "The Panday (smith) holds a respected place in the community, crafting tools and weapons that are both functional and artistic."
        };

        for (int i = 0; i < names.length; i++) {
            View itemView = getLayoutInflater().inflate(R.layout.item_discounted_product, container, false);
            ImageView img = itemView.findViewById(R.id.discount_image);
            TextView name = itemView.findViewById(R.id.discount_name);
            TextView price = itemView.findViewById(R.id.discount_price);
            TextView orig = itemView.findViewById(R.id.original_price);
            TextView tag = itemView.findViewById(R.id.discount_tag);

            final String finalName = names[i];
            final String finalPrice = prices[i];
            final String finalDesc = descriptions[i];
            final int finalImage = images[i];
            final String finalArtisan = artisans[i];
            final String finalMaterial = materials[i];
            final String finalInspiration = inspirations[i];
            final String finalHistory = histories[i];

            img.setImageResource(images[i]);
            name.setText(names[i]);
            price.setText(prices[i]);
            orig.setText(original[i]);
            orig.setPaintFlags(orig.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            tag.setText("-50%");

            itemView.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ProductDetailFragment.newInstance(finalName, finalPrice, finalDesc, finalImage, finalArtisan, finalMaterial, finalInspiration, finalHistory))
                    .addToBackStack(null)
                    .commit();
            });
            container.addView(itemView);
        }
    }

    private void handleBuyAgain(Order order) {
        if (order.items != null && !order.items.isEmpty()) {
            for (OrderItem item : order.items) {
                CartItem cartItem = new CartItem(
                        item.name,
                        item.price,
                        item.imageResId,
                        item.quantity
                );
                CartManager.getInstance(requireContext()).addToCart(cartItem);
            }
            Toast.makeText(getContext(), "Added " + order.items.size() + " items to cart", Toast.LENGTH_SHORT).show();
            
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CartFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(), "No items found in this order", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCancelReasonDialog(String orderId, LinearLayout container, String filterStatus) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cancel_reason, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        RadioGroup rgReasons = dialogView.findViewById(R.id.rg_cancel_reasons);
        EditText etOtherReason = dialogView.findViewById(R.id.et_other_reason);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit_cancel);
        TextView btnClose = dialogView.findViewById(R.id.btn_close_dialog);

        rgReasons.setOnCheckedChangeListener((group, checkedId) -> {
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.katutubo_orange)));
            btnSubmit.setTextColor(getResources().getColor(R.color.white));

            if (checkedId == R.id.reason_other) {
                etOtherReason.setVisibility(View.VISIBLE);
            } else {
                etOtherReason.setVisibility(View.GONE);
            }
        });

        btnSubmit.setOnClickListener(v -> {
            int selectedId = rgReasons.getCheckedRadioButtonId();
            RadioButton rb = dialogView.findViewById(selectedId);
            String reason;

            if (selectedId == R.id.reason_other) {
                reason = etOtherReason.getText().toString().trim();
                if (reason.isEmpty()) {
                    Toast.makeText(getContext(), "Please type your reason", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                reason = rb != null ? rb.getText().toString() : "No reason provided";
            }

            // Show confirmation dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Order")
                    .setMessage("Are you sure you want to cancel this order?")
                    .setPositiveButton("YES, CANCEL", (confirmDialog, which) -> {
                        String currentTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
                        OrderManager.getInstance(requireContext()).cancelOrder(orderId, reason, currentTime);

                        // Send Push Notification for Cancellation
                        NotificationHelper.sendOrderNotification(getContext(), "Order Cancelled ❌",
                                "Order #" + orderId + " has been successfully cancelled.");

                        displayOrdersByStatus(container, filterStatus);
                        dialog.dismiss();
                    })
                    .setNegativeButton("NO", null)
                    .show();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void displayRecentProducts(LinearLayout container) {
        List<RecentProduct> recentProducts = RecentViewManager.getInstance(requireContext()).getRecentProducts();
        if (recentProducts.isEmpty()) {
            showEmptyMessage(container, "No recently viewed products.");
            return;
        }
        for (RecentProduct product : recentProducts) {
            addProductItem(container, product.title, product.price, product.description, product.imageResource);
        }
    }

    private void displayFavoriteProducts(LinearLayout container) {
        List<FavoriteProduct> favoriteProducts = FavoriteManager.getInstance(requireContext()).getFavoriteProducts();
        if (favoriteProducts.isEmpty()) {
            showEmptyMessage(container, "Your favorites list is empty.");
            return;
        }
        for (FavoriteProduct product : favoriteProducts) {
            addProductItem(container, product.title, product.price, product.description, product.imageResource);
        }
    }

    private void addProductItem(LinearLayout container, String title, String price, String desc, int imgRes) {
        View itemView = getLayoutInflater().inflate(R.layout.item_recent_product, container, false);
        ImageView img = itemView.findViewById(R.id.recent_img);
        TextView tvTitle = itemView.findViewById(R.id.recent_title);
        TextView tvPrice = itemView.findViewById(R.id.recent_price);
        
        img.setImageResource(imgRes);
        tvTitle.setText(title);
        tvPrice.setText(price);
        
        itemView.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ProductDetailFragment.newInstance(title, price, desc, imgRes))
                .addToBackStack(null)
                .commit();
        });
        container.addView(itemView);
    }

    private void showEmptyMessage(LinearLayout container, String message) {
        TextView emptyMsg = new TextView(getContext());
        emptyMsg.setText(message);
        emptyMsg.setPadding(0, 100, 0, 0);
        emptyMsg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyMsg.setTextColor(getResources().getColor(R.color.katutubo_brown));
        container.addView(emptyMsg);
    }
}
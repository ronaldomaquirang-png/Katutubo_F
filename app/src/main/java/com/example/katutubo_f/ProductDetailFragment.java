package com.example.katutubo_f;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDetailFragment extends Fragment {

    private String title, price, description;
    private int imageResource;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(String title, String price, String description, int imageResource) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("price", price);
        args.putString("description", description);
        args.putInt("imageResource", imageResource);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            price = getArguments().getString("price");
            description = getArguments().getString("description");
            imageResource = getArguments().getInt("imageResource");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        TextView titleTxt = view.findViewById(R.id.productTitle);
        TextView priceTxt = view.findViewById(R.id.productPrice);
        TextView descTxt = view.findViewById(R.id.productDescription);
        ImageView productImg = view.findViewById(R.id.productImage);
        ImageButton backBtn = view.findViewById(R.id.backBtn);

        titleTxt.setText(title);
        priceTxt.setText(price);
        descTxt.setText(description);
        if (imageResource != 0) {
            productImg.setImageResource(imageResource);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
package com.example.katutubo_f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TribeDetailFragment extends Fragment {

    private static final String ARG_NAME = "tribe_name";
    private static final String ARG_REGION = "tribe_region";
    private static final String ARG_DESC = "tribe_desc";

    private String tribeName, tribeRegion, tribeDesc;

    public static TribeDetailFragment newInstance(String name, String region, String desc) {
        TribeDetailFragment fragment = new TribeDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_REGION, region);
        args.putString(ARG_DESC, desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tribeName = getArguments().getString(ARG_NAME);
            tribeRegion = getArguments().getString(ARG_REGION);
            tribeDesc = getArguments().getString(ARG_DESC);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tribe_detail, container, false);

        TextView nameTxt = view.findViewById(R.id.detail_tribe_name);
        TextView regionTxt = view.findViewById(R.id.detail_tribe_region);
        TextView descTxt = view.findViewById(R.id.detail_tribe_desc);

        nameTxt.setText(tribeName);
        regionTxt.setText(tribeRegion);
        descTxt.setText(tribeDesc);

        view.findViewById(R.id.btn_back_detail).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }
}
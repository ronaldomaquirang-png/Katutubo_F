package com.example.katutubo_f;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NewAddressFragment extends Fragment {

    private EditText etFullName, etPhoneNumber, etPostalCode, etStreetAddress, etRegion;
    private TextView btnLabelWork, btnLabelHome;
    private Button btnSubmit;
    private String selectedLabel = "";

    public NewAddressFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_address, container, false);

        etFullName = view.findViewById(R.id.et_full_name);
        etPhoneNumber = view.findViewById(R.id.et_phone_number);
        etPostalCode = view.findViewById(R.id.et_postal_code);
        etStreetAddress = view.findViewById(R.id.et_street_address);
        etRegion = view.findViewById(R.id.et_region);
        btnLabelWork = view.findViewById(R.id.btn_label_work);
        btnLabelHome = view.findViewById(R.id.btn_label_home);
        btnSubmit = view.findViewById(R.id.btn_submit);
        ImageButton btnBack = view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnLabelWork.setOnClickListener(v -> selectLabel("Work"));
        btnLabelHome.setOnClickListener(v -> selectLabel("Home"));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etFullName.addTextChangedListener(textWatcher);
        etPhoneNumber.addTextChangedListener(textWatcher);
        etPostalCode.addTextChangedListener(textWatcher);
        etStreetAddress.addTextChangedListener(textWatcher);
        etRegion.addTextChangedListener(textWatcher);

        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Address saved successfully!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void selectLabel(String label) {
        selectedLabel = label;
        if (label.equals("Work")) {
            btnLabelWork.setBackgroundResource(R.drawable.label_selected_bg);
            btnLabelWork.setTextColor(Color.WHITE);
            btnLabelHome.setBackgroundResource(R.drawable.label_unselected_bg);
            btnLabelHome.setTextColor(Color.parseColor("#888888"));
        } else {
            btnLabelHome.setBackgroundResource(R.drawable.label_selected_bg);
            btnLabelHome.setTextColor(Color.WHITE);
            btnLabelWork.setBackgroundResource(R.drawable.label_unselected_bg);
            btnLabelWork.setTextColor(Color.parseColor("#888888"));
        }
        validateForm();
    }

    private void validateForm() {
        boolean isValid = !etFullName.getText().toString().trim().isEmpty() &&
                !etPhoneNumber.getText().toString().trim().isEmpty() &&
                !etPostalCode.getText().toString().trim().isEmpty() &&
                !etStreetAddress.getText().toString().trim().isEmpty() &&
                !etRegion.getText().toString().trim().isEmpty() &&
                !selectedLabel.isEmpty();

        if (isValid) {
            btnSubmit.setEnabled(true);
            btnSubmit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.katutubo_orange)));
            btnSubmit.setTextColor(Color.WHITE);
        } else {
            btnSubmit.setEnabled(false);
            btnSubmit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            btnSubmit.setTextColor(Color.parseColor("#AAAAAA"));
        }
    }
}
package com.example.katutubo_f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class TribeSelectionFragment extends Fragment {

    private GridLayout tribeGrid;

    public TribeSelectionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tribe_selection, container, false);

        tribeGrid = view.findViewById(R.id.tribe_grid);
        view.findViewById(R.id.btn_back_selection).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        setupTribes();

        return view;
    }

    private void setupTribes() {
        List<Tribe> tribes = new ArrayList<>();
        tribes.add(new Tribe("Igorot", "Cordillera", "The Igorots are the people from the Cordillera mountains. They are known for their rice terraces and vibrant weaving."));
        tribes.add(new Tribe("Lumad", "Mindanao", "Lumad is a group of non-Islamized indigenous peoples in Mindanao, known for their elaborate musical instruments and colorful traditional attire."));
        tribes.add(new Tribe("Mangyan", "Mindoro", "Mangyan is the generic name for the eight indigenous groups found on the island of Mindoro."));
        tribes.add(new Tribe("Aeta", "Central Luzon", "The Aetas are among the earliest inhabitants of the Philippines, known for their deep connection to the forest."));
        tribes.add(new Tribe("Badjao", "Sulu Archipelago", "Known as Sea Gypsies, the Badjaos are indigenous ethnic groups of the Southern Philippines."));
        tribes.add(new Tribe("T'boli", "South Cotabato", "The T'boli people are famous for their Dream Weavers and the T'nalak fabric."));
        tribes.add(new Tribe("Ifugao", "Cordillera", "Builders of the famous Rice Terraces, the Ifugaos have a rich oral tradition and woodcarving skills."));
        tribes.add(new Tribe("Mandaya", "Davao", "The Mandayas are known for their 'Dagmay' fabric and intricate beadwork."));

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Tribe tribe : tribes) {
            View tribeView = inflater.inflate(R.layout.item_tribe_card, tribeGrid, false);
            
            // Set grid params for 2 columns
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            tribeView.setLayoutParams(params);

            TextView nameTxt = tribeView.findViewById(R.id.tribe_name);
            TextView regionTxt = tribeView.findViewById(R.id.tribe_region);
            
            nameTxt.setText(tribe.name);
            regionTxt.setText(tribe.region);

            tribeView.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, TribeDetailFragment.newInstance(tribe.name, tribe.region, tribe.description))
                        .addToBackStack(null)
                        .commit();
            });

            tribeGrid.addView(tribeView);
        }
    }

    public static class Tribe {
        String name, region, description;
        Tribe(String name, String region, String description) {
            this.name = name;
            this.region = region;
            this.description = description;
        }
    }
}
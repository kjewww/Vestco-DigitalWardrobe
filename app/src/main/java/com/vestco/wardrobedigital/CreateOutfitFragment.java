package com.vestco.wardrobedigital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CreateOutfitFragment extends Fragment {

    private ImageView ivOuter, ivInner, ivBawahan, ivSepatu, ivAksesoris;
    private TextView tvOuter, tvInner, tvBawahan, tvSepatu, tvAksesoris;
    private Button btnSaveOutfit;

    private ClothingItem selectedOuter, selectedInner, selectedBawahan, selectedSepatu, selectedAksesoris;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_outfit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        ivOuter = view.findViewById(R.id.iv_outer);
        ivInner = view.findViewById(R.id.iv_inner);
        ivBawahan = view.findViewById(R.id.iv_bawahan);
        ivSepatu = view.findViewById(R.id.iv_sepatu);
        ivAksesoris = view.findViewById(R.id.iv_aksesoris);

        tvOuter = view.findViewById(R.id.tv_outer);
        tvInner = view.findViewById(R.id.tv_inner);
        tvBawahan = view.findViewById(R.id.tv_bawahan);
        tvSepatu = view.findViewById(R.id.tv_sepatu);
        tvAksesoris = view.findViewById(R.id.tv_aksesoris);

        Button btnSelectOuter = view.findViewById(R.id.btn_select_outer);
        Button btnSelectInner = view.findViewById(R.id.btn_select_inner);
        Button btnSelectBawahan = view.findViewById(R.id.btn_select_bawahan);
        Button btnSelectSepatu = view.findViewById(R.id.btn_select_sepatu);
        Button btnSelectAksesoris = view.findViewById(R.id.btn_select_aksesoris);
        btnSaveOutfit = view.findViewById(R.id.btn_save_outfit);

        // Set click listeners
        btnSelectOuter.setOnClickListener(v -> showItemSelector("Atasan - Outer"));
        btnSelectInner.setOnClickListener(v -> showItemSelector("Atasan - Inner"));
        btnSelectBawahan.setOnClickListener(v -> showItemSelector("Bawahan"));
        btnSelectSepatu.setOnClickListener(v -> showItemSelector("Sepatu"));
        btnSelectAksesoris.setOnClickListener(v -> showItemSelector("Aksesoris"));

        btnSaveOutfit.setOnClickListener(v -> saveOutfit());

        updateSaveButtonState();
    }

    private void showItemSelector(String category) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        List<ClothingItemEntity> allItems = db.clothingDao().getAll();

        List<ClothingItem> filteredItems = new ArrayList<>();
        for (ClothingItemEntity entity : allItems) {
            if (entity.getCategory().equalsIgnoreCase(category)) {
                ClothingItem item = new ClothingItem(
                        entity.getId(),
                        android.net.Uri.parse(entity.getImageUri()),
                        entity.getCategory(),
                        java.util.Arrays.asList(entity.getWeather().split(",")),
                        entity.getPattern(),
                        java.util.Arrays.asList(entity.getColors().split(",")),
                        entity.getNotes(),
                        entity.getName()
                );
                filteredItems.add(item);
            }
        }

        if (filteredItems.isEmpty()) {
            Toast.makeText(getContext(), "Tidak ada " + category + " yang ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show dialog with grid
        showItemDialog(category, filteredItems);
    }

    private void showItemDialog(String category, List<ClothingItem> items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_item, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Pilih " + category)
                .setNegativeButton("Batal", null)
                .create();

        ItemSelectorAdapter adapter = new ItemSelectorAdapter(items, item -> {
            // Handle item selection
            switch (category) {
                case "Atasan - Outer":
                    selectedOuter = item;
                    updateImageView(ivOuter, tvOuter, item);
                    break;
                case "Atasan - Inner":
                    selectedInner = item;
                    updateImageView(ivInner, tvInner, item);
                    break;
                case "Bawahan":
                    selectedBawahan = item;
                    updateImageView(ivBawahan, tvBawahan, item);
                    break;
                case "Sepatu":
                    selectedSepatu = item;
                    updateImageView(ivSepatu, tvSepatu, item);
                    break;
                case "Aksesoris":
                    selectedAksesoris = item;
                    updateImageView(ivAksesoris, tvAksesoris, item);
                    break;
            }
            updateSaveButtonState();
            dialog.dismiss(); // ✅ Dismiss dialog setelah item dipilih
        });

        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    private void updateImageView(ImageView imageView, TextView textView, ClothingItem item) {
        Glide.with(this).load(item.getImageUri()).into(imageView);
        textView.setText(item.getName());
        imageView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
    }

    private void updateSaveButtonState() {
        // Minimal: (Outer ATAU Inner) + Bawahan + Sepatu
        boolean hasAtasan = selectedOuter != null || selectedInner != null;
        boolean canSave = hasAtasan && selectedBawahan != null && selectedSepatu != null;

        btnSaveOutfit.setEnabled(canSave);
        btnSaveOutfit.setAlpha(canSave ? 1.0f : 0.5f);
    }

    private void saveOutfit() {
        // Show dialog untuk nama outfit (opsional)
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_name_outfit, null);
        TextInputEditText etOutfitName = dialogView.findViewById(R.id.et_outfit_name);

        builder.setView(dialogView)
                .setTitle("Simpan Outfit")
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String outfitName = etOutfitName.getText() != null ?
                            etOutfitName.getText().toString().trim() : "";
                    if (outfitName.isEmpty()) {
                        outfitName = "Outfit " + System.currentTimeMillis();
                    }

                    // Save to database
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    OutfitEntity outfit = new OutfitEntity(
                            outfitName,
                            selectedOuter != null ? selectedOuter.getId() : null,
                            selectedInner != null ? selectedInner.getId() : null,
                            selectedBawahan.getId(),
                            selectedSepatu.getId(),
                            selectedAksesoris != null ? selectedAksesoris.getId() : null,
                            System.currentTimeMillis()
                    );

                    db.outfitDao().insertOutfit(outfit);

                    Toast.makeText(getContext(), "Outfit disimpan!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
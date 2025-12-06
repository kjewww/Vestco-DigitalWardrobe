package com.example.wardrobedigital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {

    private ClothingItem currentItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            currentItem = DetailFragmentArgs.fromBundle(getArguments()).getClothingItem();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("Detail Pakaian");
            }
        }

        ImageView detailImage = view.findViewById(R.id.detail_image);
        TextView detailCategory = view.findViewById(R.id.detail_category);
        TextView detailDescription = view.findViewById(R.id.detail_description);
        TextView detailName = view.findViewById(R.id.detail_nama);
        detailName.setText(currentItem.getName());

        if (currentItem != null) {
            Glide.with(this).load(currentItem.getImageUri()).into(detailImage);

            detailImage.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("imageUri", currentItem.getImageUri().toString());

                Navigation.findNavController(v)
                        .navigate(R.id.action_detailFragment_to_imageFullscreenFragment, bundle);
            });

            detailCategory.setText(currentItem.getCategory());

            StringBuilder descriptionBuilder = new StringBuilder();
            descriptionBuilder.append("Cuaca: ").append(String.join(", ", currentItem.getWeather())).append("\n");
            descriptionBuilder.append("Motif: ").append(currentItem.getPattern()).append("\n");
            if (!currentItem.getColors().isEmpty()) {
                descriptionBuilder.append("Warna: ").append(String.join(", ", currentItem.getColors())).append("\n\n");
            }
            if (currentItem.getNotes() != null && !currentItem.getNotes().isEmpty()) {
                descriptionBuilder.append("Catatan: ").append(currentItem.getNotes());
            }
            detailDescription.setText(descriptionBuilder.toString());
        }
    }

    private void updateDetailUI() {
        View view = getView();
        if (view == null || currentItem == null) return;

        ImageView detailImage = view.findViewById(R.id.detail_image);
        TextView detailCategory = view.findViewById(R.id.detail_category);
        TextView detailDescription = view.findViewById(R.id.detail_description);
        TextView detailName = view.findViewById(R.id.detail_nama);

        Glide.with(this).load(currentItem.getImageUri()).into(detailImage);
        detailCategory.setText(currentItem.getCategory());
        detailName.setText(currentItem.getName());

        StringBuilder desc = new StringBuilder();
        desc.append("Cuaca: ").append(String.join(", ", currentItem.getWeather())).append("\n");
        desc.append("Motif: ").append(currentItem.getPattern()).append("\n");
        if (!currentItem.getColors().isEmpty())
            desc.append("Warna: ").append(String.join(", ", currentItem.getColors())).append("\n\n");
        if (currentItem.getNotes() != null && !currentItem.getNotes().isEmpty())
            desc.append("Catatan: ").append(currentItem.getNotes());

        detailDescription.setText(desc.toString());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showDeleteConfirmationDialog(NavController navController) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Item")
                .setMessage("Apakah Anda yakin ingin menghapus item ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    if (currentItem != null) {
                        AppDatabase db = AppDatabase.getInstance(requireContext());
                        ClothingItemEntity entity = new ClothingItemEntity(
                                currentItem.getImageUri().toString(),
                                currentItem.getCategory(),
                                String.join(",", currentItem.getWeather()),
                                currentItem.getPattern(),
                                String.join(",", currentItem.getColors()),
                                currentItem.getNotes(),
                                currentItem.getName()
                        );
                        entity.setId(currentItem.getId());
                        db.clothingDao().delete(entity);

                        Toast.makeText(getContext(), "Item dihapus", Toast.LENGTH_SHORT).show();
                        navController.navigateUp();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showEditItemDialog() {
        if (currentItem == null) return;

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item_details, null);

        // Ambil semua view dari layout
        RadioGroup rgCategory = dialogView.findViewById(R.id.rg_category);
        ChipGroup cgWeather = dialogView.findViewById(R.id.cg_weather);
        RadioGroup rgPattern = dialogView.findViewById(R.id.rg_pattern);
        ChipGroup cgColors = dialogView.findViewById(R.id.cg_colors);
        TextInputEditText etNotes = dialogView.findViewById(R.id.et_notes);
        TextInputEditText etName = dialogView.findViewById(R.id.et_name);
        RadioGroup rgSubCategory = dialogView.findViewById(R.id.rg_subcategory);

        // === Prefill data dari currentItem ===
        // Nama
        etName.setText(currentItem.getName());

        // Catatan
        etNotes.setText(currentItem.getNotes());

        // Kategori - Handle subcategory untuk Atasan
        String currentCategory = currentItem.getCategory();
        if (currentCategory.startsWith("Atasan")) {
            // Set radio button Atasan
            RadioButton rbAtasan = dialogView.findViewById(R.id.rb_atasan);
            rbAtasan.setChecked(true);

            // Show subcategory
            rgSubCategory.setVisibility(View.VISIBLE);

            // Set subcategory (Outer atau Inner)
            if (currentCategory.contains("Outer")) {
                RadioButton rbOuter = dialogView.findViewById(R.id.rb_outer);
                rbOuter.setChecked(true);
            } else if (currentCategory.contains("Inner")) {
                RadioButton rbInner = dialogView.findViewById(R.id.rb_inner);
                rbInner.setChecked(true);
            }
        } else {
            // Kategori selain Atasan
            for (int i = 0; i < rgCategory.getChildCount(); i++) {
                View child = rgCategory.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    if (rb.getText().toString().equalsIgnoreCase(currentCategory)) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        }

        // Setup listener untuk show/hide subcategory
        rgCategory.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = dialogView.findViewById(checkedId);
            if (selected != null && "Atasan".equalsIgnoreCase(selected.getText().toString())) {
                rgSubCategory.setVisibility(View.VISIBLE);
            } else {
                rgSubCategory.setVisibility(View.GONE);
                rgSubCategory.clearCheck();
            }
        });

        // Motif
        for (int i = 0; i < rgPattern.getChildCount(); i++) {
            View child = rgPattern.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                if (rb.getText().toString().equalsIgnoreCase(currentItem.getPattern())) {
                    rb.setChecked(true);
                    break;
                }
            }
        }

        // Cuaca
        for (int i = 0; i < cgWeather.getChildCount(); i++) {
            View child = cgWeather.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setChecked(currentItem.getWeather().contains(chip.getText().toString()));
            }
        }

        // Warna
        for (int i = 0; i < cgColors.getChildCount(); i++) {
            View child = cgColors.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setChecked(currentItem.getColors().contains(chip.getText().toString()));
            }
        }

        // === Bangun dialog ===
        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Detail Pakaian")
                .setView(dialogView)
                .setPositiveButton("Simpan Perubahan", (dialog, id) -> {
                    int selectedCategoryId = rgCategory.getCheckedRadioButtonId();
                    int selectedPatternId = rgPattern.getCheckedRadioButtonId();

                    if (selectedCategoryId == -1 || selectedPatternId == -1) {
                        Toast.makeText(getContext(), "Kategori dan Motif wajib diisi!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String category = ((RadioButton) dialogView.findViewById(selectedCategoryId)).getText().toString();

                    // Jika kategori = Atasan, ambil subkategori
                    if ("Atasan".equalsIgnoreCase(category)) {
                        int subId = rgSubCategory.getCheckedRadioButtonId();
                        if (subId == -1) {
                            Toast.makeText(getContext(), "Pilih jenis Atasan (Outer / Inner)!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String subCategory = ((RadioButton) dialogView.findViewById(subId)).getText().toString();
                        category = category + " - " + subCategory;
                    }

                    String pattern = ((RadioButton) dialogView.findViewById(selectedPatternId)).getText().toString();
                    String notes = etNotes.getText() != null ? etNotes.getText().toString() : "";
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";

                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Nama pakaian wajib diisi!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<String> weather = new ArrayList<>();
                    for (int chipId : cgWeather.getCheckedChipIds()) {
                        weather.add(((Chip) dialogView.findViewById(chipId)).getText().toString());
                    }

                    List<String> colors = new ArrayList<>();
                    for (int chipId : cgColors.getCheckedChipIds()) {
                        colors.add(((Chip) dialogView.findViewById(chipId)).getText().toString());
                    }

                    // Update ke database
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    db.clothingDao().updateItemWithName(
                            currentItem.getId(),
                            category,
                            String.join(",", weather),
                            pattern,
                            String.join(",", colors),
                            notes,
                            name
                    );

                    // Update di memori juga
                    currentItem.setCategory(category);
                    currentItem.setPattern(pattern);
                    currentItem.setWeather(weather);
                    currentItem.setColors(colors);
                    currentItem.setNotes(notes);
                    currentItem.setName(name);

                    // Refresh UI di detail
                    updateDetailUI();

                    Toast.makeText(getContext(), "Data pakaian diperbarui", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setTitle(getString(R.string.app_name));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(requireView());
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            navController.navigateUp();
            return true;
        } else if (itemId == R.id.action_delete) {
            showDeleteConfirmationDialog(navController);
            return true;
        } else if (itemId == R.id.action_edit) {
            showEditItemDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
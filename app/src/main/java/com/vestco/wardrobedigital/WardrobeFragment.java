package com.vestco.wardrobedigital;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vestco.wardrobedigital.api.ApiClient;
import com.vestco.wardrobedigital.api.RemoveBgService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class WardrobeFragment extends Fragment {

    private RecyclerView rvWardrobeItems;
    private WardrobeAdapter adapter;
    private final ArrayList<ClothingItem> clothingItems = new ArrayList<>();
    private ChipGroup chipGroup;
    private static final String REMOVE_BG_API = BuildConfig.REMOVEBG_API_KEY;



    // ✅ Permission launcher modern
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(getContext(), "Izin diperlukan untuk mengakses galeri", Toast.LENGTH_SHORT).show();
                }
            });

    // ✅ Modern ActivityResult for gallery
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
                        requireContext().getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        showAddItemDetailsDialog(selectedImageUri);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wardrobe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvWardrobeItems = view.findViewById(R.id.rv_wardrobe_items);
        chipGroup = view.findViewById(R.id.chip_all).getParent() instanceof ChipGroup ?
                (ChipGroup) view.findViewById(R.id.chip_all).getParent() : null;

        // ✅ Setup Add Item Card
        CardView addItemCard = view.findViewById(R.id.card_add_item);
        if (addItemCard != null) {
            addItemCard.setOnClickListener(v -> openGallery());
        }

        // ✅ Setup RecyclerView
        adapter = new WardrobeAdapter(clothingItems, requireContext());
        rvWardrobeItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvWardrobeItems.setAdapter(adapter);

        // ✅ Setup Filter Chips
        setupFilterChips();

        // ✅ Request permissions
        requestStoragePermission();

        // ✅ Load data dari database
        loadWardrobeData();
    }

    public void triggerAddItemFlow() {
        // Pastikan view sudah dibuat untuk mencegah NullPointerException
        if (isAdded() && getView() != null) {
            openGallery();
        } else {
            // Jika view belum siap, kita bisa tunda sedikit atau log pesan.
            // Untuk sekarang, kita tampilkan Toast.
            Toast.makeText(getContext(), "Harap tunggu sebentar...", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFilterChips() {
        if (chipGroup == null) return;

        Chip chipAll = getView().findViewById(R.id.chip_all);
        Chip chipTops = getView().findViewById(R.id.chip_tops);
        Chip chipBottoms = getView().findViewById(R.id.chip_bottoms);
        Chip chipShoes = getView().findViewById(R.id.chip_shoes);
        Chip chipMore = getView().findViewById(R.id.chip_more);

        chipAll.setOnClickListener(v -> filterItems("all"));
        chipTops.setOnClickListener(v -> filterItems("atasan"));
        chipBottoms.setOnClickListener(v -> filterItems("bawahan"));
        chipShoes.setOnClickListener(v -> filterItems("sepatu"));
        chipMore.setOnClickListener(v -> filterItems("aksesoris"));
    }

    private void filterItems(String category) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        List<ClothingItemEntity> allItems = db.clothingDao().getAll();

        clothingItems.clear();

        for (ClothingItemEntity entity : allItems) {
            if (category.equals("all") ||
                    entity.getCategory().toLowerCase().contains(category.toLowerCase())) {
                ClothingItem item = new ClothingItem(
                        entity.getId(),
                        Uri.parse(entity.getImageUri()),
                        entity.getCategory(),
                        Arrays.asList(entity.getWeather().split(",")),
                        entity.getPattern(),
                        Arrays.asList(entity.getColors().split(",")),
                        entity.getNotes(),
                        entity.getName()
                );
                clothingItems.add(item);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        } else {
            if (requireContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void loadWardrobeData() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        List<ClothingItemEntity> savedItems = db.clothingDao().getAll();

        clothingItems.clear();
        for (ClothingItemEntity entity : savedItems) {
            ClothingItem item = new ClothingItem(
                    entity.getId(),
                    Uri.parse(entity.getImageUri()),
                    entity.getCategory(),
                    Arrays.asList(entity.getWeather().split(",")),
                    entity.getPattern(),
                    Arrays.asList(entity.getColors().split(",")),
                    entity.getNotes(),
                    entity.getName()
            );
            clothingItems.add(item);
        }

        adapter.notifyDataSetChanged();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryLauncher.launch(intent);
    }

    private void showAddItemDetailsDialog(Uri imageUri) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item_details, null);

        final RadioGroup rgCategory = dialogView.findViewById(R.id.rg_category);
        final ChipGroup cgWeather = dialogView.findViewById(R.id.cg_weather);
        final RadioGroup rgPattern = dialogView.findViewById(R.id.rg_pattern);
        final ChipGroup cgColors = dialogView.findViewById(R.id.cg_colors);
        final TextInputEditText etNotes = dialogView.findViewById(R.id.et_notes);
        final RadioGroup rgSubCategory = dialogView.findViewById(R.id.rg_subcategory);
        final TextInputEditText etName = dialogView.findViewById(R.id.et_name);
        final android.widget.CheckBox cbRemoveBackground = dialogView.findViewById(R.id.cb_remove_background);

        rgCategory.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = dialogView.findViewById(checkedId);
            if (selected != null && "Atasan".equalsIgnoreCase(selected.getText().toString())) {
                rgSubCategory.setVisibility(View.VISIBLE);
            } else {
                rgSubCategory.setVisibility(View.GONE);
                rgSubCategory.clearCheck();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle("Detail Item Pakaian")
                .setPositiveButton("Simpan", (dialog, id) -> {
                    int selectedCategoryId = rgCategory.getCheckedRadioButtonId();
                    int selectedPatternId = rgPattern.getCheckedRadioButtonId();

                    if (selectedCategoryId == -1 || selectedPatternId == -1) {
                        Toast.makeText(getContext(), "Kategori dan Motif wajib diisi!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String category = ((RadioButton) dialogView.findViewById(selectedCategoryId)).getText().toString();

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

                    List<String> weather = new ArrayList<>();
                    for (int chipId : cgWeather.getCheckedChipIds()) {
                        weather.add(((Chip) dialogView.findViewById(chipId)).getText().toString());
                    }

                    List<String> colors = new ArrayList<>();
                    for (int chipId : cgColors.getCheckedChipIds()) {
                        colors.add(((Chip) dialogView.findViewById(chipId)).getText().toString());
                    }

                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";

                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Nama pakaian wajib diisi!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ✅ Check apakah user mau remove background atau tidak
                    boolean shouldRemoveBackground = cbRemoveBackground.isChecked();

                    if (shouldRemoveBackground) {
                        removeBackgroundAndSave(imageUri, category, weather, pattern, colors, notes, name);
                    } else {
                        saveDirectlyWithoutRemovingBackground(imageUri, category, weather, pattern, colors, notes, name);
                    }
                })
                .setNegativeButton("Batal", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        String fileName = "upload_image_" + System.currentTimeMillis() + ".png";
        File tempFile = new File(requireContext().getCacheDir(), fileName);

        try (java.io.InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }

    private void removeBackgroundAndSave(Uri imageUri, String category, List<String> weather,
                                         String pattern, List<String> colors, String notes, String name) {

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Menghapus background, mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        File imageFile;
        try {
            imageFile = createTempFileFromUri(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Gagal membaca gambar", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        RequestBody requestFile = RequestBody.create(imageFile, okhttp3.MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("image_file", imageFile.getName(), requestFile);

        RemoveBgService service = ApiClient.getClient().create(RemoveBgService.class);

        Call<ResponseBody> call = service.removeBackground(body, REMOVE_BG_API);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File outputFile = new File(requireContext().getCacheDir(),
                                "transparent_" + System.currentTimeMillis() + "_" + imageFile.getName());
                        FileOutputStream fos = new FileOutputStream(outputFile);
                        fos.write(response.body().bytes());
                        fos.close();

                        Uri transparentUri = Uri.fromFile(outputFile);
                        AppDatabase db = AppDatabase.getInstance(requireContext());
                        ClothingItemEntity entity = new ClothingItemEntity(
                                transparentUri.toString(),
                                category,
                                String.join(",", weather),
                                pattern,
                                String.join(",", colors),
                                notes,
                                name
                        );

                        long newId = db.clothingDao().insertAndReturnId(entity);

                        ClothingItem newItem = new ClothingItem((int) newId, transparentUri, category, weather, pattern, colors, notes, name);
                        clothingItems.add(newItem);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), "Gambar disimpan dengan background transparan!", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Gagal menyimpan hasil!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal hapus background: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Simpan gambar langsung tanpa remove background
     */
    private void saveDirectlyWithoutRemovingBackground(Uri imageUri, String category, List<String> weather,
                                                       String pattern, List<String> colors, String notes, String name) {

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Menyimpan gambar...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Langsung simpan ke database tanpa processing
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                ClothingItemEntity entity = new ClothingItemEntity(
                        imageUri.toString(),
                        category,
                        String.join(",", weather),
                        pattern,
                        String.join(",", colors),
                        notes,
                        name
                );

                long newId = db.clothingDao().insertAndReturnId(entity);

                ClothingItem newItem = new ClothingItem((int) newId, imageUri, category, weather, pattern, colors, notes, name);
                clothingItems.add(newItem);
                adapter.notifyDataSetChanged();

                progressDialog.dismiss();
                Toast.makeText(getContext(), "Gambar disimpan!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(getContext(), "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, 500); // Small delay untuk smooth UX
    }
}
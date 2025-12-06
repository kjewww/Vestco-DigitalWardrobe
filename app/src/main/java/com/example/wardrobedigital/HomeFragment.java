package com.example.wardrobedigital;

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
import androidx.navigation.Navigation;

import com.example.wardrobedigital.api.ApiClient;
import com.example.wardrobedigital.api.RemoveBgService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class HomeFragment extends Fragment {

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        requireContext().getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                        showAddItemDetailsDialog(selectedImageUri);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView addItemCard = view.findViewById(R.id.card_add_item_home);
        addItemCard.setOnClickListener(v -> openGallery());

        CardView createOutfitCard = view.findViewById(R.id.card_create_outfit_home);
        createOutfitCard.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_nav_home_to_createOutfitFragment);
        });

        requestStoragePermission();
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
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
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";

                    if (selectedCategoryId == -1 || selectedPatternId == -1 || name.isEmpty()) {
                        Toast.makeText(getContext(), "Nama, Kategori, dan Motif wajib diisi!", Toast.LENGTH_SHORT).show();
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
                    for (int i = 0; i < cgWeather.getChildCount(); i++) {
                        Chip chip = (Chip) cgWeather.getChildAt(i);
                        if (chip.isChecked()) {
                            weather.add(chip.getText().toString());
                        }
                    }

                    List<String> colors = new ArrayList<>();
                    for (int i = 0; i < cgColors.getChildCount(); i++) {
                        Chip chip = (Chip) cgColors.getChildAt(i);
                        if (chip.isChecked()) {
                            colors.add(chip.getText().toString());
                        }
                    }

                    removeBackgroundAndSave(imageUri, category, weather, pattern, colors, notes, name);
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

        Call<ResponseBody> call = service.removeBackground(body, "uLqwMG8CMJH6jURdnzFiJAaw");
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File outputFile = new File(requireContext().getCacheDir(),
                                "transparent_" + System.currentTimeMillis() + "_" + imageFile.getName());

                        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                            outputStream.write(response.body().bytes());
                        }

                        AppDatabase db = AppDatabase.getInstance(requireContext());
                        ClothingItemEntity entity = new ClothingItemEntity(
                                Uri.fromFile(outputFile).toString(),
                                category,
                                String.join(",", weather),
                                pattern,
                                String.join(",", colors),
                                notes,
                                name
                        );

                        db.clothingDao().insert(entity);
                        Toast.makeText(getContext(), "Item berhasil disimpan!", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Gagal menyimpan gambar tanpa background", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal menghapus background", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(getContext(), "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

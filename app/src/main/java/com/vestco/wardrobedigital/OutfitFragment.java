// File: app/src/main/java/com/example/wardrobedigital/OutfitFragment.java

package com.vestco.wardrobedigital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OutfitFragment extends Fragment implements OutfitAdapter.OnOutfitListener {

    private RecyclerView recyclerView;
    private OutfitAdapter adapter;
    private final List<Outfit> outfitList = new ArrayList<>();
    private long lastChangeTimestamp = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outfit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.rv_outfits);
        setupRecyclerView();

        // Inisialisasi tombol "Create New Outfit"
        CardView createOutfitCard = view.findViewById(R.id.card_create_outfit); // Pastikan ID ini ada di XML
        createOutfitCard.setOnClickListener(v -> {
            // Arahkan ke fragment untuk membuat outfit baru
            Navigation.findNavController(v).navigate(R.id.action_nav_outfit_to_createOutfitFragment);
        });

        // Muat data awal
        loadOutfitsFromDb();
    }

    private void setupRecyclerView() {
        adapter = new OutfitAdapter(this); // 'this' merujuk ke implementasi OnOutfitListener
        recyclerView.setAdapter(adapter);
        // Layout manager sudah diatur di XML, tapi bisa juga diatur di sini:
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cek apakah ada perubahan data saat fragment kembali aktif
        long currentTimestamp = DataChangeNotifier.getOutfitChangeTimestamp(requireContext());
        if (currentTimestamp != lastChangeTimestamp) {
            lastChangeTimestamp = currentTimestamp;
            loadOutfitsFromDb();
        }
    }

    private void loadOutfitsFromDb() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        // Menjalankan query database di background thread
        db.databaseWriteExecutor.execute(() -> {
            List<OutfitEntity> savedOutfits = db.outfitDao().getAllOutfits();
            outfitList.clear();

            for (OutfitEntity entity : savedOutfits) {
                // Konversi dari Entity ke objek Parcelable
                ClothingItem outer = entity.getOuterId() != null ? loadClothingItem(entity.getOuterId(), db) : null;
                ClothingItem inner = entity.getInnerId() != null ? loadClothingItem(entity.getInnerId(), db) : null;
                ClothingItem bawahan = loadClothingItem(entity.getBawahanId(), db);
                ClothingItem sepatu = loadClothingItem(entity.getSepatuId(), db);
                ClothingItem aksesoris = entity.getAksesorisId() != null ? loadClothingItem(entity.getAksesorisId(), db) : null;

                Outfit outfit = new Outfit(
                        entity.getId(),
                        entity.getName(),
                        outer,
                        inner,
                        bawahan,
                        sepatu,
                        aksesoris,
                        entity.getCreatedAt()
                );
                outfitList.add(outfit);
            }

            // Update UI di main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.submitList(outfitList);
                });
            }
        });
    }

    private ClothingItem loadClothingItem(int id, AppDatabase db) {
        // Menggunakan DAO yang ada
        ClothingItemEntity entity = db.clothingDao().getById(id);
        if (entity != null) {
            return new ClothingItem(
                    entity.getId(),
                    android.net.Uri.parse(entity.getImageUri()),
                    entity.getCategory(),
                    Arrays.asList(entity.getWeather().split(",")),
                    entity.getPattern(),
                    Arrays.asList(entity.getColors().split(",")),
                    entity.getNotes(),
                    entity.getName()
            );
        }
        return null;
    }

    // -- Implementasi dari OnOutfitListener --

    @Override
    public void onOutfitClick(Outfit outfit) {
        // Aksi saat item outfit di-klik
        OutfitFragmentDirections.ActionNavOutfitToOutfitDetailFragment action =
                OutfitFragmentDirections.actionNavOutfitToOutfitDetailFragment(outfit);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onOutfitLongClick(Outfit outfit) {
        // Aksi saat item outfit di-klik lama (untuk hapus)
        showDeleteOutfitDialog(outfit);
    }

    private void showDeleteOutfitDialog(Outfit outfit) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Outfit")
                .setMessage("Apakah Anda yakin ingin menghapus outfit '" + outfit.getName() + "'?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    db.databaseWriteExecutor.execute(() -> {
                        db.outfitDao().deleteOutfitById(outfit.getId());
                        // Muat ulang data setelah menghapus
                        getActivity().runOnUiThread(this::loadOutfitsFromDb);
                    });
                    Toast.makeText(getContext(), "Outfit dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}

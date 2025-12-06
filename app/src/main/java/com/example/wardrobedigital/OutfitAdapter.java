// File: app/src/main/java/com/example/wardrobedigital/OutfitAdapter.java

package com.example.wardrobedigital;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {

    private final List<Outfit> outfitList = new ArrayList<>();
    private final OnOutfitListener listener;

    // Interface untuk menangani klik
    public interface OnOutfitListener {
        void onOutfitClick(Outfit outfit);
        void onOutfitLongClick(Outfit outfit);
    }

    public OutfitAdapter(OnOutfitListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout item_outfit.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outfit, parent, false);
        return new OutfitViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder holder, int position) {
        Outfit outfit = outfitList.get(position);
        holder.bind(outfit);
    }

    @Override
    public int getItemCount() {
        return outfitList.size();
    }

    // Metode untuk mengupdate data di adapter
    public void submitList(List<Outfit> newOutfitList) {
        outfitList.clear();
        outfitList.addAll(newOutfitList);
        notifyDataSetChanged(); // Memberitahu RecyclerView bahwa data telah berubah
    }

    // ViewHolder untuk setiap item outfit
    static class OutfitViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOutfitName;
        private final ImageView img1, img2, img3, img4;
        private final MaterialCardView cardView;
        private final OnOutfitListener listener;

        public OutfitViewHolder(@NonNull View itemView, OnOutfitListener listener) {
            super(itemView);
            this.listener = listener;
            tvOutfitName = itemView.findViewById(R.id.tv_outfit_name);
            img1 = itemView.findViewById(R.id.img_item_1);
            img2 = itemView.findViewById(R.id.img_item_2);
            img3 = itemView.findViewById(R.id.img_item_3);
            img4 = itemView.findViewById(R.id.img_item_4);
            cardView = itemView.findViewById(R.id.outfit_card);
        }

        public void bind(final Outfit outfit) {
            tvOutfitName.setText(outfit.getName());

            // Buat daftar ImageView untuk memudahkan
            ImageView[] imageViews = {img1, img2, img3, img4};

            // Buat daftar item pakaian yang akan ditampilkan
            List<ClothingItem> displayItems = new ArrayList<>();
            if (outfit.getOuter() != null) displayItems.add(outfit.getOuter());
            if (outfit.getInner() != null) displayItems.add(outfit.getInner());
            if (outfit.getBawahan() != null) displayItems.add(outfit.getBawahan());
            if (outfit.getSepatu() != null) displayItems.add(outfit.getSepatu());
            // Anda bisa menambahkan aksesoris jika ada slot ke-5

            // Loop untuk mengisi ImageView
            for (int i = 0; i < imageViews.length; i++) {
                if (i < displayItems.size() && displayItems.get(i) != null) {
                    Glide.with(itemView.getContext())
                            .load(displayItems.get(i).getImageUri())
                            .centerCrop()
                            .into(imageViews[i]);
                    imageViews[i].setVisibility(View.VISIBLE);
                } else {
                    // Sembunyikan jika tidak ada gambar
                    imageViews[i].setVisibility(View.INVISIBLE);
                }
            }

            // Atur listener untuk klik dan long-klik
            cardView.setOnClickListener(v -> listener.onOutfitClick(outfit));
            cardView.setOnLongClickListener(v -> {
                listener.onOutfitLongClick(outfit);
                return true; // Penting untuk menandakan event sudah ditangani
            });
        }
    }
}

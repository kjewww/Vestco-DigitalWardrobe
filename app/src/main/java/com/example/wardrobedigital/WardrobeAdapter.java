package com.example.wardrobedigital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WardrobeAdapter extends RecyclerView.Adapter<WardrobeAdapter.ViewHolder> {

    private List<ClothingItem> items;
    private Context context;

    public WardrobeAdapter(List<ClothingItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wardrobe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClothingItem item = items.get(position);

        // Load image dengan Glide
        Glide.with(context)
                .load(item.getImageUri())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivItem);

        // Set label kategori (remove "Atasan - " prefix)
        String category = item.getCategory().replace("Atasan - ", "");
        holder.tvCategory.setText(category);

        // Set warna label berdasarkan kategori
        int backgroundColor;
        String categoryLower = item.getCategory().toLowerCase();
        if (categoryLower.contains("atasan") || categoryLower.contains("outer") || categoryLower.contains("inner")) {
            backgroundColor = 0xFF3366CC; // Biru
        } else if (categoryLower.contains("bawahan")) {
            backgroundColor = 0xFF33CC66; // Hijau
        } else if (categoryLower.contains("sepatu")) {
            backgroundColor = 0xFFCC6633; // Orange
        } else {
            backgroundColor = 0xFF660000; // Hitam transparan
        }
        holder.tvCategory.setBackgroundColor(backgroundColor);

        // Click listener - navigate ke detail
        holder.itemView.setOnClickListener(v -> {
            WardrobeFragmentDirections.ActionWardrobeFragmentToDetailFragment action =
                    WardrobeFragmentDirections.actionWardrobeFragmentToDetailFragment(item);
            Navigation.findNavController(v).navigate(action);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem;
        TextView tvCategory;

        ViewHolder(View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.iv_wardrobe_item);
            tvCategory = itemView.findViewById(R.id.tv_category_label);
        }
    }
}
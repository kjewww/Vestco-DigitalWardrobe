//// app/src/main/java/com/example/wardrobedigital/RecentLooksAdapter.java
//package com.example.wardrobedigital;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class RecentLooksAdapter extends RecyclerView.Adapter<RecentLooksAdapter.RecentLookViewHolder> {
//
//    private List<ClothingItem> items = new ArrayList<>();
//
//    // Listener untuk menangani klik pada item
//    public interface OnItemClickListener {
//        void onItemClick(ClothingItem item);
//    }
//
//    private final OnItemClickListener listener;
//
//    public RecentLooksAdapter(OnItemClickListener listener) {
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public RecentLookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_recent_look, parent, false);
//        return new RecentLookViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecentLookViewHolder holder, int position) {
//        ClothingItem currentItem = items.get(position);
//        holder.bind(currentItem, listener);
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    // Method untuk mengupdate data di adapter
//    public void submitList(List<ClothingItem> newItems) {
//        items.clear();
//        items.addAll(newItems);
//        notifyDataSetChanged(); // Memberi tahu RecyclerView bahwa data telah berubah
//    }
//
//    // ViewHolder yang menampung view dari setiap item
////    static class RecentLookViewHolder extends RecyclerView.ViewHolder {
////        private final ImageView imageView;
////
////        public RecentLookViewHolder(@NonNull View itemView) {
////            super(itemView);
////            imageView = itemView.findViewById(R.id.iv_recent_item);
////        }
////
////        public void bind(final ClothingItem item, final OnItemClickListener listener) {
////            // Gunakan Glide untuk memuat gambar dari URI
////            Glide.with(itemView.getContext())
////                    .load(item.getImageUri())
////                    .placeholder(R.drawable.placeholder_image) // Gambar default saat loading
////                    .into(imageView);
////
////            // Atur listener klik
////            itemView.setOnClickListener(v -> listener.onItemClick(item));
////        }
////    }
//}

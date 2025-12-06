package com.example.wardrobedigital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ImageFullscreenFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_fullscreen, container, false);

        ImageView imageView = view.findViewById(R.id.fullscreen_image);

        if (getArguments() != null) {
            String imageUri = getArguments().getString("imageUri");
            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);
        }

        // Klik di mana saja untuk menutup fullscreen
        imageView.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }
}

package com.example.wardrobedigital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class OutfitDetailFragment extends Fragment {

    private Outfit currentOutfit;
    private ImageView ivOuter, ivInner, ivBawahan, ivSepatu, ivAksesoris;
    private TextView tvOuterName, tvInnerName, tvBawahanName, tvSepatuName, tvAksesorisName;
    private TextView tvOutfitName;
    private View cardOuter, cardInner, cardBawahan, cardSepatu, cardAksesoris;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            currentOutfit = OutfitDetailFragmentArgs.fromBundle(getArguments()).getOutfit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outfit_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set ActionBar
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("Detail Outfit");
            }
        }

        // Initialize views
        tvOutfitName = view.findViewById(R.id.tv_outfit_name);

        cardOuter = view.findViewById(R.id.card_outer);
        cardInner = view.findViewById(R.id.card_inner);
        cardBawahan = view.findViewById(R.id.card_bawahan);
        cardSepatu = view.findViewById(R.id.card_sepatu);
        cardAksesoris = view.findViewById(R.id.card_aksesoris);

        ivOuter = view.findViewById(R.id.iv_detail_outer);
        ivInner = view.findViewById(R.id.iv_detail_inner);
        ivBawahan = view.findViewById(R.id.iv_detail_bawahan);
        ivSepatu = view.findViewById(R.id.iv_detail_sepatu);
        ivAksesoris = view.findViewById(R.id.iv_detail_aksesoris);

        tvOuterName = view.findViewById(R.id.tv_detail_outer_name);
        tvInnerName = view.findViewById(R.id.tv_detail_inner_name);
        tvBawahanName = view.findViewById(R.id.tv_detail_bawahan_name);
        tvSepatuName = view.findViewById(R.id.tv_detail_sepatu_name);
        tvAksesorisName = view.findViewById(R.id.tv_detail_aksesoris_name);

        if (currentOutfit != null) {
            displayOutfitDetails();
        }
    }

    private void displayOutfitDetails() {
        // Outfit Name
        tvOutfitName.setText(currentOutfit.getName());

        // Outer
        if (currentOutfit.getOuter() != null) {
            cardOuter.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentOutfit.getOuter().getImageUri()).into(ivOuter);
            tvOuterName.setText(currentOutfit.getOuter().getName());

            // Click to view full detail
            cardOuter.setOnClickListener(v -> navigateToItemDetail(currentOutfit.getOuter()));
        } else {
            cardOuter.setVisibility(View.GONE);
        }

        // Inner
        if (currentOutfit.getInner() != null) {
            cardInner.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentOutfit.getInner().getImageUri()).into(ivInner);
            tvInnerName.setText(currentOutfit.getInner().getName());

            cardInner.setOnClickListener(v -> navigateToItemDetail(currentOutfit.getInner()));
        } else {
            cardInner.setVisibility(View.GONE);
        }

        // Bawahan
        if (currentOutfit.getBawahan() != null) {
            cardBawahan.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentOutfit.getBawahan().getImageUri()).into(ivBawahan);
            tvBawahanName.setText(currentOutfit.getBawahan().getName());

            cardBawahan.setOnClickListener(v -> navigateToItemDetail(currentOutfit.getBawahan()));
        } else {
            cardBawahan.setVisibility(View.GONE);
        }

        // Sepatu
        if (currentOutfit.getSepatu() != null) {
            cardSepatu.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentOutfit.getSepatu().getImageUri()).into(ivSepatu);
            tvSepatuName.setText(currentOutfit.getSepatu().getName());

            cardSepatu.setOnClickListener(v -> navigateToItemDetail(currentOutfit.getSepatu()));
        } else {
            cardSepatu.setVisibility(View.GONE);
        }

        // Aksesoris
        if (currentOutfit.getAksesoris() != null) {
            cardAksesoris.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentOutfit.getAksesoris().getImageUri()).into(ivAksesoris);
            tvAksesorisName.setText(currentOutfit.getAksesoris().getName());

            cardAksesoris.setOnClickListener(v -> navigateToItemDetail(currentOutfit.getAksesoris()));
        } else {
            cardAksesoris.setVisibility(View.GONE);
        }
    }

    private void navigateToItemDetail(ClothingItem item) {
        OutfitDetailFragmentDirections.ActionOutfitDetailFragmentToDetailFragment action =
                OutfitDetailFragmentDirections.actionOutfitDetailFragmentToDetailFragment(item);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.outfit_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(requireView());
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            navController.navigateUp();
            return true;
        } else if (itemId == R.id.action_delete_outfit) {
            showDeleteConfirmationDialog(navController);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog(NavController navController) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Outfit")
                .setMessage("Apakah Anda yakin ingin menghapus outfit ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    if (currentOutfit != null) {
                        AppDatabase db = AppDatabase.getInstance(requireContext());
                        db.outfitDao().deleteOutfitById(currentOutfit.getId());

                        Toast.makeText(getContext(), "Outfit dihapus", Toast.LENGTH_SHORT).show();
                        navController.navigateUp();
                    }
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
}
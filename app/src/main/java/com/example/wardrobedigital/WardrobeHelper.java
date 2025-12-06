package com.example.wardrobedigital;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WardrobeHelper {

    private Context context;

    public WardrobeHelper(Context context) {
        this.context = context;
    }

    /**
     * Mendapatkan semua item pakaian dalam format JSON untuk AI
     */
    public JsonObject getAllClothingItemsAsJson() {
        AppDatabase db = AppDatabase.getInstance(context);
        List<ClothingItemEntity> items = db.clothingDao().getAll();

        JsonObject result = new JsonObject();
        result.addProperty("total_items", items.size());

        // Group by category
        JsonArray outers = new JsonArray();
        JsonArray inners = new JsonArray();
        JsonArray bawahans = new JsonArray();
        JsonArray sepatus = new JsonArray();
        JsonArray aksesoris = new JsonArray();

        for (ClothingItemEntity item : items) {
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("id", item.getId());
            itemJson.addProperty("name", item.getName());
            itemJson.addProperty("category", item.getCategory());
            itemJson.addProperty("pattern", item.getPattern());
            itemJson.addProperty("weather", item.getWeather());
            itemJson.addProperty("colors", item.getColors());

            String category = item.getCategory().toLowerCase();
            if (category.contains("outer")) {
                outers.add(itemJson);
            } else if (category.contains("inner")) {
                inners.add(itemJson);
            } else if (category.contains("bawahan")) {
                bawahans.add(itemJson);
            } else if (category.contains("sepatu")) {
                sepatus.add(itemJson);
            } else if (category.contains("aksesoris")) {
                aksesoris.add(itemJson);
            }
        }

        result.add("outers", outers);
        result.add("inners", inners);
        result.add("bawahans", bawahans);
        result.add("sepatus", sepatus);
        result.add("aksesoris", aksesoris);

        return result;
    }

    /**
     * Mendapatkan ringkasan pakaian dalam format string untuk system prompt
     */
    public String getWardrobeSummary() {
        AppDatabase db = AppDatabase.getInstance(context);
        List<ClothingItemEntity> items = db.clothingDao().getAll();

        int outerCount = 0, innerCount = 0, bawahanCount = 0, sepatuCount = 0, aksesorisCount = 0;

        for (ClothingItemEntity item : items) {
            String category = item.getCategory().toLowerCase();
            if (category.contains("outer")) outerCount++;
            else if (category.contains("inner")) innerCount++;
            else if (category.contains("bawahan")) bawahanCount++;
            else if (category.contains("sepatu")) sepatuCount++;
            else if (category.contains("aksesoris")) aksesorisCount++;
        }

        return String.format(
                "User memiliki %d item pakaian: %d outer, %d inner, %d bawahan, %d sepatu, %d aksesoris.",
                items.size(), outerCount, innerCount, bawahanCount, sepatuCount, aksesorisCount
        );
    }

    /**
     * Membuat outfit berdasarkan ID yang dipilih AI
     */
    public boolean createOutfit(String outfitName, Integer outerId, Integer innerId,
                                int bawahanId, int sepatuId, Integer aksesorisId) {
        try {
            AppDatabase db = AppDatabase.getInstance(context);

            OutfitEntity outfit = new OutfitEntity(
                    outfitName,
                    outerId,
                    innerId,
                    bawahanId,
                    sepatuId,
                    aksesorisId,
                    System.currentTimeMillis()
            );

            db.outfitDao().insertOutfit(outfit);

            // ✅ Notify bahwa outfit berubah
            DataChangeNotifier.notifyOutfitChanged(context);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mendapatkan detail item berdasarkan ID
     */
    public ClothingItemEntity getItemById(int id) {
        AppDatabase db = AppDatabase.getInstance(context);
        List<ClothingItemEntity> items = db.clothingDao().getAll();

        for (ClothingItemEntity item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Format daftar pakaian untuk ditampilkan ke AI
     */
    public String getClothingListForPrompt() {
        AppDatabase db = AppDatabase.getInstance(context);
        List<ClothingItemEntity> items = db.clothingDao().getAll();

        if (items.isEmpty()) {
            return "User belum menambahkan pakaian apapun.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Daftar Pakaian User:\n\n");

        // Group by category
        List<ClothingItemEntity> outers = new ArrayList<>();
        List<ClothingItemEntity> inners = new ArrayList<>();
        List<ClothingItemEntity> bawahans = new ArrayList<>();
        List<ClothingItemEntity> sepatus = new ArrayList<>();
        List<ClothingItemEntity> aksesoris = new ArrayList<>();

        for (ClothingItemEntity item : items) {
            String category = item.getCategory().toLowerCase();
            if (category.contains("outer")) outers.add(item);
            else if (category.contains("inner")) inners.add(item);
            else if (category.contains("bawahan")) bawahans.add(item);
            else if (category.contains("sepatu")) sepatus.add(item);
            else if (category.contains("aksesoris")) aksesoris.add(item);
        }

        // Format each category
        if (!outers.isEmpty()) {
            sb.append("OUTER:\n");
            for (ClothingItemEntity item : outers) {
                sb.append(String.format("- [ID:%d] %s (%s, warna: %s)\n",
                        item.getId(), item.getName(), item.getPattern(), item.getColors()));
            }
            sb.append("\n");
        }

        if (!inners.isEmpty()) {
            sb.append("INNER:\n");
            for (ClothingItemEntity item : inners) {
                sb.append(String.format("- [ID:%d] %s (%s, warna: %s)\n",
                        item.getId(), item.getName(), item.getPattern(), item.getColors()));
            }
            sb.append("\n");
        }

        if (!bawahans.isEmpty()) {
            sb.append("BAWAHAN:\n");
            for (ClothingItemEntity item : bawahans) {
                sb.append(String.format("- [ID:%d] %s (%s, warna: %s)\n",
                        item.getId(), item.getName(), item.getPattern(), item.getColors()));
            }
            sb.append("\n");
        }

        if (!sepatus.isEmpty()) {
            sb.append("SEPATU:\n");
            for (ClothingItemEntity item : sepatus) {
                sb.append(String.format("- [ID:%d] %s (%s, warna: %s)\n",
                        item.getId(), item.getName(), item.getPattern(), item.getColors()));
            }
            sb.append("\n");
        }

        if (!aksesoris.isEmpty()) {
            sb.append("AKSESORIS:\n");
            for (ClothingItemEntity item : aksesoris) {
                sb.append(String.format("- [ID:%d] %s (%s, warna: %s)\n",
                        item.getId(), item.getName(), item.getPattern(), item.getColors()));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Mendapatkan semua outfit dalam format string untuk AI
     */
    public String getOutfitListForPrompt() {
        AppDatabase db = AppDatabase.getInstance(context);
        List<OutfitEntity> outfits = db.outfitDao().getAllOutfits();

        if (outfits.isEmpty()) {
            return "User belum membuat outfit apapun.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Daftar Outfit yang Sudah Dibuat:\n\n");

        for (OutfitEntity outfit : outfits) {
            sb.append(String.format("📌 %s\n", outfit.getName()));

            // Get item details
            if (outfit.getOuterId() != null) {
                ClothingItemEntity outer = getItemById(outfit.getOuterId());
                if (outer != null) {
                    sb.append(String.format("   - Outer: %s (ID:%d)\n", outer.getName(), outer.getId()));
                }
            }

            if (outfit.getInnerId() != null) {
                ClothingItemEntity inner = getItemById(outfit.getInnerId());
                if (inner != null) {
                    sb.append(String.format("   - Inner: %s (ID:%d)\n", inner.getName(), inner.getId()));
                }
            }

            ClothingItemEntity bawahan = getItemById(outfit.getBawahanId());
            if (bawahan != null) {
                sb.append(String.format("   - Bawahan: %s (ID:%d)\n", bawahan.getName(), bawahan.getId()));
            }

            ClothingItemEntity sepatu = getItemById(outfit.getSepatuId());
            if (sepatu != null) {
                sb.append(String.format("   - Sepatu: %s (ID:%d)\n", sepatu.getName(), sepatu.getId()));
            }

            if (outfit.getAksesorisId() != null) {
                ClothingItemEntity aksesoris = getItemById(outfit.getAksesorisId());
                if (aksesoris != null) {
                    sb.append(String.format("   - Aksesoris: %s (ID:%d)\n", aksesoris.getName(), aksesoris.getId()));
                }
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Mendapatkan ringkasan outfit untuk system prompt
     */
    public String getOutfitSummary() {
        AppDatabase db = AppDatabase.getInstance(context);
        List<OutfitEntity> outfits = db.outfitDao().getAllOutfits();

        return String.format("User sudah membuat %d outfit.", outfits.size());
    }

    /**
     * Check apakah kombinasi outfit sudah ada
     */
    public boolean isOutfitDuplicate(Integer outerId, Integer innerId, int bawahanId, int sepatuId) {
        AppDatabase db = AppDatabase.getInstance(context);
        List<OutfitEntity> outfits = db.outfitDao().getAllOutfits();

        for (OutfitEntity outfit : outfits) {
            boolean outerMatch = (outfit.getOuterId() == null && outerId == null) ||
                    (outfit.getOuterId() != null && outfit.getOuterId().equals(outerId));
            boolean innerMatch = (outfit.getInnerId() == null && innerId == null) ||
                    (outfit.getInnerId() != null && outfit.getInnerId().equals(innerId));
            boolean bawahanMatch = outfit.getBawahanId() == bawahanId;
            boolean sepatuMatch = outfit.getSepatuId() == sepatuId;

            if (outerMatch && innerMatch && bawahanMatch && sepatuMatch) {
                return true;
            }
        }

        return false;
    }
}
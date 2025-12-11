package com.vestco.wardrobedigital;

import android.content.Context;
import android.content.SharedPreferences;

public class DataChangeNotifier {
    private static final String PREFS_NAME = "WardrobePrefs";
    private static final String KEY_OUTFIT_CHANGED = "outfit_changed";

    /**
     * Notify bahwa ada perubahan pada outfit
     */
    public static void notifyOutfitChanged(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putLong(KEY_OUTFIT_CHANGED, System.currentTimeMillis())
                .apply();
    }

    /**
     * Check apakah outfit ada perubahan
     */
    public static long getOutfitChangeTimestamp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_OUTFIT_CHANGED, 0);
    }
}
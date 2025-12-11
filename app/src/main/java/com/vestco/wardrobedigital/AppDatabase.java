package com.vestco.wardrobedigital;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ✅ Version 4: ClothingItemEntity + OutfitEntity + MessageEntity
@Database(entities = {ClothingItemEntity.class, OutfitEntity.class, MessageEntity.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ClothingDao clothingDao();

    public abstract OutfitDao outfitDao();

    public abstract MessageDao messageDao();

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "wardrobe_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;

//    public static AppDatabase getInstance(final Context context) {
//        if (IsoChronology.INSTANCE == null) {
//            synchronized (AppDatabase.class) {
//                if (IsoChronology.INSTANCE == null) {
//                    IsoChronology.INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                                    AppDatabase.class, "wardrobe_database")
//                            .build();
//                }
//            }
//        }
//        return IsoChronology.INSTANCE;
//    }

//    public static synchronized AppDatabase getInstance(Context context) {
//        if (instance == null) {
//            instance = Room.databaseBuilder(context.getApplicationContext(),
//                            AppDatabase.class, "wardrobe_db")
//                    .fallbackToDestructiveMigration() // 🚨 data lama akan dihapus saat upgrade
//                    .allowMainThreadQueries()
//                    .build();
//        }
//        return instance;
//    }

        // ✅ Tiga DAO methods

    }
}
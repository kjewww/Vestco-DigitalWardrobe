package com.example.wardrobedigital;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClothingDao {
    @Query("SELECT * FROM clothing_items")
    List<ClothingItemEntity> getAll();

    @Insert
    long insertAndReturnId(ClothingItemEntity item);

    @Insert
    void insert(ClothingItemEntity item);

    @Delete
    void delete(ClothingItemEntity item);

    @Query("DELETE FROM clothing_items")
    void clearAll();

    @Query("UPDATE clothing_items SET notes = :notes WHERE id = :id")
    void updateNotes(int id, String notes);

    @Query("UPDATE clothing_items SET category = :category, weather = :weather, pattern = :pattern, colors = :colors, notes = :notes WHERE id = :id")
    void updateItem(int id, String category, String weather, String pattern, String colors, String notes);

    // ✅ Method baru untuk update termasuk name
    @Query("UPDATE clothing_items SET category = :category, weather = :weather, pattern = :pattern, colors = :colors, notes = :notes, name = :name WHERE id = :id")
    void updateItemWithName(int id, String category, String weather, String pattern, String colors, String notes, String name);

    @Query("SELECT * FROM clothing_items WHERE id = :id LIMIT 1")
    ClothingItemEntity getById(int id);
}
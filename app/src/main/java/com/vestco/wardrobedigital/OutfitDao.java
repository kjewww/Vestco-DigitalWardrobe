package com.vestco.wardrobedigital;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OutfitDao {

    @Query("SELECT * FROM outfits ORDER BY created_at DESC")
    List<OutfitEntity> getAllOutfits();

    @Insert
    long insertOutfit(OutfitEntity outfit);

    @Delete
    void deleteOutfit(OutfitEntity outfit);

    @Query("DELETE FROM outfits WHERE id = :outfitId")
    void deleteOutfitById(int outfitId);

    @Query("SELECT * FROM outfits WHERE id = :outfitId")
    OutfitEntity getOutfitById(int outfitId);
}
package com.example.wardrobedigital;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "outfits")
public class OutfitEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name; // Nama outfit (opsional)

    @ColumnInfo(name = "outer_id")
    private Integer outerId; // ID dari ClothingItem (nullable)

    @ColumnInfo(name = "inner_id")
    private Integer innerId; // ID dari ClothingItem (nullable)

    @ColumnInfo(name = "bawahan_id")
    private int bawahanId; // ID dari ClothingItem (wajib)

    @ColumnInfo(name = "sepatu_id")
    private int sepatuId; // ID dari ClothingItem (wajib)

    @ColumnInfo(name = "aksesoris_id")
    private Integer aksesorisId; // ID dari ClothingItem (nullable)

    @ColumnInfo(name = "created_at")
    private long createdAt; // Timestamp

    // Constructor
    public OutfitEntity(String name, Integer outerId, Integer innerId, int bawahanId,
                        int sepatuId, Integer aksesorisId, long createdAt) {
        this.name = name;
        this.outerId = outerId;
        this.innerId = innerId;
        this.bawahanId = bawahanId;
        this.sepatuId = sepatuId;
        this.aksesorisId = aksesorisId;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getOuterId() { return outerId; }
    public void setOuterId(Integer outerId) { this.outerId = outerId; }

    public Integer getInnerId() { return innerId; }
    public void setInnerId(Integer innerId) { this.innerId = innerId; }

    public int getBawahanId() { return bawahanId; }
    public void setBawahanId(int bawahanId) { this.bawahanId = bawahanId; }

    public int getSepatuId() { return sepatuId; }
    public void setSepatuId(int sepatuId) { this.sepatuId = sepatuId; }

    public Integer getAksesorisId() { return aksesorisId; }
    public void setAksesorisId(Integer aksesorisId) { this.aksesorisId = aksesorisId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
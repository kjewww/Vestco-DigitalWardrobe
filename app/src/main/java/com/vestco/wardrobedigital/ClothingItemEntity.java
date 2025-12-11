package com.vestco.wardrobedigital;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "clothing_items")
@TypeConverters({Converters.class})
public class ClothingItemEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "image_uri")
    private String imageUri;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "weather")
    private String weather;

    @ColumnInfo(name = "pattern")
    private String pattern;

    @ColumnInfo(name = "colors")
    private String colors;

    @ColumnInfo(name = "notes")
    private String notes;

    // 🆕 Tambahkan kolom baru
    @ColumnInfo(name = "name")
    private String name;

    public ClothingItemEntity(String imageUri, String category, String weather,
                              String pattern, String colors, String notes, String name) {
        this.imageUri = imageUri;
        this.category = category;
        this.weather = weather;
        this.pattern = pattern;
        this.colors = colors;
        this.notes = notes;
        this.name = name;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

// D:/Materi Kuliah/Semester 3/Mobile Development/WardrobeDigital/app/src/main/java/com/example/wardrobedigital/ClothingItem.java

package com.example.wardrobedigital;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ClothingItem implements Parcelable { // <-- Implementasikan Parcelable
    private int id; // <--- tambahkan ini
    private Uri imageUri;
    private String category;
    private List<String> weather;
    private String pattern;
    private List<String> colors;
    private String notes;
    private String name;
    // Constructor

    // Constructor untuk item baru tanpa id (saat menambah)
    // BENAR
    public ClothingItem(Uri imageUri, String category, List<String> weather, String pattern, List<String> colors, String notes, String name) {
        this(0, imageUri, category, weather, pattern, colors, notes, name);
    }


    // Constructor lengkap dengan id (saat load dari database)
    public ClothingItem(int id, Uri imageUri, String category, List<String> weather, String pattern, List<String> colors, String notes, String name) {
        this.id = id;
        this.imageUri = imageUri;
        this.category = category;
        this.weather = weather;
        this.pattern = pattern;
        this.colors = colors;
        this.notes = notes;
        this.name = name;
    }



    // --- Kode Parcelable (dibuat otomatis atau ditulis manual) ---

    // Parcelable
    protected ClothingItem(Parcel in) {
        id = in.readInt();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        category = in.readString();
        weather = in.createStringArrayList();
        pattern = in.readString();
        colors = in.createStringArrayList();
        notes = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(imageUri, flags);
        dest.writeString(category);
        dest.writeStringList(weather);
        dest.writeString(pattern);
        dest.writeStringList(colors);
        dest.writeString(notes);
        dest.writeString(name);
    }





    public static final Creator<ClothingItem> CREATOR = new Creator<ClothingItem>() {
        @Override
        public ClothingItem createFromParcel(Parcel in) {
            return new ClothingItem(in);
        }

        @Override
        public ClothingItem[] newArray(int size) {
            return new ClothingItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // --- Getters ---

    public Uri getImageUri() { return imageUri; }
    public String getCategory() { return category; }
    public List<String> getWeather() { return weather; }
    public String getPattern() { return pattern; }
    public List<String> getColors() { return colors; }
    public String getNotes() { return notes; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setWeather(List<String> weather) {
        this.weather = weather;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }
}

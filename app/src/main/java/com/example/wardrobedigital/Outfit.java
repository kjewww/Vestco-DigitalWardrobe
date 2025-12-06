package com.example.wardrobedigital;

import android.os.Parcel;
import android.os.Parcelable;

public class Outfit implements Parcelable {
    private int id;
    private String name;
    private ClothingItem outer; // nullable
    private ClothingItem inner; // nullable
    private ClothingItem bawahan; // wajib
    private ClothingItem sepatu; // wajib
    private ClothingItem aksesoris; // nullable
    private long createdAt;

    // Constructor
    public Outfit(int id, String name, ClothingItem outer, ClothingItem inner,
                  ClothingItem bawahan, ClothingItem sepatu, ClothingItem aksesoris, long createdAt) {
        this.id = id;
        this.name = name;
        this.outer = outer;
        this.inner = inner;
        this.bawahan = bawahan;
        this.sepatu = sepatu;
        this.aksesoris = aksesoris;
        this.createdAt = createdAt;
    }

    // Parcelable implementation
    protected Outfit(Parcel in) {
        id = in.readInt();
        name = in.readString();
        outer = in.readParcelable(ClothingItem.class.getClassLoader());
        inner = in.readParcelable(ClothingItem.class.getClassLoader());
        bawahan = in.readParcelable(ClothingItem.class.getClassLoader());
        sepatu = in.readParcelable(ClothingItem.class.getClassLoader());
        aksesoris = in.readParcelable(ClothingItem.class.getClassLoader());
        createdAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeParcelable(outer, flags);
        dest.writeParcelable(inner, flags);
        dest.writeParcelable(bawahan, flags);
        dest.writeParcelable(sepatu, flags);
        dest.writeParcelable(aksesoris, flags);
        dest.writeLong(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Outfit> CREATOR = new Creator<Outfit>() {
        @Override
        public Outfit createFromParcel(Parcel in) {
            return new Outfit(in);
        }

        @Override
        public Outfit[] newArray(int size) {
            return new Outfit[size];
        }
    };

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ClothingItem getOuter() { return outer; }
    public void setOuter(ClothingItem outer) { this.outer = outer; }

    public ClothingItem getInner() { return inner; }
    public void setInner(ClothingItem inner) { this.inner = inner; }

    public ClothingItem getBawahan() { return bawahan; }
    public void setBawahan(ClothingItem bawahan) { this.bawahan = bawahan; }

    public ClothingItem getSepatu() { return sepatu; }
    public void setSepatu(ClothingItem sepatu) { this.sepatu = sepatu; }

    public ClothingItem getAksesoris() { return aksesoris; }
    public void setAksesoris(ClothingItem aksesoris) { this.aksesoris = aksesoris; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
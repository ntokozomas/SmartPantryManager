package com.ntokozo.smartpantry.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * One ingredient the user has at home (a row in the "pantry_items" table).
 */
@Entity(tableName = "pantry_items")
public class PantryItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name = "";

    private double quantity;

    @NonNull
    private String unit = "pcs";

    /** Expiry date stored as days since 1970-01-01 (LocalDate.toEpochDay()); null = no expiry. */
    @Nullable
    private Long expiryEpochDay;

    /** Required by Room. */
    public PantryItem() {
    }

    @Ignore
    public PantryItem(@NonNull String name, double quantity, @NonNull String unit,
                      @Nullable Long expiryEpochDay) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryEpochDay = expiryEpochDay;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @NonNull
    public String getUnit() {
        return unit;
    }

    public void setUnit(@NonNull String unit) {
        this.unit = unit;
    }

    @Nullable
    public Long getExpiryEpochDay() {
        return expiryEpochDay;
    }

    public void setExpiryEpochDay(@Nullable Long expiryEpochDay) {
        this.expiryEpochDay = expiryEpochDay;
    }
}

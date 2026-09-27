package com.ntokozo.smartpantry.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe in the pre-loaded recipe book (a row in the "recipes" table).
 * Its ingredients live in the separate "recipe_ingredients" table.
 */
@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name = "";

    @NonNull
    private String emoji = "🍽️";

    @NonNull
    private String description = "";

    /** Preparation steps separated by new lines. */
    @NonNull
    private String steps = "";

    /** Required by Room. */
    public Recipe() {
    }

    @Ignore
    public Recipe(@NonNull String name, @NonNull String emoji, @NonNull String description,
                  @NonNull String steps) {
        this.name = name;
        this.emoji = emoji;
        this.description = description;
        this.steps = steps;
    }

    /** Splits the stored steps text into a list, one step per entry. */
    @NonNull
    public List<String> getStepList() {
        List<String> result = new ArrayList<>();
        for (String step : steps.split("\n")) {
            if (!step.trim().isEmpty()) {
                result.add(step.trim());
            }
        }
        return result;
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

    @NonNull
    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(@NonNull String emoji) {
        this.emoji = emoji;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getSteps() {
        return steps;
    }

    public void setSteps(@NonNull String steps) {
        this.steps = steps;
    }
}

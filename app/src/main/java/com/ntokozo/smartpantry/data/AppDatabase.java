package com.ntokozo.smartpantry.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The app's local SQLite database, managed by Room.
 * A single shared instance is used across the whole app (singleton pattern).
 */
@Database(
        entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "smart_pantry.db";

    /**
     * Room does not allow queries on the main (UI) thread, so all database work
     * runs on this single background thread. One thread keeps operations in order.
     */
    public static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private static volatile AppDatabase instance;

    public abstract PantryItemDao pantryItemDao();

    public abstract RecipeDao recipeDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME)
                            .addCallback(seedCallback)
                            .build();
                }
            }
        }
        return instance;
    }

    /** Runs once, the very first time the database file is created: loads the recipe book. */
    private static final RoomDatabase.Callback seedCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecutor.execute(() -> RecipeSeeder.seedIfEmpty(instance.recipeDao()));
        }
    };
}

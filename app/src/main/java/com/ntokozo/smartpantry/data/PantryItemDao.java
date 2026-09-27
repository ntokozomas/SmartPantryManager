package com.ntokozo.smartpantry.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * CRUD queries for the pantry_items table.
 * Room generates the implementation at compile time.
 */
@Dao
public interface PantryItemDao {

    /** Create */
    @Insert
    long insert(PantryItem item);

    /** Read (all) */
    @Query("SELECT * FROM pantry_items ORDER BY name COLLATE NOCASE")
    List<PantryItem> getAll();

    /** Read (one) */
    @Query("SELECT * FROM pantry_items WHERE id = :id")
    PantryItem getById(long id);

    /** Update */
    @Update
    int update(PantryItem item);

    /** Delete (one) */
    @Query("DELETE FROM pantry_items WHERE id = :id")
    int deleteById(long id);

    /** Delete (all) - used by the "Clear pantry" setting. */
    @Query("DELETE FROM pantry_items")
    void deleteAll();
}

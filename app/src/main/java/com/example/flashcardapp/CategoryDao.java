package com.example.flashcardapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Delete
    void delete(Category category);

    @Update(onConflict = REPLACE)
    void update(Category category);

    @Query("SELECT * FROM category_table WHERE category_name = :cName AND deck_name = :dName")
    LiveData<List<Category>> getCategoryByName(String cName, String dName);

    @Query("SELECT * FROM category_table WHERE deck_name = :dName")
    LiveData<List<Category>> getAllCategoriesFromDeck(String dName);

    @Query("DELETE FROM category_table WHERE deck_name = :dName")
    void deleteAllCategoriesInDeck(String dName);
}

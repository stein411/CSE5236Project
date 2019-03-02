package com.example.flashcardapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category, Deck deckName);

    @Delete
    void delete(Category category, Deck deckName);

    @Query("SELECT * FROM Category WHERE deckName = :deckName")
    List<Category> getAllCategoriesForDeck(String deckName);
}

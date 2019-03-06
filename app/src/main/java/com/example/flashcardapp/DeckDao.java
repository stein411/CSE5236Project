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
public interface DeckDao {
    @Insert
    void insert(Deck deck);

    @Query("DELETE FROM deck_table")
    void deleteAll();

    @Query("SELECT * FROM deck_table ORDER BY name")
    LiveData<List<Deck>> getAllDecks();

    @Query("SELECT * FROM deck_table WHERE name = :dName")
    LiveData<List<Deck>> getDeckWithGivenName(String dName);

    @Update(onConflict = REPLACE)
    void update(Deck deck);

    @Delete
    void delete(Deck deck);
}

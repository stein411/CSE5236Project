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
public interface FlashcardDao {
    @Insert
    void insert(Flashcard flashcard);

    @Delete
    void delete(Flashcard flashcard);

    @Update(onConflict = REPLACE)
    void update(Flashcard flashcard);

    @Query("SELECT * FROM flashcard_table WHERE term = :term AND deck_name = :dName")
    LiveData<List<Flashcard>> getFlashcardByTerm(String term, String dName);

    @Query("SELECT * FROM flashcard_table WHERE deck_name = :dName")
    LiveData<List<Flashcard>> getAllFlashcardsFromDeck(String dName);
}

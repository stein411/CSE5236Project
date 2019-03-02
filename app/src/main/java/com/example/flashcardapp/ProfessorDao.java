package com.example.flashcardapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ProfessorDao {
    @Insert
    void insert(Professor professor, Deck deck);

    @Delete
    void delete(Professor professor, Deck deck);

    @Query("SELECT * FROM Professor WHERE deckName = :deckName")
    List<Professor> getAllProfessorsForDeck(String deckName);
}

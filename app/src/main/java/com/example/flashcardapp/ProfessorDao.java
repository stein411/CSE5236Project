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
public interface ProfessorDao {
    @Insert
    void insert(Professor professor);

    @Delete
    void delete(Professor professor);

    @Update(onConflict = REPLACE)
    void update(Professor professor);

    @Query("SELECT * FROM professor_table WHERE professor_name = :pName AND deck_name = :dName")
    LiveData<List<Professor>> getProfessorByName(String pName, String dName);

    @Query("SELECT * FROM professor_table WHERE deck_name = :dName")
    LiveData<List<Professor>> getAllProfessorsFromDeck(String dName);

    @Query("DELETE FROM professor_table WHERE deck_name = :dName")
    void deleteAllProfessorsInDeck(String dName);
}

package com.example.flashcardapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.flashcardapp.RoomDatabase.Professor;
import com.example.flashcardapp.RoomDatabase.ProfessorRepository;

import java.util.List;

/**
 * ProfessorViewModel.java.
 * ViewModel class used for interacting with the professor entity of the Room Database.
 */
public class ProfessorViewModel extends AndroidViewModel {
    /**
     * Professor repository which interacts with the professor entity of the Room Database.
     */
    private ProfessorRepository mProfessorRepository;

    /**
     * Constructor method.
     * @param application
     */
    public ProfessorViewModel(Application application) {
        super(application);
        mProfessorRepository = new ProfessorRepository(application);
    }

    /**
     * Delete professor from the Room Database.
     * @param professor
     *          professor object to delete
     */
    public void delete(Professor professor) {
        mProfessorRepository.delete(professor);
    }

    /**
     * Update professor in the Room Database.
     * @param professor
     *          professor object to update
     */
    public void update(Professor professor) {
        mProfessorRepository.update(professor);
    }

    /**
     * Insert professor into the Room Database.
     * @param professor
     *         professor object to insert
     */
    public void insert(Professor professor) {
        mProfessorRepository.insert(professor);
    }

    /**
     * Obtain a list of the professors in the database by professor and deck name.
     * @param pName
     *          professor name
     * @param dName
     *          deck name
     * @return list of professors matching pName and dName
     */
    LiveData<List<Professor>> getProfessorByName(String pName, String dName) {
        return mProfessorRepository.getProfessorByName(pName, dName);
    }

    /**
     * Obtain a list of professors in the database with the given deck name.
     * @param dName
     *          deck name
     * @return list of professors matching dName
     */
    LiveData<List<Professor>> getAllProfessorsFromDeck(String dName) {
        return mProfessorRepository.getAllProfessorsFromDeck(dName);
    }


    /**
     * Delete all professors from the given deck.
     * @param dName
     *          deck name
     */
    public void deleteAllProfessorsInDeck(String dName) {
        mProfessorRepository.deleteAllProfessorsInDeck(dName);
    }
}

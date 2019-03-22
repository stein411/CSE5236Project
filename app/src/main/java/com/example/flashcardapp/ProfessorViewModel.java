package com.example.flashcardapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.flashcardapp.RoomDatabase.Professor;
import com.example.flashcardapp.RoomDatabase.ProfessorRepository;

import java.util.List;

public class ProfessorViewModel extends AndroidViewModel {
    private ProfessorRepository mProfessorRepository;

    public ProfessorViewModel(Application application) {
        super(application);
        mProfessorRepository = new ProfessorRepository(application);
    }

    public void delete(Professor professor) {
        mProfessorRepository.delete(professor);
    }

    public void update(Professor professor) {
        mProfessorRepository.update(professor);
    }

    public void insert(Professor professor) {
        mProfessorRepository.insert(professor);
    }

    LiveData<List<Professor>> getProfessorByName(String pName, String dName) {
        return mProfessorRepository.getProfessorByName(pName, dName);
    }

    LiveData<List<Professor>> getAllProfessorsFromDeck(String dName) {
        return mProfessorRepository.getAllProfessorsFromDeck(dName);
    }

    public void deleteAllProfessorsInDeck(String dName) {
        mProfessorRepository.deleteAllProfessorsInDeck(dName);
    }
}

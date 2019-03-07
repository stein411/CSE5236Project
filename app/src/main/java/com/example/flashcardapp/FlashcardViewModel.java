package com.example.flashcardapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class FlashcardViewModel extends AndroidViewModel {
    private FlashcardRepository mFlashcardRepository;

    public FlashcardViewModel(Application application) {
        super(application);
        mFlashcardRepository = new FlashcardRepository(application);
    }

    public void delete(Flashcard flashcard) {
        mFlashcardRepository.delete(flashcard);
    }

    public void update(Flashcard flashcard) {
        mFlashcardRepository.update(flashcard);
    }

    public void insert(Flashcard flashcard) {
        mFlashcardRepository.insert(flashcard);
    }

    LiveData<List<Flashcard>> getFlashcardByName(String term, String dName) {
        return mFlashcardRepository.getFlashcardByTerm(term, dName);
    }

    LiveData<List<Flashcard>> getAllFlashcardsFromDeck(String dName) {
        return mFlashcardRepository.getAllFlashcardsFromDeck(dName);
    }
}

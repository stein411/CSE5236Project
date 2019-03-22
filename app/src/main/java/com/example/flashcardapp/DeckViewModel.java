package com.example.flashcardapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.flashcardapp.RoomDatabase.Deck;
import com.example.flashcardapp.RoomDatabase.DeckRepository;

import java.util.List;

public class DeckViewModel extends AndroidViewModel {
    private DeckRepository mRepository;

    private LiveData<List<Deck>> mAllDecks;

    public DeckViewModel(Application application) {
        super(application);
        mRepository = new DeckRepository(application);
        mAllDecks = mRepository.getAllDecks();
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    LiveData<List<Deck>> getAllDecks() {
        return mAllDecks;
    }

    LiveData<List<Deck>> getSelectDecks(String name) {
        return mRepository.getDeckWithGivenName(name);
    }

    public void insert(Deck deck) {
        mRepository.insert(deck);
    }

    public void update(Deck deck, Deck oldDeck) {
        mRepository.update(deck, oldDeck);
    }

    public void delete(Deck deck) {
        mRepository.delete(deck);
    }
}

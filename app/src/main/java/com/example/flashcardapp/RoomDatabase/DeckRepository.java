package com.example.flashcardapp.RoomDatabase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class DeckRepository {
    private DeckDao mDeckDao;
    private LiveData<List<Deck>> mAllDecks;


    public DeckRepository(Application application) {
        DeckRoomDatabase db = DeckRoomDatabase.getDatabase(application);
        mDeckDao = db.deckDao();
        mAllDecks = mDeckDao.getAllDecks();
    }

    public LiveData<List<Deck>> getAllDecks() {
        return mAllDecks;
    }

    public LiveData<List<Deck>> getDeckWithGivenName(String dName) {
        return mDeckDao.getDeckWithGivenName(dName);
    }

    public LiveData<List<Deck>> getDecksByOwnerEmail(String email) {
        return mDeckDao.getAllDecksByOwnerEmail(email);
    }

    public void insert (Deck deck) {
        new insertAsyncTask(mDeckDao).execute(deck);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(mDeckDao);
    }

    public void update(Deck deck, Deck oldDeck) {
        new updateAsyncTask(mDeckDao).execute(deck, oldDeck);
    }

    public void delete(Deck deck) {
        new deleteAsyncTask(mDeckDao).execute(deck);
    }

    private static class deleteAsyncTask extends AsyncTask<Deck, Void, Void> {
        private DeckDao mAsyncTaskDao;

        deleteAsyncTask(DeckDao asyncTaskDao) {
            mAsyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(Deck... decks) {
            mAsyncTaskDao.delete(decks[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Deck, Void, Void> {
        private DeckDao mAsyncTaskDao;

        updateAsyncTask(DeckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Deck... decks) {
            Deck newDeck = decks[0];
            Deck oldDeck = decks[1];
            mAsyncTaskDao.update(newDeck.getName(), newDeck.getCourse(), newDeck.getSchool(), oldDeck.getName());
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Deck, Void, Void> {
        private DeckDao mAsyncTaskDao;

        deleteAllAsyncTask(DeckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Deck... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Deck, Void, Void> {

        private DeckDao mAsyncTaskDao;

        insertAsyncTask(DeckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Deck... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class getAllDecksAsyncTask extends AsyncTask<Deck, Void, Void> {
        private DeckDao mAsyncTaskDao;

        getAllDecksAsyncTask(DeckDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Deck... params) {
            mAsyncTaskDao.getAllDecks();
            return null;
        }
    }
}

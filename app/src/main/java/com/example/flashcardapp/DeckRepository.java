package com.example.flashcardapp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class DeckRepository {
    private DeckDao mDeckDao;
    private LiveData<List<Deck>> mAllDecks;


    DeckRepository(Application application) {
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

    public void insert (Deck deck) {
        new insertAsyncTask(mDeckDao).execute(deck);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(mDeckDao);
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

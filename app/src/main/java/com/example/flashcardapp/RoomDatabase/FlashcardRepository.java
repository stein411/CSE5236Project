package com.example.flashcardapp.RoomDatabase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class FlashcardRepository {
    private FlashcardDao mFlashcardDao;

    public FlashcardRepository(Application application) {
        DeckRoomDatabase db = DeckRoomDatabase.getDatabase(application);
        mFlashcardDao = db.flashcardDao();
    }

    // SELECT QUERIES
    public LiveData<List<Flashcard>> getFlashcardByTerm(String term, String dName) {
        return mFlashcardDao.getFlashcardByTerm(term, dName);
    }

    public LiveData<List<Flashcard>> getAllFlashcardsFromDeck(String dName) {
        return mFlashcardDao.getAllFlashcardsFromDeck(dName);
    }

    // CRUD OPERATIONS
    public void insert(Flashcard flashcard) {
        new FlashcardRepository.insertAsyncTask(mFlashcardDao).execute(flashcard);
    }

    private class insertAsyncTask extends AsyncTask<Flashcard, Void, Void> {
        private FlashcardDao mAsyncTaskDao;

        public insertAsyncTask(FlashcardDao asyncTaskDao) {
            mAsyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(Flashcard... flashcards) {
            mAsyncTaskDao.insert(flashcards[0]);
            return null;
        }
    }

    public void delete(Flashcard flashcard) {
        new FlashcardRepository.deleteAsyncTask(mFlashcardDao).execute(flashcard);
    }

    private class deleteAsyncTask extends AsyncTask<Flashcard, Void, Void> {
        private FlashcardDao mFlashcardDao;

        public deleteAsyncTask(FlashcardDao flashcardDao) {
            mFlashcardDao = flashcardDao;
        }

        @Override
        protected Void doInBackground(Flashcard... flashcards) {
            mFlashcardDao.delete(flashcards[0]);
            return null;
        }
    }

    public void deleteAllFlashcardsInDeck(String dName) {
        new FlashcardRepository.deleteAllFlashcardsInDeckAsyncTask(mFlashcardDao).execute(dName);
    }

    private class deleteAllFlashcardsInDeckAsyncTask extends AsyncTask<String, Void, Void> {
        private FlashcardDao mFlashcardDao;

        public deleteAllFlashcardsInDeckAsyncTask(FlashcardDao flashcardDao) {
            mFlashcardDao = flashcardDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mFlashcardDao.deleteAllFlashcardsInDeck(strings[0]);
            return null;
        }
    }

    public void update(Flashcard flashcard) {
        new FlashcardRepository.updateAsyncTask(mFlashcardDao).execute(flashcard);
    }

    private class updateAsyncTask extends AsyncTask<Flashcard, Void, Void> {
        private FlashcardDao mFlashcardDao;

        public updateAsyncTask(FlashcardDao flashcardDao) {
            mFlashcardDao = flashcardDao;
        }

        @Override
        protected Void doInBackground(Flashcard... flashcards) {
            mFlashcardDao.update(flashcards[0]);
            return null;
        }
    }
}

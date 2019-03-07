package com.example.flashcardapp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ProfessorRepository {
    private ProfessorDao mProfessorDao;

    ProfessorRepository(Application application) {
        DeckRoomDatabase db = DeckRoomDatabase.getDatabase(application);
        mProfessorDao = db.professorDao();
    }

    // SELECT QUERIES---------------------------------------------------------
    public LiveData<List<Professor>> getProfessorByName(String pName, String dName) {
        return mProfessorDao.getProfessorByName(pName, dName);
    }

    public LiveData<List<Professor>> getAllProfessorsFromDeck(String dName) {
        return mProfessorDao.getAllProfessorsFromDeck(dName);
    }


    // CRUD OPERATIONS--------------------------------------------------------
    public void insert(Professor professor) {
        new insertAsyncTask(mProfessorDao).execute(professor);
    }

    private class insertAsyncTask extends AsyncTask<Professor, Void, Void> {
        private ProfessorDao mAsyncTaskDao;

        public insertAsyncTask(ProfessorDao asyncTaskDao) {
            mAsyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(Professor... professors) {
            mAsyncTaskDao.insert(professors[0]);
            return null;
        }
    }

    public void delete(Professor professor) {
        new deleteAsyncTask(mProfessorDao).execute(professor);
    }

    private class deleteAsyncTask extends AsyncTask<Professor, Void, Void> {
        private ProfessorDao mProfessorDao;

        public deleteAsyncTask(ProfessorDao professorDao) {
            mProfessorDao = professorDao;
        }

        @Override
        protected Void doInBackground(Professor... professors) {
            mProfessorDao.delete(professors[0]);
            return null;
        }
    }

    public void update(Professor professor) {
        new updateAsyncTask(mProfessorDao).execute(professor);
    }

    private class updateAsyncTask extends AsyncTask<Professor, Void, Void> {
        private ProfessorDao mProfessorDao;

        public updateAsyncTask(ProfessorDao professorDao) {
            mProfessorDao = professorDao;
        }

        @Override
        protected Void doInBackground(Professor... professors) {
            mProfessorDao.update(professors[0]);
            return null;
        }
    }
}

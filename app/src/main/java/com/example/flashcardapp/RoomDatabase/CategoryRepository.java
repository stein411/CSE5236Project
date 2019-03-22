package com.example.flashcardapp.RoomDatabase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class CategoryRepository {
    private CategoryDao mCategoryDao;

    public CategoryRepository(Application application) {
        DeckRoomDatabase db = DeckRoomDatabase.getDatabase(application);
        mCategoryDao = db.categoryDao();
    }

    // SELECT QUERIES---------------------------------------------------------
    public LiveData<List<Category>> getCategoryByName(String cName, String dName) {
        return mCategoryDao.getCategoryByName(cName, dName);
    }

    public LiveData<List<Category>> getAllCategoriesFromDeck(String dName) {
        return mCategoryDao.getAllCategoriesFromDeck(dName);
    }


    // CRUD OPERATIONS--------------------------------------------------------
    public void insert(Category category) {
        new insertAsyncTask(mCategoryDao).execute(category);
    }

    private class insertAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao mAsyncTaskDao;

        public insertAsyncTask(CategoryDao asyncTaskDao) {
            mAsyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            mAsyncTaskDao.insert(categories[0]);
            return null;
        }
    }

    public void delete(Category category) {
        new deleteAsyncTask(mCategoryDao).execute(category);
    }

    private class deleteAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao mCategoryDao;

        public deleteAsyncTask(CategoryDao categoryDao) {
            mCategoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            mCategoryDao.delete(categories[0]);
            return null;
        }
    }

    public void update(Category category) {
        new updateAsyncTask(mCategoryDao).execute(category);
    }

    private class updateAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao mCategoryDao;

        public updateAsyncTask(CategoryDao categoryDao) {
            mCategoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            mCategoryDao.update(categories[0]);
            return null;
        }
    }

    public void deleteAllCategoriesInDeck(String dName) {
        new deleteAllCategoriesInDeckAsyncTask(mCategoryDao).execute(dName);
    }

    private class deleteAllCategoriesInDeckAsyncTask extends AsyncTask<String, Void, Void> {
        private CategoryDao mCategoryDao;

        public deleteAllCategoriesInDeckAsyncTask(CategoryDao categoryDao) {
            mCategoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mCategoryDao.deleteAllCategoriesInDeck(strings[0]);
            return null;
        }
    }
}

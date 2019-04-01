package com.example.flashcardapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.flashcardapp.RoomDatabase.Category;
import com.example.flashcardapp.RoomDatabase.CategoryRepository;

import java.util.List;

/**
 * CategoryViewModel.java.
 * ViewModel class used for interacting with the category entity of the Room Database.
 */
public class CategoryViewModel extends AndroidViewModel {
    /**
     * category repository which interacts with the category entity of the Room Database.
     */
    private CategoryRepository mCategoryRepository;

    /**
     * Constructor method.
     * @param application
     */
    public CategoryViewModel(Application application) {
        super(application);
        mCategoryRepository = new CategoryRepository(application);
    }

    /**
     * Delete category from the Room Database.
     * @param category
     *          category object to delete
     */
    public void delete(Category category) {
        mCategoryRepository.delete(category);
    }

    /**
     * Update category in the Room Database.
     * @param category
     *          category object to update
     */
    public void update(Category category) {
        mCategoryRepository.update(category);
    }

    /**
     * Insert category into the Room Database.
     * @param category
     *         category object to insert
     */
    public void insert(Category category) {
        mCategoryRepository.insert(category);
    }

    /**
     * Obtain a list of the categories in the database by category and deck name.
     * @param cName
     *          category name
     * @param dName
     *          deck name
     * @return list of categories matching pName and dName
     */
    LiveData<List<Category>> getCategoryByName(String cName, String dName) {
        return mCategoryRepository.getCategoryByName(cName, dName);
    }

    /**
     * Obtain a list of categories in the database with the given deck name.
     * @param dName
     *          deck name
     * @return list of categories matching dName
     */
    LiveData<List<Category>> getAllCategoriesFromDeck(String dName) {
        return mCategoryRepository.getAllCategoriesFromDeck(dName);
    }

    /**
     * Delete all categories from the given deck.
     * @param dName
     *          deck name
     */
    public void deleteAllCategoriesInDeck(String dName) {
        mCategoryRepository.deleteAllCategoriesInDeck(dName);
    }
}

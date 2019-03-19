package com.example.flashcardapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository mCategoryRepository;

    public CategoryViewModel(Application application) {
        super(application);
        mCategoryRepository = new CategoryRepository(application);
    }

    public void delete(Category category) {
        mCategoryRepository.delete(category);
    }

    public void update(Category category) {
        mCategoryRepository.update(category);
    }

    public void insert(Category category) {
        mCategoryRepository.insert(category);
    }

    LiveData<List<Category>> getCategoryByName(String cName, String dName) {
        return mCategoryRepository.getCategoryByName(cName, dName);
    }

    LiveData<List<Category>> getAllCategoriesFromDeck(String dName) {
        return mCategoryRepository.getAllCategoriesFromDeck(dName);
    }

    public void deleteAllCategoriesInDeck(String dName) {
        mCategoryRepository.deleteAllCategoriesInDeck(dName);
    }
}

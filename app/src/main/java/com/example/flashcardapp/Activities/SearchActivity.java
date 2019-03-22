package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.SearchFragment;

public class SearchActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SearchFragment();
    }
}

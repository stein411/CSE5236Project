package com.example.flashcardapp;

import android.support.v4.app.Fragment;

public class SearchDecksActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SearchDecksFragment();
    }
}

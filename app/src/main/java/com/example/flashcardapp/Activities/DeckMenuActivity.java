package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.DeckMenuFragment;

public class DeckMenuActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DeckMenuFragment();
    }
}

package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.DeckHomeFragment;

public class DeckHomeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DeckHomeFragment();
    }
}

package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.FlashcardsFragment;

public class FlashcardsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new FlashcardsFragment();
    }
}

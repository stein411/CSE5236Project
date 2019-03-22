package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.UneditableDeckFragment;

public class UneditableDeckActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new UneditableDeckFragment();
    }
}

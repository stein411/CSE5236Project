package com.example.flashcardapp;

import android.support.v4.app.Fragment;

public class UserHomeActivity extends SingleFragmentActivity {
    @Override
    protected android.support.v4.app.Fragment createFragment() {
        return new UserHomeFragment();
    }
}

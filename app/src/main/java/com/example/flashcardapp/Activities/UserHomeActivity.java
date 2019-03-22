package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.UserHomeFragment;

public class UserHomeActivity extends SingleFragmentActivity {
    @Override
    protected android.support.v4.app.Fragment createFragment() {
        return new UserHomeFragment();
    }
}

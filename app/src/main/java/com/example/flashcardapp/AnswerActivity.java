package com.example.flashcardapp;

import android.support.v4.app.Fragment;

public class AnswerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AnswerFragment();
    }
}

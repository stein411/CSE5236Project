package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.AnswerFragment;

public class AnswerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AnswerFragment();
    }
}

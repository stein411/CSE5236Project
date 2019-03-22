package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.StudyDeckFragment;

public class StudyDeckActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new StudyDeckFragment();
    }
}

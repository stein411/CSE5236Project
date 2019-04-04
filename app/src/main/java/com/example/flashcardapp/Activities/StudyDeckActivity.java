package com.example.flashcardapp.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.example.flashcardapp.R;
import com.example.flashcardapp.StudyDeckFragment;

public class StudyDeckActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new StudyDeckFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Study");
        setSupportActionBar(toolbar);
    }
}

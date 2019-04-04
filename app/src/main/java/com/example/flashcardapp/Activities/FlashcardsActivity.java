package com.example.flashcardapp.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.example.flashcardapp.FlashcardsFragment;
import com.example.flashcardapp.R;

public class FlashcardsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new FlashcardsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Study");
        setSupportActionBar(toolbar);
    }
}

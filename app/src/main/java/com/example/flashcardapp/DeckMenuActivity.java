package com.example.flashcardapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

public class DeckMenuActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DeckMenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Your Decks");
        setSupportActionBar(myToolbar);
    }
}

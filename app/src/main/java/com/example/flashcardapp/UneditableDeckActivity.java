package com.example.flashcardapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

public class UneditableDeckActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new UneditableDeckFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Deck 1");
        setSupportActionBar(myToolbar);
    }
}

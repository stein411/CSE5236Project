package com.example.flashcardapp;

import android.arch.lifecycle.SingleGeneratedAdapterObserver;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class DeckHomeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DeckHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        //TODO reference deck title, "New Deck" as default
        toolbar.setTitle("New Deck");
        setSupportActionBar(toolbar);
    }
}

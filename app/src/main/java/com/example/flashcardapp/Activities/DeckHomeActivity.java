package com.example.flashcardapp.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.flashcardapp.DeckHomeFragment;
import com.example.flashcardapp.R;

public class DeckHomeActivity extends SingleFragmentActivity {

    private Toolbar mToolbar;

    @Override
    protected Fragment createFragment() {
        return new DeckHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
    }

    protected void setupToolbar(){
        mToolbar = findViewById(R.id.my_toolbar);
        //TODO reference deck title, "New Deck" as default
        //TODO recreate functionality of back button within fragment
        //TODO the above will probably require handling the toolbar within the fragment which is a giant pain.
        mToolbar.setTitle("New Deck");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Owen's still working on this one...", Toast.LENGTH_LONG).show();
            }
        });
    }
}

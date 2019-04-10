package com.example.flashcardapp.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.flashcardapp.R;
import com.example.flashcardapp.UneditableDeckFragment;

public class UneditableDeckActivity extends SingleFragmentActivity {
    private Toolbar mToolbar;

    @Override
    protected Fragment createFragment() {
        return new UneditableDeckFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
    }

    protected void setupToolbar(){
        mToolbar = findViewById(R.id.my_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setTitle("Flashcards");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

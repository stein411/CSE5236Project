package com.example.flashcardapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.flashcardapp.DeckHomeFragment;
import com.example.flashcardapp.R;

public class DeckHomeActivity extends SingleFragmentActivity {

    private Toolbar mToolbar;
    private Intent mIntent;

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
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent();
                mIntent.putExtra(getString(R.string.completed_deck_key), false);
                setResult(Activity.RESULT_OK, mIntent);
                finish();
            }
        });
    }
}

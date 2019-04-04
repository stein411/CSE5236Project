package com.example.flashcardapp.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.flashcardapp.DeckEditFragment;
import com.example.flashcardapp.R;

public class DeckEditActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new DeckEditFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Edit Deck Info");
        setSupportActionBar(toolbar);
    }


    /*
     * Overriding lifestyle methods for logging.
     */
    private static final String TAG = "DeckEditActivity";

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}

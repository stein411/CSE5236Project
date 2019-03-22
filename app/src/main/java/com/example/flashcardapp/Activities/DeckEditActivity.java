package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.flashcardapp.DeckEditFragment;

public class DeckEditActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new DeckEditFragment();
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
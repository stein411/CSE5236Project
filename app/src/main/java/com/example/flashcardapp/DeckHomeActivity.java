package com.example.flashcardapp;

import android.arch.lifecycle.SingleGeneratedAdapterObserver;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DeckHomeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DeckHomeFragment();
    }
}

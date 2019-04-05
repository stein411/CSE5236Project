package com.example.flashcardapp.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.flashcardapp.DeckMenuFragment;
import com.example.flashcardapp.MapsFragment;
import com.example.flashcardapp.R;
import com.example.flashcardapp.SearchFragment;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class BottomNavActivity extends AppCompatActivity {
    private FragmentManager fm;
    private Fragment active;
    private Toolbar mToolbar;
    private final Fragment fragment1 = new DeckMenuFragment();
    private final Fragment fragment2 = new MapsFragment(); // TODO convert maps to fragment
    private final Fragment fragment3 = new SearchFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    mToolbar.setTitle("Your Decks");
                    return true;

                case R.id.navigation_dashboard:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    mToolbar.setTitle("Nearby Decks");
                    return true;

                case R.id.navigation_notifications:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    mToolbar.setTitle("Search");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        active = fragment1;

        // Fragment manager
        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();

        setupToolbar();
    }


    protected void setupToolbar(){
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle("Your Decks");
        }
        setSupportActionBar(mToolbar);
    }
}

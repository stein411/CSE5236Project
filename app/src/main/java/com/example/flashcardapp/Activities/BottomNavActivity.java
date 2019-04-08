package com.example.flashcardapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.flashcardapp.DeckMenuFragment;
import com.example.flashcardapp.MapsFragment;
import com.example.flashcardapp.R;
import com.example.flashcardapp.SearchFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class BottomNavActivity extends AppCompatActivity {
    private FragmentManager fm;
    private Fragment active;
    private Toolbar mToolbar;
    private final Fragment fragment1 = new DeckMenuFragment();
    private final Fragment fragment2 = new MapsFragment();
    private final Fragment fragment3 = new SearchFragment();
    private int activeNo;
    private String activeNoKey;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    activeNo = 1;
                    mToolbar.setTitle(getString(R.string.MyDecksString));
                    return true;

                case R.id.navigation_maps:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    activeNo = 2;
                    mToolbar.setTitle(getString(R.string.NearbyDecks));
                    return true;

                case R.id.navigation_search:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    activeNo = 3;
                    mToolbar.setTitle(R.string.SearchDecksString);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        activeNoKey = getString(R.string.active_number_key);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm = getSupportFragmentManager();
        setupToolbar();

        if (savedInstanceState != null && savedInstanceState.get(activeNoKey) != null) {
            activeNo = savedInstanceState.getInt(activeNoKey);

            if (activeNo == 1) {
                active = fragment1;
            } else if (activeNo == 2) {
                active = fragment2;
            } else if (activeNo == 3) {
                active = fragment3;
            }

            fm.beginTransaction().add(R.id.main_container, fragment1, "1").hide(fragment1).commit();
            fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
            fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();

            if (activeNo == 1) {
                ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_home);
            } else if (activeNo == 2) {
                ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_maps);
            } else if (activeNo == 3) {
                ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_search);
            }
        } else {
            active = fragment1;
            activeNo = 1;

            fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
            fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
            fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();
        }
    }

    protected void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle("Your Decks");
        }
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(getApplicationContext(), MainEmptyActivity.class));
                    finish();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (activeNo == 1) {
            ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_search);
        } else if (activeNo == 2) {
            ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_home);
        } else if (activeNo == 3) {
            ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_maps);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (active != null) {
            fm.beginTransaction().show(active).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        fm.beginTransaction().hide(active).commit();
        outState.putInt(activeNoKey, activeNo);
        super.onSaveInstanceState(outState);
    }
}

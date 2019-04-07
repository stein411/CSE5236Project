package com.example.flashcardapp;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flashcardapp.Activities.DeckHomeActivity;
import com.example.flashcardapp.Activities.UneditableDeckActivity;
import com.example.flashcardapp.RoomDatabase.Deck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

public class DeckMenuFragment extends Fragment {
    private static final String TAG = "DeckMenuFragment";
    private Button addDeckButton;
    private Intent resultOfNewDeck;
    private String completedDeckKey;
    private String deckNameKey;
    private LinearLayout decksContainer;
    private Intent resultOfExistingDeck;
    private String isNewDeckKey;
    private DeckViewModel mDeckViewModel;
    private List<Deck> mAllDecks;
    private boolean mJustChanged;
    private TextView usernameLabel;
    private String email;
    private FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);

        mJustChanged = false;

        // TODO modify here with new query that selects by email
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = "guest";
        if (user != null && user.getEmail() != null && user.getEmail().length() != 0) {
            email = user.getEmail();
        }

        mDeckViewModel = ViewModelProviders.of(this).get(DeckViewModel.class);
        mDeckViewModel.getDecksByOwnerEmail(email).observe(this, new Observer<List<Deck>>() {
            @Override
            public void onChanged(@Nullable final List<Deck> decks) {
                mAllDecks = decks;
                if (!mJustChanged) {
                    populateDecks();
                } else {
                    mJustChanged = false;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View v = inflater.inflate(R.layout.fragment_deck_menu, container, false);
        completedDeckKey = getString(R.string.completed_deck_key);
        deckNameKey = getString(R.string.deck_name_key);
        isNewDeckKey = getString(R.string.is_new_deck_key);
        decksContainer = (LinearLayout) v.findViewById(R.id.decks_container);

        if (user != null && user.getDisplayName() != null) {
            usernameLabel = (TextView) v.findViewById(R.id.username_label);
            String name = user.getDisplayName();
            if (name.length() == 0) {
                name = "Guest";
            }
            usernameLabel.setText(name + "\'s Decks");
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated() called");
        addDeckButton = (Button) getView().findViewById(R.id.add_deck_button);
        addDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    resultOfNewDeck = new Intent(getContext(), DeckHomeActivity.class);
                    resultOfNewDeck.putExtra(isNewDeckKey, true);
                    startActivityForResult(resultOfNewDeck, 0);
                }
            }
        });
    }

    private void populateDecks() {
        Log.d(TAG, "populateDecks() called");
        if (mAllDecks != null) {
            for (Deck deck : mAllDecks) {
                Button launchDeckButton = new Button(getContext());
                final String dName = deck.getName();
                launchDeckButton.setText(dName);
                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(toDp(200), ViewGroup.LayoutParams.WRAP_CONTENT);
                launchDeckButton.setLayoutParams(layoutParams);
                launchDeckButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resultOfExistingDeck = new Intent(getContext(), DeckHomeActivity.class);
                        resultOfExistingDeck.putExtra(isNewDeckKey, false);
                        resultOfExistingDeck.putExtra(deckNameKey, dName);
                        startActivityForResult(resultOfExistingDeck, 1);
                    }
                });
                decksContainer.addView(launchDeckButton);
            }
        }
        mJustChanged = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Bundle extras = data.getExtras();
        if (requestCode == 0 && extras.getBoolean(completedDeckKey)) {
            Button newDeck = new Button(getContext());
            String dName = extras.getString(deckNameKey);
            newDeck.setText(dName);
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(toDp(200), ViewGroup.LayoutParams.WRAP_CONTENT);
            newDeck.setLayoutParams(layoutParams);
            newDeck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resultOfExistingDeck = new Intent(getContext(), DeckHomeActivity.class);
                    resultOfExistingDeck.putExtra(isNewDeckKey, false);
                    startActivityForResult(resultOfExistingDeck, 1);
                }
            });
            decksContainer.addView(newDeck);
        }

        /*
         * Temporary workaround to allow for view & database to update.
         * By doing this, the TextViews will be populated when we start the DeckHomeActivity.
         */
        if (getActivity() != null) {
            Intent i = getActivity().getIntent();
            getActivity().finish();
            startActivity(i);
        }
//        getFragmentManager()
//                .beginTransaction()
//                .detach(DeckMenuFragment.this)
//                .attach(DeckMenuFragment.this)
//                .commit();
    }

    /**
     * Converts the given value to DP units.
     *
     * @param value
     *          the value to convert
     * @return the given value in dp units
     */
    public int toDp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }


    /*
     * Overriding lifestyle methods for logging.
     */

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

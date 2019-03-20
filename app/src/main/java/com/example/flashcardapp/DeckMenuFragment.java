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
import android.widget.Toast;

import java.util.List;

public class DeckMenuFragment extends Fragment {
    private Button addDeckButton;
    private Button backButton;
    private Button deck1Button;
    private Intent resultOfNewDeck;
    private String completedDeckKey;
    private String deckNameKey;
    private LinearLayout decksContainer;
    private Intent resultOfExistingDeck;
    private String isNewDeckKey;
    private DeckViewModel mDeckViewModel;
    private List<Deck> mAllDecks;
    private boolean mJustChanged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mJustChanged = false;

        mDeckViewModel = ViewModelProviders.of(this).get(DeckViewModel.class);
        mDeckViewModel.getAllDecks().observe(this, new Observer<List<Deck>>() {
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
        View v = inflater.inflate(R.layout.fragment_deck_menu, container, false);
        completedDeckKey = getString(R.string.completed_deck_key);
        deckNameKey = getString(R.string.deck_name_key);
        isNewDeckKey = getString(R.string.is_new_deck_key);

        backButton = (Button) v.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        deck1Button = (Button) v.findViewById(R.id.deck_1_button);
        deck1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().startActivity(new Intent(getContext(), UneditableDeckActivity.class));
                }
            }
        });
        decksContainer = (LinearLayout) v.findViewById(R.id.decks_container);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
}

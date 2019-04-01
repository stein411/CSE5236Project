package com.example.flashcardapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcardapp.RoomDatabase.Flashcard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * StudyFragment.java
 * Superclass for FlashcardsFragment and AnswerFragment.
 */
public abstract class StudyFragment extends Fragment {
    protected Button shuffleDeckButton;
    protected Button backButton;
    protected ImageButton forwardButton;
    protected ImageButton prevButton;
    protected ImageButton revButton;
    protected TextView answerPrompt;
    protected FlashcardViewModel mFlashcardViewModel;
    protected String deckKey;
    protected String deckName;
    protected String markedCardsKey;
    protected List<Flashcard> mFlashcards;
    protected int mCurrentIndex;
    protected boolean answerWithDef;
    protected String mAnswer;
    protected String isFirebaseDeckKey;
    protected boolean isFirebaseDeck;
    protected ArrayList<String> markedCards;



    protected void populateView(View v) {
        mCurrentIndex = 0;
        mAnswer = "";
        answerWithDef = true;
        backButton = v.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        this.forwardButton = (ImageButton) v.findViewById(R.id.next_card_button);
        this.prevButton = (ImageButton) v.findViewById(R.id.prev_card_button);
        this.revButton = (ImageButton) v.findViewById(R.id.flip_card_button);
        this.shuffleDeckButton = (Button) v.findViewById(R.id.shuffle_deck_button);
        this.answerPrompt = (TextView) v.findViewById(R.id.answer_prompt);
        this.isFirebaseDeckKey = getString(R.string.is_firebase_deck_key);
        this.isFirebaseDeck = getActivity().getIntent().getBooleanExtra(isFirebaseDeckKey, true);
        this.markedCardsKey = getString(R.string.marked_cards);

        // Get the list of flashcards from the deck name
        this.mFlashcardViewModel = ViewModelProviders.of(this).get(FlashcardViewModel.class);
        this.deckKey = getString(R.string.NameString);
        this.deckName = getActivity().getIntent().getStringExtra(deckKey);
        this.mFlashcards = new ArrayList<>();
        if (this.isFirebaseDeck) {
            final DocumentReference deckDocument = FirebaseFirestore.getInstance().collection("decks").document(deckName);
            if (deckDocument != null) {
                deckDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.get("flashcards") != null) {
                                List<Map> f = (List<Map>) documentSnapshot.get("flashcards");
                                for (Map flashcard : f) {
                                    for (Object obj : flashcard.entrySet()) {
                                        Map.Entry<String, String> entry  = (Map.Entry) obj;
                                        Flashcard flashcard1 = new Flashcard();
                                        flashcard1.setDeckName(deckName);
                                        flashcard1.setTerm(entry.getKey());
                                        flashcard1.setDefinition(entry.getValue());
                                        mFlashcards.add(flashcard1);
                                    }
                                }
                                markedCards = getActivity().getIntent().getStringArrayListExtra(markedCardsKey);
                                if (markedCards != null) {
                                    // Remove non-marked terms
                                    List<Flashcard> fl = new ArrayList<>();
                                    for (int i = 0; i < mFlashcards.size(); i++) {
                                        if (markedCards.contains(Integer.toString(i))) {
                                            fl.add(mFlashcards.get(i));
                                        }
                                    }
                                    mFlashcards = fl;
                                }
                            }

                            if (mFlashcards.size() > 0) {
                                setupUI();
                            }
                        }
                    }
                });
            }
        } else {
            this.mFlashcardViewModel.getAllFlashcardsFromDeck(this.deckName).observe(this, new Observer<List<Flashcard>>() {
                @Override
                public void onChanged(@Nullable List<Flashcard> flashcards) {
                    mFlashcards = flashcards;
                    onChangedCalled();
                }
            });
        }
    }

    protected void onChangedCalled() {
        if (this.mFlashcards.size() > 0) {
            this.markedCards = getActivity().getIntent().getStringArrayListExtra(this.markedCardsKey);
            if (this.markedCards != null) {
                // Remove non-marked terms
                List<Flashcard> fl = new ArrayList<>();
                for (int i = 0; i < mFlashcards.size(); i++) {
                    if (markedCards.contains(Integer.toString(i))) {
                        fl.add(mFlashcards.get(i));
                    }
                }
                mFlashcards = fl;
            }
            setupUI();
        } else {
            // Should be caught by study deck, but just in case
            Toast.makeText(getContext(), "Try adding some flashcards to start studying", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    /**
     * Add event listeners for the buttons and modify the text elements.
     */
    protected void setupUI() {
        this.answerPrompt.setText(this.mFlashcards.get(0).getTerm());
        this.mAnswer = this.mFlashcards.get(0).getDefinition();

        // Go to the next card or cycle back around
        this.forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mFlashcards.size();
                Flashcard f = mFlashcards.get(mCurrentIndex);
                String prompt;
                if (answerWithDef) {
                    mAnswer = f.getDefinition();
                    prompt = f.getTerm();
                } else {
                    mAnswer = f.getTerm();
                    prompt = f.getDefinition();
                }
                answerPrompt.setText(prompt);
            }
        });

        // Go to the previous card or cycle back around
        this.prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex--;
                if (mCurrentIndex < 0) {
                    mCurrentIndex = mFlashcards.size() - 1;
                }
                Flashcard f = mFlashcards.get(mCurrentIndex);
                String prompt;
                if (answerWithDef) {
                    mAnswer = f.getDefinition();
                    prompt = f.getTerm();
                } else {
                    mAnswer = f.getTerm();
                    prompt = f.getDefinition();
                }
                answerPrompt.setText(prompt);
            }
        });
    }
}

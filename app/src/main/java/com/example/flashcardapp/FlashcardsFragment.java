package com.example.flashcardapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flashcardapp.RoomDatabase.Flashcard;

import java.util.Collections;

/**
 * FlashcardsFragment.java
 * Allows the user to study the deck by viewing the flashcards and rotating them to check the term/definition mentally.
 * The user can also cycle through and shuffle the deck.
 */
public class FlashcardsFragment extends StudyFragment {
    /**
     * OnCreateView method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view with the view elements initialized with some event listeners
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_flashcards, container, false);
        super.populateView(v);
        return v;
    }

    /**
     * Add event listeners for the buttons and modify the text elements.
     */
    @Override
    protected void setupUI() {
        super.setupUI();

        // Reverse the card
        revButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flashcard f = mFlashcards.get(mCurrentIndex);
                answerWithDef = !answerWithDef;
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

        // Shuffle the deck
        shuffleDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.shuffle(mFlashcards);
                mCurrentIndex = 0;
                Flashcard f = mFlashcards.get(0);
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

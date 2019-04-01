package com.example.flashcardapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.flashcardapp.RoomDatabase.Flashcard;

import java.util.Collections;

/**
 * AnswerFragment.java
 * Allows the user to study the deck by providing term/definition answers to specific flashcards.
 * The user can also cycle through and shuffle the deck.
 */
public class AnswerFragment extends StudyFragment {
    private Button checkAnswerButton;
    private EditText enterAnswer;

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
        View v = inflater.inflate(R.layout.fragment_answer, container, false);

        // Populate the view elements common to both subclasses
        super.populateView(v);

        // Allow the user to enter/check their answers
        checkAnswerButton = (Button) v.findViewById(R.id.check_answer_button);
        enterAnswer = (EditText) v.findViewById(R.id.enter_answer);
        return v;
    }

    /**
     * Add event listeners for the buttons and modify the text elements.
     */
    protected void setupUI() {
        super.setupUI();
        enterAnswer.setHint(R.string.enter_def);

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
                    enterAnswer.setHint(R.string.enter_def);
                } else {
                    mAnswer = f.getTerm();
                    prompt = f.getDefinition();
                    enterAnswer.setHint(R.string.enter_term);
                }
                answerPrompt.setText(prompt);
            }
        });

        // Check answer
        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response = enterAnswer.getText().toString().toLowerCase();
                String toastTxt = (response.equals(mAnswer.toLowerCase())) ? "Your answer was correct! Nice job!" : "Your answer was not quite correct! You'll get 'em next time!";
                Toast.makeText(getContext(), toastTxt, Toast.LENGTH_LONG).show();
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
                    enterAnswer.setHint(R.string.enter_def);
                } else {
                    mAnswer = f.getTerm();
                    prompt = f.getDefinition();
                    enterAnswer.setHint(R.string.enter_term);
                }
                answerPrompt.setText(prompt);
            }
        });
    }
}

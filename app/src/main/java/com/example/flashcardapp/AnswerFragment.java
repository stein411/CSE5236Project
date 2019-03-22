package com.example.flashcardapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnswerFragment extends Fragment {
    private Button backButton;
    private ImageButton forwardButton;
    private ImageButton prevButton;
    private ImageButton revButton;
    private Button checkAnswerButton;
    private Button shuffleDeckButton;
    private EditText enterAnswer;
    private TextView answerPrompt;
    private FlashcardViewModel mFlashcardViewModel;
    private String deckKey;
    private String deckName;
    private List<Flashcard> mFlashcards;
    private int mCurrentIndex;
    private boolean answerWithDef;
    private String mAnswer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_answer, container, false);

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
        forwardButton = (ImageButton) v.findViewById(R.id.next_card_button);
        prevButton = (ImageButton) v.findViewById(R.id.prev_card_button);
        revButton = (ImageButton) v.findViewById(R.id.flip_card_button);
        checkAnswerButton = (Button) v.findViewById(R.id.check_answer_button);
        shuffleDeckButton = (Button) v.findViewById(R.id.shuffle_deck_button);
        enterAnswer = (EditText) v.findViewById(R.id.enter_answer);
        answerPrompt = (TextView) v.findViewById(R.id.answer_prompt);


        // Get the list of flashcards from the deck name
        mFlashcardViewModel = ViewModelProviders.of(this).get(FlashcardViewModel.class);
        deckKey = getString(R.string.NameString);
        deckName = getActivity().getIntent().getStringExtra(deckKey);
        mFlashcards = new ArrayList<>();
        mFlashcardViewModel.getAllFlashcardsFromDeck(deckName).observe(this, new Observer<List<Flashcard>>() {
            @Override
            public void onChanged(@Nullable List<Flashcard> flashcards) {
                mFlashcards = flashcards;
                onChangedCalled();
            }
        });

        return v;
    }

    private void onChangedCalled() {
        if (mFlashcards.size() > 0) {
            answerPrompt.setText(mFlashcards.get(0).getTerm());
            mAnswer = mFlashcards.get(0).getDefinition();
            enterAnswer.setHint(R.string.enter_def);

            // Go to the next card or cycle back around
            forwardButton.setOnClickListener(new View.OnClickListener() {
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
            prevButton.setOnClickListener(new View.OnClickListener() {
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
        } else {
            // Should be caught by study deck, but just in case
            Toast.makeText(getContext(), "Try adding some flashcards to start studying", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }
}

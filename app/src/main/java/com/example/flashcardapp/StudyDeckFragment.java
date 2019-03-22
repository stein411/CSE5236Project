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

import com.example.flashcardapp.Activities.AnswerActivity;
import com.example.flashcardapp.Activities.FlashcardsActivity;
import com.example.flashcardapp.RoomDatabase.Flashcard;

import java.util.List;

public class StudyDeckFragment extends Fragment {
    private Button backButton;
    private Button answerWithTermOrDefButton;
    private Button flashcardsButton;
    private String deckKey;
    private String deckName;
    private FlashcardViewModel mFlashcardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_study_deck, container, false);
        deckKey = getString(R.string.NameString);
        deckName = getActivity().getIntent().getStringExtra(deckKey);
        backButton = (Button) v.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        answerWithTermOrDefButton = (Button) v.findViewById(R.id.answer_term_def_button);
        answerWithTermOrDefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AnswerActivity.class);
                if (deckName != null) {
                    intent.putExtra(deckKey, deckName);
                }
                getActivity().startActivity(intent);
            }
        });
        flashcardsButton = (Button) v.findViewById(R.id.flashcards_button);
        flashcardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FlashcardsActivity.class);
                if (deckName != null) {
                    intent.putExtra(deckKey, deckName);
                }
                getActivity().startActivity(intent);
            }
        });

        if (deckName == null) {
            answerWithTermOrDefButton.setEnabled(false);
            flashcardsButton.setEnabled(false);
        }

        // Check if no flashcards (disable all studying activities if that's the case)
        mFlashcardViewModel = ViewModelProviders.of(this).get(FlashcardViewModel.class);
        mFlashcardViewModel.getAllFlashcardsFromDeck(deckName).observe(this, new Observer<List<Flashcard>>() {
            @Override
            public void onChanged(@Nullable List<Flashcard> flashcards) {
                if (flashcards.size() == 0) {
                    answerWithTermOrDefButton.setEnabled(false);
                    flashcardsButton.setEnabled(false);
                }
            }
        });
        return v;
    }
}

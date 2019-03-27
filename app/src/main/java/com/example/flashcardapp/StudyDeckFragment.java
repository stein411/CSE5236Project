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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudyDeckFragment extends Fragment {
    private Button backButton;
    private Button answerWithTermOrDefButton;
    private Button flashcardsButton;
    private String deckKey;
    private String deckName;
    private String isFirebaseDeckKey;
    private FlashcardViewModel mFlashcardViewModel;
    private boolean isFirebaseDeck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_study_deck, container, false);
        deckKey = getString(R.string.NameString);
        deckName = getActivity().getIntent().getStringExtra(deckKey);
        isFirebaseDeckKey = getString(R.string.is_firebase_deck_key);
        isFirebaseDeck = getActivity().getIntent().getBooleanExtra(isFirebaseDeckKey, true);
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
                intent.putExtra(isFirebaseDeckKey, isFirebaseDeck);
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
                intent.putExtra(isFirebaseDeckKey, isFirebaseDeck);
                getActivity().startActivity(intent);
            }
        });

        if (deckName == null) {
            answerWithTermOrDefButton.setEnabled(false);
            flashcardsButton.setEnabled(false);
        }

        // Check if no flashcards (disable all studying activities if that's the case)
        if (!isFirebaseDeck) {
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
        } else {
            // Get the firebase document
            final DocumentReference deckDocument = FirebaseFirestore.getInstance().collection("decks").document(deckName);
            if (deckDocument != null) {
                deckDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.get("flashcards") != null) {
                                    // Check if there are any flashcards (just in case)
                                    List<Map> f = (List<Map>) documentSnapshot.get("flashcards");
                                    if (f.size() == 0) {
                                        answerWithTermOrDefButton.setEnabled(false);
                                        flashcardsButton.setEnabled(false);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        return v;
    }
}

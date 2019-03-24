package com.example.flashcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.flashcardapp.Activities.StudyDeckActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UneditableDeckFragment extends Fragment {
    private TextView ratingsText;
    private SeekBar ratingsBar;
    private int mRating;
    private Button studyDeckButton;
    private Button backButton;
    private String deckKey;
    private String deckName;
    private TextView deckNameLabel;
    private TextView schoolNameLabel;
    private TextView courseNameLabel;
    private TextView professorNameLabel;
    private TextView categoryNameLabel;
    private ArrayList<String> profNames;
    private ArrayList<String> categoryNames;
    private int profIndex;
    private int categoryIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uneditable_deck, container, false);
        deckKey = getString(R.string.NameString);
        deckName = getActivity().getIntent().getStringExtra(deckKey);
        profNames = new ArrayList<>();
        categoryNames = new ArrayList<>();
        profIndex = 0;
        categoryIndex = 0;
        ratingsText = (TextView) v.findViewById(R.id.ratings_input);
        ratingsBar = (SeekBar) v.findViewById(R.id.ratings_bar);
        studyDeckButton = (Button) v.findViewById(R.id.study_deck_button);
        studyDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().startActivity(new Intent(getContext(), StudyDeckActivity.class));
                }
            }
        });
        backButton = (Button) v.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        // Display the correct deck name
        deckNameLabel = (TextView) v.findViewById(R.id.deck_name_label);
        schoolNameLabel = (TextView) v.findViewById(R.id.school_name_label);
        courseNameLabel = (TextView) v.findViewById(R.id.course_name_label);
        professorNameLabel = (TextView) v.findViewById(R.id.professor_name_label);
        professorNameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profNames != null && profNames.size() > 0) {
                    professorNameLabel.setText(profNames.get(profIndex));
                    profIndex++;
                    profIndex %= profNames.size();
                }
            }
        });
        categoryNameLabel = (TextView) v.findViewById(R.id.category_name_label);
        categoryNameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryNames != null && categoryNames.size() > 0) {
                    categoryNameLabel.setText(categoryNames.get(categoryIndex));
                    categoryIndex++;
                    categoryIndex %= categoryNames.size();
                }
            }
        });
        if (deckName != null) {
            setupLayout();
        }
        return v;
    }

    /**
     * Populate the layout with the information from Firebase.
     */
    private void setupLayout() {
        deckNameLabel.setText(deckName);

        // Get the firebase document
        final DocumentReference deckDocument = FirebaseFirestore.getInstance().collection("decks").document(deckName);
        if (deckDocument != null) {
            deckDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.get("school") != null) {
                                schoolNameLabel.setText(documentSnapshot.get("school").toString());
                            }
                            if (documentSnapshot.get("course") != null) {
                                courseNameLabel.setText(documentSnapshot.get("course").toString());
                            }
                            if (documentSnapshot.get("professor") != null) {
                                profNames = (ArrayList<String>) documentSnapshot.get("professor");
                            }
                            if (documentSnapshot.get("category") != null) {
                                categoryNames = (ArrayList<String>) documentSnapshot.get("category");
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ratingsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRating = progress;
                if (ratingsText != null) {
                    ratingsText.setText(Integer.toString(mRating));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}

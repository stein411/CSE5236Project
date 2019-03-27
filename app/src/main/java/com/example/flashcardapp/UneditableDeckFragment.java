package com.example.flashcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcardapp.Activities.StudyDeckActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UneditableDeckFragment extends Fragment {
    private TextView ratingsText;
    private SeekBar ratingsBar;
    private int mRating;
    private Button studyDeckButton;
    private Button backButton;
    private Button ratingsButton;
    private Button downloadDeckButton;
    private String deckKey;
    private String deckName;
    private String isFirebaseDeckKey;
    private TextView deckNameLabel;
    private TextView schoolNameLabel;
    private TextView courseNameLabel;
    private TextView professorNameLabel;
    private TextView categoryNameLabel;
    private TextView authorNameLabel;
    private ArrayList<String> profNames;
    private ArrayList<String> categoryNames;
    private List<String> terms;
    private List<String> defs;
    private int profIndex;
    private int categoryIndex;
    private int flashcardCount;
    private List<Integer> cardLayouts;
    private List<Integer> cardLabels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uneditable_deck, container, false);
        deckKey = getString(R.string.NameString);
        deckName = getActivity().getIntent().getStringExtra(deckKey);
        isFirebaseDeckKey = getString(R.string.is_firebase_deck_key);
        profNames = new ArrayList<>();
        categoryNames = new ArrayList<>();
        profIndex = 0;
        categoryIndex = 0;
        flashcardCount = 0;
        terms = new ArrayList<>();
        defs = new ArrayList<>();
        ratingsText = (TextView) v.findViewById(R.id.ratings_input);
        ratingsBar = (SeekBar) v.findViewById(R.id.ratings_bar);
        studyDeckButton = (Button) v.findViewById(R.id.study_deck_button);
        studyDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getContext(), StudyDeckActivity.class);
                    intent.putExtra(deckKey, deckName);
                    intent.putExtra(isFirebaseDeckKey, true);
                    getActivity().startActivity(intent);
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
        cardLayouts = new ArrayList<>();
        cardLabels = new ArrayList<>();

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
        authorNameLabel = (TextView) v.findViewById(R.id.author_name_label);


        ratingsButton = (Button) v.findViewById(R.id.post_rating);

        // TODO once ratings are working, modify this
        ratingsButton.setEnabled(false);

        downloadDeckButton = (Button) v.findViewById(R.id.download_deck);

        // TODO once download decks is working, modify this. May need to create a migration to allow for non-unique deck names
        downloadDeckButton.setEnabled(false);

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
                                try {
                                    profNames = (ArrayList<String>) documentSnapshot.get("professor");
                                } catch (ClassCastException e) {}
                            }
                            if (documentSnapshot.get("category") != null) {
                                try {
                                    categoryNames = (ArrayList<String>) documentSnapshot.get("category");
                                } catch (ClassCastException e) {}
                            }
                            if (documentSnapshot.get("flashcards") != null) {
                                List<Map> f = (List<Map>) documentSnapshot.get("flashcards");
                                // Disable studying if no flashcards currently
                                if (f.size() == 0) {
                                    studyDeckButton.setEnabled(false);
                                }
                                for (Map flashcard : f) {
                                    for (Object obj : flashcard.entrySet()) {
                                        Map.Entry<String, String> entry = (Map.Entry) obj;
                                        terms.add(entry.getKey());
                                        defs.add(entry.getValue());
                                    }
                                }
                                addFlashcardsToView();
                            }
                            if (documentSnapshot.get("owner") != null) {
                                authorNameLabel.setText("Deck Made By: " + documentSnapshot.get("owner"));
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Add the flashcards to the view.
     */
    private void addFlashcardsToView() {
        for (int index = 0; index < terms.size(); index++) {
            String termTxt = terms.get(index);
            String defTxt = defs.get(index);
            LinearLayout ll = (LinearLayout) getView().findViewById(R.id.flashcards_container);
            if (ll != null) {
                // Create a new layout
                ConstraintLayout layout = new ConstraintLayout(getContext());
                final int layoutId = View.generateViewId();
                layout.setId(layoutId);
                cardLayouts.add(layoutId);

                // Setup the widgets
                TextView lbl = new TextView(getContext());
                lbl.setText(Integer.toString(++flashcardCount));
                ViewGroup.LayoutParams lblParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ((ConstraintLayout.LayoutParams) lblParams).setMargins(toDp(16), toDp(8), 0, toDp(8));
                lbl.setLayoutParams(lblParams);
                int lblId = View.generateViewId();
                lbl.setId(lblId);
                cardLabels.add(lblId);

                TextView term = new TextView(getContext());
                ViewGroup.LayoutParams termParams = new ConstraintLayout.LayoutParams(toDp(160), ViewGroup.LayoutParams.MATCH_PARENT);
                ((ConstraintLayout.LayoutParams) termParams).setMargins(toDp(8), toDp(16), 0, toDp(8));
                term.setLayoutParams(termParams);
                int termId = View.generateViewId();
                term.setId(termId);
                term.setText(termTxt);

                TextView definition = new TextView(getContext());
                ViewGroup.LayoutParams defParams = new ConstraintLayout.LayoutParams(toDp(160), ViewGroup.LayoutParams.MATCH_PARENT);
                ((ConstraintLayout.LayoutParams) defParams).setMargins(toDp(8), toDp(16), 0, toDp(8));
                definition.setLayoutParams(defParams);
                int defId = View.generateViewId();
                definition.setId(defId);
                definition.setText(defTxt);


                // Add widgets to layout
                layout.addView(lbl);
                layout.addView(term);
                layout.addView(definition);

                // Add constraints so widgets line up correctly
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                // Set top and bottom layouts of all widgets
                int[] idList = {lblId, termId, defId};
                for (int i = 0; i < idList.length; i++) {
                    constraintSet.connect(idList[i], ConstraintSet.TOP, layoutId, ConstraintSet.TOP);
                    constraintSet.connect(idList[i], ConstraintSet.BOTTOM, layoutId, ConstraintSet.BOTTOM);
                }
                // Set horizontal constraints
                constraintSet.connect(lblId, ConstraintSet.START, layoutId, ConstraintSet.START);
                constraintSet.connect(termId, ConstraintSet.START, lblId, ConstraintSet.END);
                constraintSet.connect(defId, ConstraintSet.START, termId, ConstraintSet.END);
                constraintSet.connect(defId, ConstraintSet.END, layoutId, ConstraintSet.END);

                // Apply constraints
                constraintSet.applyTo(layout);

                if (flashcardCount % 2 == 0) {
                    layout.setBackgroundResource(android.R.color.background_light);
                } else {
                    layout.setBackgroundResource(android.R.color.darker_gray);
                }

                // Set layout height
                layout.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toDp(56)));
                ll.addView(layout);
            }
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

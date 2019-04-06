package com.example.flashcardapp;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcardapp.Activities.StudyDeckActivity;
import com.example.flashcardapp.RoomDatabase.Category;
import com.example.flashcardapp.RoomDatabase.Deck;
import com.example.flashcardapp.RoomDatabase.Flashcard;
import com.example.flashcardapp.RoomDatabase.Professor;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
    private String markedCardsKey;
    private TextView deckNameLabel;
    private TextView schoolNameLabel;
    private TextView courseNameLabel;
    private TextView professorNameLabel;
    private TextView categoryNameLabel;
    private TextView authorNameLabel;
    private TextView averageRating;
    private TextView ratingsLabel;
    private ArrayList<String> profNames;
    private ArrayList<String> categoryNames;
    private List<String> terms;
    private List<String> defs;
    private int profIndex;
    private int categoryIndex;
    private int flashcardCount;
    private List<Integer> cardLayouts;
    private List<Integer> cardLabels;
    private ArrayList<String> markedCards;
    private FirebaseUser user;
    private DeckViewModel mDeckViewModel;
    private ProfessorViewModel mProfessorViewModel;
    private CategoryViewModel mCategoryViewModel;
    private FlashcardViewModel mFlashcardViewModel;
    private boolean mJustChanged;
    private boolean mNeedToAddProfs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDeckViewModel = ViewModelProviders.of(this).get(DeckViewModel.class);
        mProfessorViewModel = ViewModelProviders.of(this).get(ProfessorViewModel.class);
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        mFlashcardViewModel = ViewModelProviders.of(this).get(FlashcardViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uneditable_deck, container, false);
        deckKey = getString(R.string.NameString);
        deckName = getActivity().getIntent().getStringExtra(deckKey);
        user = FirebaseAuth.getInstance().getCurrentUser();
        isFirebaseDeckKey = getString(R.string.is_firebase_deck_key);
        markedCardsKey = getString(R.string.marked_cards);
        profNames = new ArrayList<>();
        categoryNames = new ArrayList<>();
        markedCards = new ArrayList<>();
        profIndex = 0;
        categoryIndex = 0;
        flashcardCount = 0;
        terms = new ArrayList<>();
        defs = new ArrayList<>();
        mJustChanged = false;
        mNeedToAddProfs = true;
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
                    intent.putStringArrayListExtra(markedCardsKey, markedCards);
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
        averageRating = (TextView) v.findViewById(R.id.average_rating);
        authorNameLabel = (TextView) v.findViewById(R.id.author_name_label);
        ratingsLabel = (TextView) v.findViewById(R.id.ratings_label);
        ratingsButton = (Button) v.findViewById(R.id.post_rating);
        ratingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference deckDocumentReference = FirebaseFirestore.getInstance().collection("decks").document(deckName);
                if (deckDocumentReference != null) {
                    deckDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    Map<String, Object> deckInfo = snapshot.getData();
                                    if (snapshot.get("ratings_by_user") == null) {
                                        Map<String, Long> ratings = new HashMap<>();
                                        if (user != null) {
                                            ratings.put(user.getEmail(), (long) mRating);
                                        }
                                        deckInfo.put("ratings_by_user", ratings);

                                        // First rating in deck should be the average
                                        deckInfo.replace("rating", mRating);

                                        // Update textview
                                        averageRating.setText("Average Rating: " + mRating);
                                    } else {
                                        if (user != null) {
                                            Map<String, Long> ratings = (Map) snapshot.get("ratings_by_user");
                                            if (ratings.containsKey(user.getEmail())) {
                                                ratings.replace(user.getEmail(), (long) mRating);
                                            } else {
                                                ratings.put(user.getEmail(), (long) mRating);
                                            }
                                            deckInfo.replace("ratings_by_user", ratings);

                                            // Recalculate average rating
                                            double numRatings = ratings.size();
                                            long sumRatings = 0;
                                            for (Map.Entry<String, Long> rating : ratings.entrySet()) {
                                                sumRatings += rating.getValue();
                                            }
                                            double avgRating = sumRatings / numRatings;

                                            deckInfo.replace("rating", avgRating);

                                            // Update textview
                                            averageRating.setText("Average Rating: " + avgRating);
                                        }
                                    }
                                    deckDocumentReference.set(deckInfo);
                                }
                            }
                        }
                    });
                }
            }
        });

        downloadDeckButton = (Button) v.findViewById(R.id.download_deck);

        // TODO once download decks is working, modify this. May need to create a migration to allow for non-unique deck names
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getEmail() == null) {
            downloadDeckButton.setEnabled(false);
        }

        downloadDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String dName = deckNameLabel.getText().toString();

                mDeckViewModel.getSelectDecks(dName).observe(getActivity(), new Observer<List<Deck>>() {
                    @Override
                    public void onChanged(@Nullable List<Deck> decks) {
                        final Deck deck = new Deck(dName);
                        deck.setCourse(courseNameLabel.getText().toString());
                        deck.setSchool(schoolNameLabel.getText().toString());
                        String email = "guest";
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null && user.getEmail() != null) {
                            email = user.getEmail();
                        }
                        deck.setOwnerEmail(email);

                        // download new deck
                        if (decks != null && decks.size() == 0) {
                            mNeedToAddProfs = true;
                            mDeckViewModel.insert(deck);
                            mJustChanged = true;
                            onSelectedDeckUpdated(deck, dName);
                            Toast.makeText(getContext(), "Deck downloaded", Toast.LENGTH_LONG).show();

                        // update deck
                        } else if (!mJustChanged){
                            mJustChanged = true;
                            final Deck oldDeck = new Deck(dName);

                            mFlashcardViewModel.deleteAllFlashcardsInDeck(oldDeck.getName());
                            mProfessorViewModel.deleteAllProfessorsInDeck(oldDeck.getName());
                            mCategoryViewModel.deleteAllCategoriesInDeck(oldDeck.getName());

                            mDeckViewModel.update(deck, oldDeck);
                            onSelectedDeckUpdated(deck, dName);
                            Toast.makeText(getContext(), "Deck updated", Toast.LENGTH_LONG).show();

                        }
                    }
                });
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
                                String ownerEmail = documentSnapshot.get("owner").toString();
                                authorNameLabel.setText("Deck Made By: " + ownerEmail);
                                if (user.getEmail() == null || ownerEmail.equals(user.getEmail())) {
                                    // Disable ratings
                                    ratingsButton.setEnabled(false);
                                    ratingsBar.setEnabled(false);

                                    // Disable downloading
                                    downloadDeckButton.setEnabled(false);

                                    String displayText = "Sign up to Rate and Download this Deck";
                                    if (ownerEmail.equals(user.getEmail())) {
                                        displayText = "This is your deck";
                                    }
                                    ratingsLabel.setText(displayText);
                                }
                            }
                            if (documentSnapshot.get("rating") != null) {
                                Object obj = documentSnapshot.get("rating");
                                double avgRating = 0;
                                try {
                                    avgRating = Double.parseDouble(obj.toString());
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Issue with ratings", Toast.LENGTH_SHORT).show();
                                }
                                averageRating.setText("Average Rating: " + avgRating);
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
                final String lblTxt = Integer.toString(Integer.parseInt(lbl.getText().toString()) - 1);
                ViewGroup.LayoutParams lblParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ((ConstraintLayout.LayoutParams) lblParams).setMargins(toDp(16), toDp(8), 0, toDp(8));
                lbl.setLayoutParams(lblParams);
                int lblId = View.generateViewId();
                lbl.setId(lblId);
                cardLabels.add(lblId);

                TextView term = new TextView(getContext());
                ViewGroup.LayoutParams termParams = new ConstraintLayout.LayoutParams(toDp(140), ViewGroup.LayoutParams.MATCH_PARENT);
                ((ConstraintLayout.LayoutParams) termParams).setMargins(toDp(8), toDp(16), 0, toDp(8));
                term.setLayoutParams(termParams);
                int termId = View.generateViewId();
                term.setId(termId);
                term.setText(termTxt);

                TextView definition = new TextView(getContext());
                ViewGroup.LayoutParams defParams = new ConstraintLayout.LayoutParams(toDp(140), ViewGroup.LayoutParams.MATCH_PARENT);
                ((ConstraintLayout.LayoutParams) defParams).setMargins(toDp(8), toDp(16), 0, toDp(8));
                definition.setLayoutParams(defParams);
                int defId = View.generateViewId();
                definition.setId(defId);
                definition.setText(defTxt);

                CheckBox checkBox = new CheckBox(getContext());
                ViewGroup.LayoutParams checkBoxParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ((ConstraintLayout.LayoutParams) checkBoxParams).setMargins(toDp(8), toDp(8), toDp(16), toDp(8));
                checkBox.setLayoutParams(checkBoxParams);
                int checkboxId = View.generateViewId();
                checkBox.setId(checkboxId);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        // Add or remove from list of marked indexes
                        if (b) {
                            markedCards.add(lblTxt);
                        } else {
                            markedCards.remove(lblTxt);
                        }
                        markedCards.sort(new Comparator<String>() {
                            @Override
                            public int compare(String s, String t1) {
                                int int1 = Integer.parseInt(s);
                                int int2 = Integer.parseInt(t1);
                                return int1 - int2;
                            }
                        });
                    }
                });

                // Add widgets to layout
                layout.addView(lbl);
                layout.addView(term);
                layout.addView(definition);
                layout.addView(checkBox);

                // Add constraints so widgets line up correctly
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                // Set top and bottom layouts of all widgets
                int[] idList = {lblId, termId, defId, checkboxId};
                for (int i = 0; i < idList.length; i++) {
                    constraintSet.connect(idList[i], ConstraintSet.TOP, layoutId, ConstraintSet.TOP);
                    constraintSet.connect(idList[i], ConstraintSet.BOTTOM, layoutId, ConstraintSet.BOTTOM);
                }
                // Set horizontal constraints
                constraintSet.connect(lblId, ConstraintSet.START, layoutId, ConstraintSet.START);
                constraintSet.connect(termId, ConstraintSet.START, lblId, ConstraintSet.END);
                constraintSet.connect(defId, ConstraintSet.START, termId, ConstraintSet.END);
                constraintSet.connect(checkboxId, ConstraintSet.START, defId, ConstraintSet.END);
                constraintSet.connect(checkboxId, ConstraintSet.END, layoutId, ConstraintSet.END);

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

    private void onSelectedDeckUpdated(Deck deck, String dName) {
        //mDeckViewModel.insert(deck);
        mJustChanged = true;

        if (mNeedToAddProfs) {
            for (String prof : profNames) {
                final Professor professor = new Professor();
                professor.setProfessorName(prof);
                professor.setDeckName(dName);
                mProfessorViewModel.insert(professor);
            }
            for (String cat : categoryNames) {
                final Category category = new Category();
                category.setCategoryName(cat);
                category.setDeckName(dName);
                mCategoryViewModel.insert(category);
            }

            for (int i = 0; i < terms.size(); i++) {
                String termTxt = terms.get(i);
                String defTxt = defs.get(i);
                Flashcard flashcard = new Flashcard();
                flashcard.setDeckName(dName);
                flashcard.setTerm(termTxt);
                flashcard.setDefinition(defTxt);
                flashcard.setMarked(false);
                mFlashcardViewModel.insert(flashcard);
            }
        }
    }
}

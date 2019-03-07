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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DeckHomeFragment extends Fragment implements Observer<List<Deck>> {
    private Button deckViewButton;
    private Button saveButton;
    private Button addFlashcardButton;
    private int flashcardCount = 0;
    private List<Integer> cardLayouts;
    private List<Integer> cardLabels;
    private TextView deckName;
    private TextView courseName;
    private TextView schoolName;
    private TextView profName;
    private TextView categoryName;
    private String deckKey;
    private String courseKey;
    private String schoolKey;
    private String professorKey;
    private String categoryKey;
    private ArrayList<String> profNames;
    private ArrayList<String> categoryNames;
    private int profIndex;
    private int categoryIndex;
    private String completedDeckKey;
    private String deckNameKey;
    private Intent mIntent;
    private Intent sourceIntent;
    private String isNewDeckKey;
    private DeckViewModel mDeckViewModel;
    private List<Integer> termIds;
    private List<Integer> defIds;
    private String dName;
    private boolean updatedDb;
    private List<Deck> mSelectedDecks;
    private boolean mJustChanged;
    private Button mBackButton;
    private String cName;
    private String sName;
    private Deck mDeck;
    private Button deleteButton;
    private ProfessorViewModel mProfessorViewModel;
    private boolean mNeedToAddProfs;
    private boolean mNeedToUpdateProf;
    private FlashcardViewModel mFlashcardViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDeckViewModel = ViewModelProviders.of(this).get(DeckViewModel.class);
        mProfessorViewModel = ViewModelProviders.of(this).get(ProfessorViewModel.class);
        mFlashcardViewModel = ViewModelProviders.of(this).get(FlashcardViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deck_home, container, false);

        // Setup constants
        deckKey = getResources().getString(R.string.NameString);
        courseKey = getResources().getString(R.string.CourseString);
        schoolKey = getResources().getString(R.string.SchoolString);
        professorKey = getResources().getString(R.string.ProfessorString);
        categoryKey = getResources().getString(R.string.CategoryString);
        cardLayouts = new ArrayList<>();
        cardLabels = new ArrayList<>();
        profIndex = 0;
        categoryIndex = 0;
        completedDeckKey = getString(R.string.completed_deck_key);
        deckNameKey = getString(R.string.deck_name_key);
        sourceIntent = getActivity().getIntent();
        isNewDeckKey = getString(R.string.is_new_deck_key);
        termIds = new ArrayList<>();
        defIds = new ArrayList<>();
        mNeedToAddProfs = true;
        profNames = new ArrayList<>();

        // Get references to text view widgets
        deckName = (TextView) v.findViewById(R.id.deck_name_label);
        courseName = (TextView) v.findViewById(R.id.course_name_label);
        schoolName = (TextView) v.findViewById(R.id.school_name_label);
        profName = (TextView) v.findViewById(R.id.professor_name_label);
        categoryName = (TextView) v.findViewById(R.id.category_name_label);
        dName = "";

        // Button click listeners
        deckViewButton = (Button) v.findViewById(R.id.LaunchDeckButton);
        deckViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent sendData = new Intent(getContext(), DeckEditActivity.class);
                    sendData.putExtra(deckKey, deckName.getText());
                    sendData.putExtra(courseKey, courseName.getText());
                    sendData.putExtra(schoolKey, schoolName.getText());

                    // Send the prof and category names
                    if (profNames != null && profNames.size() > 0) {
                        sendData.putStringArrayListExtra(professorKey, profNames);
                    }
                    if (categoryNames != null && categoryNames.size() > 0) {
                        sendData.putStringArrayListExtra(categoryKey, categoryNames);
                    }
                    startActivityForResult(sendData, 0);
                }
            }
        });
        saveButton = (Button) v.findViewById(R.id.save_changes_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    updateDatabase(sourceIntent.getBooleanExtra(isNewDeckKey, true));

                    if (updatedDb) {
                        // TODO debug situation where adding new deck need to click twice
                        mIntent = new Intent();
                        mIntent.putExtra(completedDeckKey, true);
                        mIntent.putExtra(deckNameKey, deckName.getText());
                        getActivity().setResult(Activity.RESULT_OK, mIntent);
                        Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                }
            }
        });

        mBackButton = v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    mIntent = new Intent();
                    mIntent.putExtra(completedDeckKey, false);
                    getActivity().setResult(Activity.RESULT_OK, mIntent);
                    getActivity().finish();
                }
            }
        });

        deleteButton = v.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String dName = deckName.getText().toString();
                String coName = courseName.getText().toString();
                String sName = schoolName.getText().toString();
                final Deck deck = new Deck(dName);
                deck.setCourse(coName);
                deck.setSchool(sName);

                mFlashcardViewModel.deleteAllFlashcardsInDeck(dName);
                mDeckViewModel.delete(deck);

                mIntent = new Intent();
                mIntent.putExtra(completedDeckKey, true);
                mIntent.putExtra(deckNameKey, deckName.getText());
                getActivity().setResult(Activity.RESULT_OK, mIntent);

                Toast.makeText(getContext(), "Deck was deleted successfully", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });

        boolean isNewDeck = sourceIntent.getBooleanExtra(isNewDeckKey, true);
        if (!isNewDeck) {
            // Need to populate the text fields
            dName = sourceIntent.getStringExtra(deckNameKey);
            if (dName != null) {
                deckName.setText(dName);

                // Update text fields
                mDeckViewModel.getSelectDecks(dName).observe(this, this);
                mProfessorViewModel.getAllProfessorsFromDeck(dName).observe(this, new ProfessorObserver());
                mFlashcardViewModel.getAllFlashcardsFromDeck(dName).observe(this, new FlashcardObserver());
            }
        } else {
            deleteButton.setEnabled(false);
        }



        return v;
    }

    private class FlashcardObserver implements Observer<List<Flashcard>> {
        @Override
        public void onChanged(@Nullable List<Flashcard> flashcards) {
            for (Flashcard flashcard : flashcards) {
                addFlashcard(flashcard.getTerm(), flashcard.getDefinition());
            }
        }
    }

    private class ProfessorObserver implements Observer<List<Professor>> {
        @Override
        public void onChanged(@Nullable List<Professor> professors) {
            for (Professor professor : professors) {
                profNames.add(professor.getProfessorName());
            }
        }
    }

    /**
     * Updates the local database by either inserting a new deck or updating the existing deck.
     */
    public void updateDatabase(boolean isNewDeck) {
        if (isNewDeck) {
            final String dName = deckName.getText().toString();
            String coName = courseName.getText().toString();
            String sName = schoolName.getText().toString();
            final Deck deck = new Deck(dName);
            deck.setCourse(coName);
            deck.setSchool(sName);

            mDeckViewModel.getSelectDecks(dName).observe(this, new Observer<List<Deck>>() {
                @Override
                public void onChanged(@Nullable List<Deck> decks) {
                    mSelectedDecks = decks;
                    if (mSelectedDecks != null && mSelectedDecks.size() == 0) {
                        mNeedToAddProfs = true;
                        onSelectedDeckUpdated(deck, dName);
                    } else if (!mJustChanged){
                        Toast.makeText(getContext(), "The deck with the name " + dName + " already "
                                + "exists. Please choose a different name", Toast.LENGTH_LONG).show();
                        mNeedToAddProfs = false;
                        mJustChanged = false;
                    }
                }
            });
        } else {
            final Deck oldDeck = new Deck(dName);
            final String dName = deckName.getText().toString();
            String coName = courseName.getText().toString();
            String sName = schoolName.getText().toString();
            final Deck deck = new Deck(dName);
            deck.setCourse(coName);
            deck.setSchool(sName);

            for (String prof : profNames) {
                final Professor professor = new Professor();
                professor.setProfessorName(prof);
                professor.setDeckName(dName);
                mProfessorViewModel.getProfessorByName(prof, dName).observe(this, new Observer<List<Professor>>() {
                    @Override
                    public void onChanged(@Nullable List<Professor> professors) {
                        // TODO possibly change or remove this condition
                        mNeedToUpdateProf = (professors != null && professors.size() > 0);
                    }
                });

                if (mNeedToUpdateProf) {
                    mProfessorViewModel.update(professor);
                } else {
                    // TODO prevent extra inserts
                    mProfessorViewModel.insert(professor);
                }
            }

            mFlashcardViewModel.deleteAllFlashcardsInDeck(oldDeck.getName());

            mDeckViewModel.update(deck, oldDeck);

            for (int i = 0; i < termIds.size(); i++) {
                String termTxt = ((TextView) getView().findViewById(termIds.get(i))).getText().toString();
                String defTxt = ((TextView) getView().findViewById(defIds.get(i))).getText().toString();
                Flashcard flashcard = new Flashcard();
                flashcard.setDeckName(dName);
                flashcard.setTerm(termTxt);
                flashcard.setDefinition(defTxt);
                mFlashcardViewModel.insert(flashcard);
            }


            updatedDb = true;
        }
    }

    private void onSelectedDeckUpdated(Deck deck, String dName) {
        mDeckViewModel.insert(deck);
        updatedDb = true;
        mJustChanged = true;

        if (mNeedToAddProfs) {
            for (String prof : profNames) {
                final Professor professor = new Professor();
                professor.setProfessorName(prof);
                professor.setDeckName(dName);
                mProfessorViewModel.insert(professor);
            }

            for (int i = 0; i < termIds.size(); i++) {
                String termTxt = ((TextView) getView().findViewById(termIds.get(i))).getText().toString();
                String defTxt = ((TextView) getView().findViewById(defIds.get(i))).getText().toString();
                Flashcard flashcard = new Flashcard();
                flashcard.setDeckName(dName);
                flashcard.setTerm(termTxt);
                flashcard.setDefinition(defTxt);
                mFlashcardViewModel.insert(flashcard);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();

        if (extras != null) {
            // Set the deck title based on the entered string
            String deckTitle = extras.getString(deckKey);
            if (deckTitle != null) {
                deckName.setText(deckTitle);
            }

            // Set the course title based on the entered string
            String courseTitle = extras.getString(courseKey);
            if (courseTitle != null) {
                courseName.setText(courseTitle);
            }

            // Set the school title based on the entered string
            String schoolTitle = extras.getString(schoolKey);
            if (schoolTitle != null) {
                schoolName.setText(schoolTitle);
            }

            // Set the professor and category names based on the given strings
            if (extras.getStringArrayList(professorKey) != null) {
                profNames = extras.getStringArrayList(professorKey);
            }
            if (extras.getStringArrayList(categoryKey) != null) {
                categoryNames = extras.getStringArrayList(categoryKey);
            }

        }
    }

    public void addFlashcard(String termTxt, String defTxt) {


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
            ((ConstraintLayout.LayoutParams) lblParams).setMargins(toDp(16), toDp(12), 0, 0);
            lbl.setLayoutParams(lblParams);
            int lblId = View.generateViewId();
            lbl.setId(lblId);
            cardLabels.add(lblId);

            EditText term = new EditText(getContext());
            term.setHint(R.string.TermString);
            ViewGroup.LayoutParams termParams = new ConstraintLayout.LayoutParams(toDp(140), ViewGroup.LayoutParams.MATCH_PARENT);
            ((ConstraintLayout.LayoutParams) termParams).setMargins(toDp(8), toDp(8), 0, toDp(8));
            term.setLayoutParams(termParams);
            int termId = View.generateViewId();
            term.setId(termId);
            termIds.add(termId);
            term.setText(termTxt);

            EditText definition = new EditText(getContext());
            definition.setHint(R.string.DefinitionString);
            ViewGroup.LayoutParams defParams = new ConstraintLayout.LayoutParams(toDp(140), ViewGroup.LayoutParams.MATCH_PARENT);
            ((ConstraintLayout.LayoutParams) defParams).setMargins(toDp(8), toDp(8), 0, toDp(8));
            definition.setLayoutParams(defParams);
            int defId = View.generateViewId();
            definition.setId(defId);
            defIds.add(defId);
            definition.setText(defTxt);

            ImageView deleteIcon = new ImageView(getContext());
            deleteIcon.setImageResource(android.R.drawable.ic_delete);
            ViewGroup.LayoutParams iconParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((ConstraintLayout.LayoutParams) iconParams).setMargins(toDp(10), toDp(8), toDp(8), toDp(8));
            deleteIcon.setLayoutParams(iconParams);
            int iconId = View.generateViewId();
            deleteIcon.setId(iconId);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the constraint layout and its parent
                    ConstraintLayout cl = getView().findViewById(layoutId);
                    LinearLayout container = getView().findViewById(R.id.flashcards_container);

                    // Clear the constraint layout
                    cl.removeAllViews();

                    // Remove the constraint layout
                    container.removeView(cl);

                    // Need to fix all the flashcards that come below
                    int index = cardLayouts.indexOf(layoutId);
                    cardLayouts.remove(index);
                    cardLabels.remove(index);
                    termIds.remove(index);
                    defIds.remove(index);
                    flashcardCount--;
                    for (int i = index; i < cardLayouts.size(); i++) {
                        // Fix index number
                        TextView label = getView().findViewById(cardLabels.get(i));
                        label.setText(Integer.toString(i + 1));

                        // Fix background color
                        ConstraintLayout l = getView().findViewById(cardLayouts.get(i));
                        if (i % 2 == 1) {
                            l.setBackgroundResource(android.R.color.background_light);
                        } else {
                            l.setBackgroundResource(android.R.color.darker_gray);
                        }
                    }
                }
            });


            // Add widgets to layout
            layout.addView(lbl);
            layout.addView(term);
            layout.addView(definition);
            layout.addView(deleteIcon);

            // Add constraints so widgets line up correctly
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);
            // Set top and bottom layouts of all widgets
            int[] idList = {lblId, termId, defId, iconId};
            for (int i = 0; i < idList.length; i++) {
                constraintSet.connect(idList[i], ConstraintSet.TOP, layoutId, ConstraintSet.TOP);
                constraintSet.connect(idList[i], ConstraintSet.BOTTOM, layoutId, ConstraintSet.BOTTOM);
            }
            // Set horizontal constraints
            constraintSet.connect(lblId, ConstraintSet.START, layoutId, ConstraintSet.START);
            constraintSet.connect(termId, ConstraintSet.START, lblId, ConstraintSet.END);
            constraintSet.connect(defId, ConstraintSet.START, termId, ConstraintSet.END);
            constraintSet.connect(iconId, ConstraintSet.START, defId, ConstraintSet.END);
            constraintSet.connect(iconId, ConstraintSet.END, layoutId, ConstraintSet.END);

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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        addFlashcardButton = (Button) view.findViewById(R.id.add_flashcard_button);
        addFlashcardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                addFlashcard("", "");
            }
        });

        profName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profNames != null && profNames.size() > 0) {
                    profName.setText(profNames.get(profIndex));
                    profIndex++;
                    profIndex %= profNames.size();
                }
            }
        });

        categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryNames != null && categoryNames.size() > 0) {
                    categoryName.setText(categoryNames.get(categoryIndex));
                    categoryIndex++;
                    categoryIndex %= categoryNames.size();
                }
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

    /**
     * On changed method for observing changes made to an already existing deck.
     * This method accesses an already existing deck with the name provided in the
     * query called by onCreateView() when the new activity is created. This provided
     * name allows for population of the course and school fields.
     *
     * @param decks
     *          list of decks returned by the following query (should only be one):
     *              SELECT * FROM deck_name WHERE name = :dName;
     *          Note that dName is equivalent to the name provided by onCreateView()
     */
    @Override
    public void onChanged(@Nullable List<Deck> decks) {
        if (decks != null && decks.size() > 0) {
            mDeck = decks.get(0);
            schoolName.setText(mDeck.getSchool());
            courseName.setText(mDeck.getCourse());
        }
    }
}

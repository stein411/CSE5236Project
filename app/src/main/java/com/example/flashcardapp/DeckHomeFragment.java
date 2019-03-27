package com.example.flashcardapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcardapp.Activities.DeckEditActivity;
import com.example.flashcardapp.Activities.DeckHomeActivity;
import com.example.flashcardapp.Activities.StudyDeckActivity;
import com.example.flashcardapp.RoomDatabase.Category;
import com.example.flashcardapp.RoomDatabase.Deck;
import com.example.flashcardapp.RoomDatabase.Flashcard;
import com.example.flashcardapp.RoomDatabase.Professor;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeckHomeFragment extends Fragment implements Observer<List<Deck>> {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private Button deckViewButton;
    private Button saveButton;
    private Button addFlashcardButton;
    private Button postDeckButton;
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
    private String isFirebaseDeckKey;
    private String ownerEmail;

    private DocumentReference deck;
    private FusedLocationProviderClient fusedLocationClient;

    private DeckViewModel mDeckViewModel;
    private List<Integer> termIds;
    private List<Integer> defIds;
    private String dName;
    private boolean updatedDb;
    private List<Deck> mSelectedDecks;
    private boolean mJustChanged;
    private boolean addFlashcardsToUI;
    private Button mBackButton;
    private Deck mDeck;
    private Button deleteButton;
    private ProfessorViewModel mProfessorViewModel;
    private boolean mNeedToAddProfs;
    private boolean mNeedToUpdateProf;
    private CategoryViewModel mCategoryViewModel;
    private FlashcardViewModel mFlashcardViewModel;
    private Button studyDeckButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDeckViewModel = ViewModelProviders.of(this).get(DeckViewModel.class);
        mProfessorViewModel = ViewModelProviders.of(this).get(ProfessorViewModel.class);
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        mFlashcardViewModel = ViewModelProviders.of(this).get(FlashcardViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
        isFirebaseDeckKey = getString(R.string.is_firebase_deck_key);
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
        categoryNames = new ArrayList<>();
        ownerEmail = "guest";

        // Get references to text view widgets
        deckName = (TextView) v.findViewById(R.id.deck_name_label);
        courseName = (TextView) v.findViewById(R.id.course_name_label);
        schoolName = (TextView) v.findViewById(R.id.school_name_label);
        profName = (TextView) v.findViewById(R.id.professor_name_label);
        categoryName = (TextView) v.findViewById(R.id.category_name_label);
        dName = "";

        addFlashcardsToUI = true;

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
        final String deckTitle = deckName.getText().toString();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    addFlashcardsToUI = false;
                    updateDatabase(sourceIntent.getBooleanExtra(isNewDeckKey, true));

                    //I used deckTitle2 since deckTitle is "Deck Name" permanently for some reason
                    final String deckTitle2 = deckName.getText().toString();

                    // Guests cannot add to Firebase
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if (user != null && user.getEmail() != null) {
//                        ownerEmail = user.getEmail();
//                        addDeckInfoToFirebase(deckTitle2, ownerEmail, profNames, categoryNames, 0, courseName.getText().toString(), schoolName.getText().toString());
//                        addFlashcardToFirebase(deckTitle2);
//                    }
                    if (updatedDb) {
                        // TODO debug situation where adding new deck need to click twice
                        mIntent = new Intent();
                        mIntent.putExtra(completedDeckKey, true);
                        mIntent.putExtra(deckNameKey, deckName.getText());
                        getActivity().setResult(Activity.RESULT_OK, mIntent);
                        Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getContext(), DeckHomeActivity.class);
//                        Bundle extras = sourceIntent.getExtras();
//                        extras.remove(deckNameKey);
//                        extras.putString(deckNameKey, deckName.getText().toString());
//                        extras.remove(isNewDeckKey);
//                        extras.putBoolean(isNewDeckKey, false);
                        //intent.putExtras(extras);
                        //startActivity(intent);
                        //getActivity().finish();
                        getActivity().getIntent().removeExtra(isNewDeckKey);
                        getActivity().getIntent().putExtra(isNewDeckKey, false);
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
                String deckName1 = dName;
                if (deckName != null) {
                    deckName1 = deckName.getText().toString();
                }
                AlertDialog d = new AlertDialog.Builder(getContext()).setTitle("Flashcards")
                        .setMessage("Are you sure you want to delete the deck " + deckName1 + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final String dName = deckName.getText().toString();
                                String coName = courseName.getText().toString();
                                String sName = schoolName.getText().toString();
                                final Deck deck = new Deck(dName);
                                deck.setCourse(coName);
                                deck.setSchool(sName);
                                String email = "guest";
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null && user.getEmail() != null) {
                                    email = user.getEmail();
                                }
                                deck.setOwnerEmail(email);
                                mFlashcardViewModel.deleteAllFlashcardsInDeck(dName);
                                mDeckViewModel.delete(deck);

                                mIntent = new Intent();
                                mIntent.putExtra(completedDeckKey, true);
                                mIntent.putExtra(deckNameKey, deckName.getText());
                                getActivity().setResult(Activity.RESULT_OK, mIntent);

                                Toast.makeText(getContext(), "Deck was deleted successfully", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
            }
        });

        studyDeckButton = v.findViewById(R.id.study_deck_button);
        studyDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StudyDeckActivity.class);
                intent.putExtra(deckKey, deckName.getText().toString());
                intent.putExtra(isFirebaseDeckKey, false);
                getActivity().startActivity(intent);
            }
        });

        postDeckButton = v.findViewById(R.id.post_deck_button);
        postDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deckName1 = dName;
                if (deckName != null) {
                    deckName1 = deckName.getText().toString();
                }
                AlertDialog d = new AlertDialog.Builder(getContext()).setTitle("Flashcards")
                        .setMessage("Are you sure you want to post the deck " + deckName1 + " online? " +
                                "You may be asked for your location")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String dName = deckName.getText().toString();
                                String coName = courseName.getText().toString();
                                String sName = schoolName.getText().toString();
                                String email = "guest";
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null && user.getEmail() != null) {
                                    email = user.getEmail();
                                }
                                final String finalEmail = email;

                                // Request location info
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    // Permission already granted: set location
                                    fusedLocationClient.getLastLocation()
                                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                                @Override
                                                public void onSuccess(Location location) {
                                                    // Got last known location. In some rare situations this can be null.
                                                    if (location != null) {
                                                        // Logic to handle location object
                                                        final String dName = deckName.getText().toString();
                                                        final String coName = courseName.getText().toString();
                                                        final String sName = schoolName.getText().toString();
                                                        addDeckInfoToFirebaseWithLocation(dName, location, finalEmail, profNames, categoryNames, 0, coName, sName);
                                                        addFlashcardToFirebase(dName);
                                                    }
                                                }
                                            });
                                    //Toast.makeText(getContext(), "Got location permission 1", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Request permission to access location
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                                }


                                Toast.makeText(getContext(), "Deck was posted successfully", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
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
                mCategoryViewModel.getAllCategoriesFromDeck(dName).observe(this, new CategoryObserver());
                mFlashcardViewModel.getAllFlashcardsFromDeck(dName).observe(this, new FlashcardObserver());
            }
        } else {
            deleteButton.setEnabled(false);
            studyDeckButton.setEnabled(false);
            postDeckButton.setEnabled(false);
        }


        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission was denied. Display a message.
                Toast.makeText(getContext(), "The location of the deck will not be shown at this time", Toast.LENGTH_LONG).show();
                // Post deck without location
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.getEmail() != null)  {
                    ownerEmail = user.getEmail();
                }
                addDeckInfoToFirebase(dName, ownerEmail, profNames, categoryNames, 0, courseName.getText().toString(), schoolName.getText().toString());
                addFlashcardToFirebase(dName);
            }
        }
    }


    /**
     * Adding deck information to firebase.
     * This is NOT adding the flashcards just yet.
     */
    private void addDeckInfoToFirebaseWithLocation(String deckName, Location location, String owner, ArrayList<String> professor, ArrayList<String> category, int rating, String courseName, String schoolName) {
        deck = FirebaseFirestore.getInstance().collection("decks").document(deckName);
        Map<String, Object> deckInfo = new HashMap<String, Object>();
        deckInfo.put("owner", owner);
        deckInfo.put("location", location);
        deckInfo.put("name", deckName);
        deckInfo.put("professor", professor);
        deckInfo.put("category", category);
        deckInfo.put("rating", rating);
        deckInfo.put("course", courseName);
        deckInfo.put("school", schoolName);
        deck.set(deckInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Success", "Document was successfully added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Failed to save to firestore", e);
            }
        });
    }


    /**
     * Adding deck information to firebase.
     * This is NOT adding the flashcards just yet.
     */
    private void addDeckInfoToFirebase(String deckName, String owner, ArrayList<String> professor, ArrayList<String> category, int rating, String courseName, String schoolName) {
        deck = FirebaseFirestore.getInstance().collection("decks").document(deckName);
        Map<String, Object> deckInfo = new HashMap<String, Object>();
        deckInfo.put("owner", owner);
        deckInfo.put("name", deckName);
        deckInfo.put("professor", professor);
        deckInfo.put("category", category);
        deckInfo.put("rating", rating);
        deckInfo.put("course", courseName);
        deckInfo.put("school", schoolName);
        deck.set(deckInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Success", "Document was successfully added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Failed to save to firestore", e);
            }
        });
    }

    /*
     * adds flashcards to firebase firestore
     */
    private void addFlashcardToFirebase(String deckTitle){
        //getting the deck reference
        deck = FirebaseFirestore.getInstance().collection("decks").document(deckTitle);

        /*
         *This map should contain a key called "flashcards", with an object of an ArrayList of
         * maps that contain each individual flashcard.
         * So, Map holds the arraylist which holds a bunch of maps
         */
        Map<String, Object> flashcards = new HashMap<String, Object>();

        /*
         * The arraylist to hold all the maps, each map is 1 flashcard
         */
        List<Map> allTheFlashcards = new ArrayList<>();

        /*
         * Getting flashcards and filling in each map in the arraylist
         */
        for (int i = 0; i < termIds.size(); i++) {

            String termTxt = ((TextView) getView().findViewById(termIds.get(i))).getText().toString();
            String defTxt = ((TextView) getView().findViewById(defIds.get(i))).getText().toString();
            Map<String, String> flashcard = new HashMap<String, String>();
            flashcard.put(termTxt, defTxt);
            allTheFlashcards.add(flashcard);
        }

        /*
         * Now I'm adding the arraylist to the map that I will add to firebase
         */
        flashcards.put("flashcards", allTheFlashcards);
        deck.update(flashcards).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Success", "Document with flashcards was successfully added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Failed to save to firestore", e);
            }
        });

    }

    private class FlashcardObserver implements Observer<List<Flashcard>> {
        @Override
        public void onChanged(@Nullable List<Flashcard> flashcards) {
            if (addFlashcardsToUI) {
                for (Flashcard flashcard : flashcards) {
                    addFlashcard(flashcard.getTerm(), flashcard.getDefinition());
                }
            }
        }
    }

    private class ProfessorObserver implements Observer<List<Professor>> {
        @Override
        public void onChanged(@Nullable List<Professor> professors) {
            if (professors.size() == 0) {
                profName.setText(getString(R.string.prof_deck_home_prompt));
            }
            for (Professor professor : professors) {
                profNames.add(professor.getProfessorName());
            }
        }
    }

    private class CategoryObserver implements Observer<List<Category>> {
        @Override
        public void onChanged(@Nullable List<Category> categories) {
            if (categories.size() == 0) {
                categoryName.setText(getString(R.string.category_deck_home_prompt));
            }
            for (Category category : categories) {
                categoryNames.add(category.getCategoryName());
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
            String email = "guest";
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null) {
                email = user.getEmail();
            }
            deck.setOwnerEmail(email);

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
            String email = "guest";
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null) {
                email = user.getEmail();
            }
            deck.setOwnerEmail(email);

            mFlashcardViewModel.deleteAllFlashcardsInDeck(oldDeck.getName());
            mProfessorViewModel.deleteAllProfessorsInDeck(oldDeck.getName());
            mCategoryViewModel.deleteAllCategoriesInDeck(oldDeck.getName());

            mDeckViewModel.update(deck, oldDeck);

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
            //edmond look here
            for (int i = 0; i < termIds.size(); i++) {
                String termTxt = ((TextView) getView().findViewById(termIds.get(i))).getText().toString();
                String defTxt = ((TextView) getView().findViewById(defIds.get(i))).getText().toString();
                Flashcard flashcard = new Flashcard();
                flashcard.setDeckName(dName);
                flashcard.setTerm(termTxt);
                flashcard.setDefinition(defTxt);
                if (flashcard.getTerm().length() > 0) {
                    // Don't allow flashcards with empty terms
                    mFlashcardViewModel.insert(flashcard);
                }
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
            for (String cat : categoryNames) {
                final Category category = new Category();
                category.setCategoryName(cat);
                category.setDeckName(dName);
                mCategoryViewModel.insert(category);
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
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
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
                if (profNames.size() == 0) {
                    profName.setText(getString(R.string.prof_deck_home_prompt));
                }
            }
            if (extras.getStringArrayList(categoryKey) != null) {
                categoryNames = extras.getStringArrayList(categoryKey);
                if (categoryNames.size() == 0) {
                    categoryName.setText(getString(R.string.category_deck_home_prompt));
                }
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
            deleteIcon.setImageResource(android.R.drawable.ic_menu_delete);
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

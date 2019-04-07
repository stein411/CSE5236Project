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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcardapp.Activities.DeckEditActivity;
import com.example.flashcardapp.Activities.StudyDeckActivity;
import com.example.flashcardapp.RoomDatabase.Category;
import com.example.flashcardapp.RoomDatabase.Deck;
import com.example.flashcardapp.RoomDatabase.Flashcard;
import com.example.flashcardapp.RoomDatabase.Professor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeckHomeFragment.java.
 * Logged-in user's perspective of a deck, where they can add flashcards, edit the deck metadata, and
 * study the terms they have entered.
 */
public class DeckHomeFragment extends Fragment implements Observer<List<Deck>> {
    // INTENT KEYS----------------------------------------------------------------------------------
    private String deckKey;
    private String courseKey;
    private String schoolKey;
    private String professorKey;
    private String categoryKey;
    private String markedCardsKey;
    private String isNewDeckKey;
    private String isFirebaseDeckKey;
    private String completedDeckKey;
    private String deckNameKey;

    // BUTTONS--------------------------------------------------------------------------------------
    private Button deckViewButton;
    private Button saveButton;
    private Button addFlashcardButton;
    private Button mBackButton;
    private Button deleteButton;
    private Button postDeckButton;
    private Button studyDeckButton;

    // TEXT VIEWS-----------------------------------------------------------------------------------
    private TextView deckName;
    private TextView courseName;
    private TextView schoolName;
    private TextView profName;
    private TextView categoryName;
    private TextView averageRating;

    // LISTS----------------------------------------------------------------------------------------
    private List<Integer> cardLayouts;
    private List<Integer> cardLabels;
    private List<Integer> termIds;
    private List<Integer> defIds;
    private List<Integer> checkIds;
    private List<Deck> mSelectedDecks;
    private ArrayList<String> markedCards;
    private ArrayList<String> profNames;
    private ArrayList<String> categoryNames;

    // VIEW MODELS----------------------------------------------------------------------------------
    private DeckViewModel mDeckViewModel;
    private CategoryViewModel mCategoryViewModel;
    private FlashcardViewModel mFlashcardViewModel;
    private ProfessorViewModel mProfessorViewModel;

    // OTHER FIELDS---------------------------------------------------------------------------------
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private int flashcardCount = 0;
    private int profIndex;
    private int categoryIndex;
    private Intent mIntent;
    private Intent sourceIntent;
    private String dName;
    private String ownerEmail;
    private String ratingText;
    private FirebaseUser user;
    private DocumentReference deck;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean mJustChanged;
    private boolean addFlashcardsToUI;
    private Deck mDeck;


    /**
     * OnCreate method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view models
        mDeckViewModel = ViewModelProviders.of(this).get(DeckViewModel.class);
        mProfessorViewModel = ViewModelProviders.of(this).get(ProfessorViewModel.class);
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        mFlashcardViewModel = ViewModelProviders.of(this).get(FlashcardViewModel.class);

        // Setup location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Get the logged-in user
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * OnCreateView method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view with layout elements initialized with event listeners
     */
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
        markedCardsKey = getString(R.string.marked_cards);
        termIds = new ArrayList<>();
        defIds = new ArrayList<>();
        checkIds = new ArrayList<>();
        markedCards = new ArrayList<>();
        profNames = new ArrayList<>();
        categoryNames = new ArrayList<>();
        ownerEmail = "guest";
        ratingText = "Average Rating: 0.0";

        // Get references to text view widgets
        deckName = (TextView) v.findViewById(R.id.deck_name_label);
        courseName = (TextView) v.findViewById(R.id.course_name_label);
        schoolName = (TextView) v.findViewById(R.id.school_name_label);
        profName = (TextView) v.findViewById(R.id.professor_name_label);
        categoryName = (TextView) v.findViewById(R.id.category_name_label);
        dName = "";
        averageRating = (TextView) v.findViewById(R.id.rating);

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
        saveButton = (Button) v.findViewById(R.id.save_deck_metadata);
        final String deckTitle = deckName.getText().toString();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    addFlashcardsToUI = false;
                    updateDatabase(sourceIntent.getBooleanExtra(isNewDeckKey, true));
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
                                if (user != null && user.getEmail() != null && user.getEmail().length() != 0) {
                                    email = user.getEmail();
                                }
                                deck.setOwnerEmail(email);
                                mFlashcardViewModel.deleteAllFlashcardsInDeck(dName);
                                mDeckViewModel.delete(deck);

                                // Delete deck from Firebase
                                final DocumentReference doc = FirebaseFirestore.getInstance().collection("decks").document(dName);
                                if (doc != null) {
                                    doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot snapshot = task.getResult();
                                                if (snapshot.exists()) {
                                                    Object email = snapshot.get("owner");
                                                    if (email != null && email.toString().equals(user.getEmail())) {
                                                        Map<String, Object> updates = new HashMap<>();
                                                        updates.put("location", FieldValue.delete());
                                                        updates.put("flashcards", FieldValue.delete());
                                                        updates.put("professor", FieldValue.delete());
                                                        updates.put("category", FieldValue.delete());
                                                        updates.put("ratings_by_user", FieldValue.delete());
                                                        doc.update(updates);
                                                        doc.delete();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }

                                mIntent = new Intent();
                                mIntent.putExtra(completedDeckKey, true);
                                mIntent.putExtra(deckNameKey, dName);
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
                intent.putStringArrayListExtra(markedCardsKey, markedCards);
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
                                if (user != null && user.getEmail() != null && user.getEmail().length() != 0) {
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

                // Get the average rating from Firebase
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("decks").document(dName);
                if (documentReference != null) {
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    if (snapshot.get("rating") != null) {
                                        ratingText = "Average Rating: " + snapshot.get("rating").toString();
                                        averageRating.setText(ratingText);
                                    }
                                    if (snapshot.get("owner") != null && !snapshot.get("owner").equals(user.getEmail())) {
                                        postDeckButton.setEnabled(false);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        } else {
            deleteButton.setEnabled(false);
            studyDeckButton.setEnabled(false);
            postDeckButton.setEnabled(false);
        }

        // Disable guest posting
        if (user == null || user.getEmail() == null || user.getEmail().length() == 0) {
            postDeckButton.setEnabled(false);
        }

        averageRating.setText(ratingText);

        return v;
    }

    /**
     * OnRequestPermissionsResult method.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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
                if (user != null && user.getEmail() != null && user.getEmail().length() != 0)  {
                    ownerEmail = user.getEmail();
                }
                addDeckInfoToFirebase(dName, ownerEmail, profNames, categoryNames, 0, courseName.getText().toString(), schoolName.getText().toString());
                addFlashcardToFirebase(dName);
            }
        }
    }

    /**
     * Setup the deck info map.
     * @param deckName
     * @param owner
     * @param professor
     * @param category
     * @param rating
     * @param courseName
     * @param schoolName
     * @return hashmap with all the deck metadata
     */
    private Map<String, Object> setupDeckInfoForFirebase(String deckName, String owner, ArrayList<String> professor, ArrayList<String> category, int rating, String courseName, String schoolName) {
        Map<String, Object> deckInfo = new HashMap<>();
        deckInfo.put("owner", owner);
        deckInfo.put("name", deckName);
        deckInfo.put("professor", professor);
        deckInfo.put("category", category);
        deckInfo.put("rating", rating);
        deckInfo.put("course", courseName);
        deckInfo.put("school", schoolName);
        return deckInfo;
    }

    /**
     * Add deck information to firebase, minus flashcards and with location info.
     * @param deckName
     * @param location
     * @param owner
     * @param professor
     * @param category
     * @param rating
     * @param courseName
     * @param schoolName
     */
    private void addDeckInfoToFirebaseWithLocation(String deckName, Location location, String owner, ArrayList<String> professor, ArrayList<String> category, int rating, String courseName, String schoolName) {
        deck = FirebaseFirestore.getInstance().collection("decks").document(deckName);
        Map<String, Object> deckInfo = setupDeckInfoForFirebase(deckName, owner, professor, category, rating, courseName, schoolName);
        deckInfo.put("location", location);
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
     * Add deck information to firebase, minus flashcards.
     * @param deckName
     * @param owner
     * @param professor
     * @param category
     * @param rating
     * @param courseName
     * @param schoolName
     */
    private void addDeckInfoToFirebase(String deckName, String owner, ArrayList<String> professor, ArrayList<String> category, int rating, String courseName, String schoolName) {
        deck = FirebaseFirestore.getInstance().collection("decks").document(deckName);
        Map<String, Object> deckInfo = setupDeckInfoForFirebase(deckName, owner, professor, category, rating, courseName, schoolName);
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
     * Add flashcards to Firebase Firestore.
     * @param deckTitle
     *          name of the deck to add the list of flashcards to
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

    // Observer classes used to get all multivalued attributes for the portrayed deck.

    /**
     * FlashcardObserver class.
     * Determines when there was a change to the flashcard table.
     */
    private class FlashcardObserver implements Observer<List<Flashcard>> {
        @Override
        public void onChanged(@Nullable List<Flashcard> flashcards) {
            if (addFlashcardsToUI) {
                studyDeckButton.setEnabled(flashcards.size() != 0);

                for (Flashcard flashcard : flashcards) {
                    addFlashcard(flashcard.getTerm(), flashcard.getDefinition(), flashcard.isMarked());
                }
            }
        }
    }

    /**
     * ProfessorObserver class.
     * Determines when there was a change to the professor table.
     */
    private class ProfessorObserver implements Observer<List<Professor>> {
        @Override
        public void onChanged(@Nullable List<Professor> professors) {
            if (professors.size() == 0) {
                profName.setText(getString(R.string.prof_deck_home_prompt));
            }
            if (addFlashcardsToUI) {
                for (Professor professor : professors) {
                    profNames.add(professor.getProfessorName());
                }
            }
        }
    }

    /**
     * CategoryObserver class.
     * Determines when there was a change to the category table.
     */
    private class CategoryObserver implements Observer<List<Category>> {
        @Override
        public void onChanged(@Nullable List<Category> categories) {
            if (categories.size() == 0) {
                categoryName.setText(getString(R.string.category_deck_home_prompt));
            }
            if (addFlashcardsToUI) {
                for (Category category : categories) {
                    categoryNames.add(category.getCategoryName());
                }
            }
        }
    }

    /**
     * Updates the local database by either inserting a new deck or updating the existing deck.
     * @param isNewDeck
     *          true if deck is new, false otherwise
     */
    public void updateDatabase(final boolean isNewDeck) {
        if (isNewDeck) {
            final String dName = deckName.getText().toString();
            String coName = courseName.getText().toString();
            String sName = schoolName.getText().toString();
            final Deck deck = new Deck(dName);
            deck.setCourse(coName);
            deck.setSchool(sName);
            String email = "guest";
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null && user.getEmail().length() != 0) {
                email = user.getEmail();
            }
            deck.setOwnerEmail(email);

            mDeckViewModel.getSelectDecks(dName).observe(this, new Observer<List<Deck>>() {
                @Override
                public void onChanged(@Nullable List<Deck> decks) {
                    mSelectedDecks = decks;
                    if (mSelectedDecks != null && mSelectedDecks.size() == 0) {
                        mDeckViewModel.insert(deck);
                        onSelectedDeckUpdated(dName);
                        mIntent = new Intent();
                        mIntent.putExtra(completedDeckKey, true);
                        mIntent.putExtra(deckNameKey, deckName.getText());
                        getActivity().setResult(Activity.RESULT_OK, mIntent);
                        getActivity().getIntent().removeExtra(isNewDeckKey);
                        getActivity().getIntent().putExtra(isNewDeckKey, false);

                        // Enable posting, studying, deleting
                        if (user != null && user.getEmail() != null && user.getEmail().length() != 0) {
                            postDeckButton.setEnabled(true);
                            if (termIds.size() > 0) {
                                studyDeckButton.setEnabled(true);
                            }
                            deleteButton.setEnabled(true);
                        }
                        Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_LONG).show();
                    } else if (!mJustChanged){
                        Toast.makeText(getContext(), "The deck with the name " + dName + " already "
                                + "exists. Please choose a different name.", Toast.LENGTH_LONG).show();
                        mJustChanged = false;
                    }
                }
            });
        } else {
            if (dName.length() == 0) {
                dName = deckName.getText().toString();
            }
            final Deck oldDeck = new Deck(dName);
            final String dName2 = deckName.getText().toString();
            String coName = courseName.getText().toString();
            String sName = schoolName.getText().toString();
            final Deck deck = new Deck(dName2);
            deck.setCourse(coName);
            deck.setSchool(sName);
            String email = "guest";
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null && user.getEmail().length() != 0) {
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
                professor.setDeckName(dName2);
                mProfessorViewModel.insert(professor);
            }

            for (String cat : categoryNames) {
                final Category category = new Category();
                category.setCategoryName(cat);
                category.setDeckName(dName2);
                mCategoryViewModel.insert(category);
            }
            //edmond look here
            for (int i = 0; i < termIds.size(); i++) {
                String termTxt = ((TextView) getView().findViewById(termIds.get(i))).getText().toString();
                String defTxt = ((TextView) getView().findViewById(defIds.get(i))).getText().toString();
                Flashcard flashcard = new Flashcard();
                flashcard.setDeckName(dName2);
                flashcard.setTerm(termTxt);
                flashcard.setDefinition(defTxt);

                // Get the flashcard checked value
                CheckBox checkBox = (CheckBox) getView().findViewById(checkIds.get(i));
                flashcard.setMarked(checkBox.isChecked());

                if (flashcard.getTerm().length() > 0) {
                    // Don't allow flashcards with empty terms
                    mFlashcardViewModel.insert(flashcard);
                }
            }
            mIntent = new Intent();
            mIntent.putExtra(completedDeckKey, true);
            mIntent.putExtra(deckNameKey, deckName.getText());
            getActivity().setResult(Activity.RESULT_OK, mIntent);
            getActivity().getIntent().removeExtra(isNewDeckKey);
            getActivity().getIntent().putExtra(isNewDeckKey, false);

            // Enable posting
            if (user != null && user.getEmail() != null && user.getEmail().length() != 0) {
                // Only enable posting if original owner (or document doesn't exist yet)
                //postDeckButton.setEnabled(false);
                final DocumentReference doc = FirebaseFirestore.getInstance().collection("decks").document(dName);
                if (doc != null) {
                    doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    Object email = snapshot.get("email");
                                    if (email != null) {
                                        postDeckButton.setEnabled(email.toString().equals(user.getEmail()));
                                    }
                                } else {
                                    postDeckButton.setEnabled(true);
                                }
                            }
                        }
                    });
                }
            }
            Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_LONG).show();
            dName = dName2;
        }
    }

    private void onSelectedDeckUpdated(String dName) {
        mJustChanged = true;

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
            //flashcard.setMarked(false);
            // TODO get marked value
            mFlashcardViewModel.insert(flashcard);
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

    /**
     * Adds the given flashcard to the view.
     * @param termTxt
     *          term of the flashcard to add
     * @param defTxt
     *          definition of the flashcard to add
     * @param isMarked
     *          previous marked status of the current flashcard (false for new cards)
     */
    public void addFlashcard(String termTxt, String defTxt, boolean isMarked) {
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
            ((ConstraintLayout.LayoutParams) lblParams).setMargins(toDp(16), toDp(12), 0, 0);
            lbl.setLayoutParams(lblParams);
            final int lblId = View.generateViewId();
            lbl.setId(lblId);
            cardLabels.add(lblId);

            // Term edit text
            EditText term = new EditText(getContext());
            term.setHint(R.string.TermString);
            ViewGroup.LayoutParams termParams = new ConstraintLayout.LayoutParams(toDp(110), ViewGroup.LayoutParams.MATCH_PARENT);
            ((ConstraintLayout.LayoutParams) termParams).setMargins(toDp(8), toDp(8), 0, toDp(8));
            term.setLayoutParams(termParams);
            int termId = View.generateViewId();
            term.setId(termId);
            termIds.add(termId);
            term.setText(termTxt);

            // Definition edit text
            EditText definition = new EditText(getContext());
            definition.setHint(R.string.DefinitionString);
            ViewGroup.LayoutParams defParams = new ConstraintLayout.LayoutParams(toDp(110), ViewGroup.LayoutParams.MATCH_PARENT);
            ((ConstraintLayout.LayoutParams) defParams).setMargins(toDp(8), toDp(8), 0, toDp(8));
            definition.setLayoutParams(defParams);
            int defId = View.generateViewId();
            definition.setId(defId);
            defIds.add(defId);
            definition.setText(defTxt);

            // Is marked checkbox
            final CheckBox checkBox = new CheckBox(getContext());
            ViewGroup.LayoutParams checkBoxParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((ConstraintLayout.LayoutParams) checkBoxParams).setMargins(toDp(8), toDp(8), 0, toDp(8));
            checkBox.setLayoutParams(checkBoxParams);
            int checkboxId = View.generateViewId();
            checkIds.add(checkboxId);
            checkBox.setId(checkboxId);
            checkBox.setChecked(isMarked);
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

            // Delete icon
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
            layout.addView(checkBox);

            // Add constraints so widgets line up correctly
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);
            // Set top and bottom layouts of all widgets
            int[] idList = {lblId, termId, defId, iconId, checkboxId};
            for (int i = 0; i < idList.length; i++) {
                constraintSet.connect(idList[i], ConstraintSet.TOP, layoutId, ConstraintSet.TOP);
                constraintSet.connect(idList[i], ConstraintSet.BOTTOM, layoutId, ConstraintSet.BOTTOM);
            }
            // Set horizontal constraints
            constraintSet.connect(lblId, ConstraintSet.START, layoutId, ConstraintSet.START);
            constraintSet.connect(termId, ConstraintSet.START, lblId, ConstraintSet.END);
            constraintSet.connect(defId, ConstraintSet.START, termId, ConstraintSet.END);
            constraintSet.connect(checkboxId, ConstraintSet.START, defId, ConstraintSet.END);
            constraintSet.connect(iconId, ConstraintSet.START, checkboxId, ConstraintSet.END);
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

    /**
     * OnViewCreated method. Needed since some view elements aren't ready to have listeners added until now.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        addFlashcardButton = (Button) view.findViewById(R.id.add_flashcard_button);
        addFlashcardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                addFlashcard("", "", false);
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

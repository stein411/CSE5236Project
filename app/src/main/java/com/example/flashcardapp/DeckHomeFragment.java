package com.example.flashcardapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DeckHomeFragment extends Fragment {
    private Button deckViewButton;
    private Button backButton;
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
    private List<String> profNames;
    private List<String> categoryNames;
    private int profIndex;
    private int categoryIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Get references to text view widgets
        deckName = (TextView) v.findViewById(R.id.deck_name_label);
        courseName = (TextView) v.findViewById(R.id.course_name_label);
        schoolName = (TextView) v.findViewById(R.id.school_name_label);
        profName = (TextView) v.findViewById(R.id.professor_name_label);
        categoryName = (TextView) v.findViewById(R.id.category_name_label);

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
                    startActivityForResult(sendData, 0);
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

        // TODO debug these two
        profName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "profNameClick");
                if (profNames != null && categoryNames.size() > 0) {
                    profName.setText(profNames.get(profIndex));
                    profIndex++;
                    profIndex %= profNames.size();
                }
            }
        });

        categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "categoryNameClick");
                if (categoryNames != null && categoryNames.size() > 0) {
                    categoryName.setText(profNames.get(categoryIndex));
                    categoryIndex++;
                    categoryIndex %= categoryNames.size();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        addFlashcardButton = (Button) view.findViewById(R.id.add_flashcard_button);
        addFlashcardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {

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
                    ((ConstraintLayout.LayoutParams) lblParams).setMargins(toDp(16), toDp(12),0,0);
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

                    EditText definition = new EditText(getContext());
                    definition.setHint(R.string.DefinitionString);
                    ViewGroup.LayoutParams defParams = new ConstraintLayout.LayoutParams(toDp(140), ViewGroup.LayoutParams.MATCH_PARENT);
                    ((ConstraintLayout.LayoutParams) defParams).setMargins(toDp(8), toDp(8), 0, toDp(8));
                    definition.setLayoutParams(defParams);
                    int defId = View.generateViewId();
                    definition.setId(defId);

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

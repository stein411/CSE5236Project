package com.example.flashcardapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DeckEditFragment extends Fragment {
    private static final String TAG = "DeckEditFragment";
    private Button saveChangesButton;
    private Button addProfessorButton;
    private Button addCategoryButton;
    private Intent mIntent;
    private Intent sourceIntent;
    private ArrayList<Integer> professorIds;
    private ArrayList<Integer> categoryIds;
    private EditText deckName;
    private EditText courseName;
    private EditText schoolName;
    private String deckKey;
    private String courseKey;
    private String schoolKey;
    private String professorKey;
    private String categoryKey;
    private ArrayList<String> professorNames;
    private ArrayList<String> categoryNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView(LayoutInflater, ViewGroup, Bundle) called");
        View v = inflater.inflate(R.layout.fragment_deck_edit, container, false);

        professorIds = new ArrayList<>();
        categoryIds = new ArrayList<>();
        professorNames = new ArrayList<>();
        categoryNames = new ArrayList<>();

        mIntent = new Intent();
        sourceIntent = getActivity().getIntent();

        deckKey = getResources().getString(R.string.NameString);
        courseKey = getResources().getString(R.string.CourseString);
        schoolKey = getResources().getString(R.string.SchoolString);
        professorKey = getResources().getString(R.string.ProfessorString);
        categoryKey = getResources().getString(R.string.CategoryString);

        deckName = (EditText) v.findViewById(R.id.edit_deck_name);
        courseName = (EditText) v.findViewById(R.id.edit_course_name);
        schoolName = (EditText) v.findViewById(R.id.edit_school_name);

        // Set the edit text values
        Bundle extras = sourceIntent.getExtras();
        if (extras != null) {
            String deckTitle = extras.getString(deckKey);
            if (deckTitle != null && !deckTitle.equals(deckKey)) {
                deckName.setText(deckTitle);
            }

            String courseTitle = extras.getString(courseKey);
            if (courseTitle != null && !courseTitle.equals(courseKey)) {
                courseName.setText(courseTitle);
            }

            String schoolTitle = extras.getString(schoolKey);
            if (schoolTitle != null && !schoolTitle.equals(schoolKey)) {
                schoolName.setText(schoolTitle);
            }
        }

        saveChangesButton = (Button) v.findViewById(R.id.save_changes_button);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    // Put the deck, course and school name into the intent
                    String deckTitle = deckName.getText().toString();
                    String courseTitle = courseName.getText().toString();
                    String schoolTitle = schoolName.getText().toString();

                    if (deckTitle.length() > 0) {
                        // TODO add more title validation checks
                        mIntent.putExtra(deckKey, deckTitle);
                    } else {
                        // Put the default (Deck Name)
                        mIntent.putExtra(deckKey, deckKey);
                    }
                    if (courseTitle.length() > 0) {
                        // TODO add more title validation checks
                        mIntent.putExtra(courseKey, courseTitle);
                    } else {
                        // Put the default (Course Name)
                        mIntent.putExtra(courseKey, courseKey);
                    }
                    if (schoolTitle.length() > 0) {
                        // TODO add more title validation checks
                        mIntent.putExtra(schoolKey, schoolTitle);
                    } else {
                        // Put the default (School Name)
                        mIntent.putExtra(schoolKey, schoolKey);
                    }

                    for (int i = 0; i < professorIds.size(); i++) {
                        int profId = professorIds.get(i);

                        EditText prof = v.findViewById(profId);
                        if (prof != null) {
                            // Add the professor text to the array list of professor names
                            String text = prof.getText().toString();
                            if (text != null && text.length() > 0 && !text.equals(professorKey)) {
                                professorNames.add(text);
                            }
                        }
                    }

                    for (int i = 0; i < categoryIds.size(); i++) {
                        int categoryId = categoryIds.get(i);

                        EditText category = v.findViewById(categoryId);
                        if (category != null) {
                            // Add the category text to the array list of category names
                            String text = category.getText().toString();
                            if (text != null && text.length() > 0 && !text.equals(categoryKey)) {
                                categoryNames.add(text);
                            }
                        }
                    }

                    mIntent.putStringArrayListExtra(professorKey, professorNames);
                    mIntent.putStringArrayListExtra(categoryKey, categoryNames);

                    getActivity().setResult(Activity.RESULT_OK, mIntent);
                    getActivity().finish();
                }
                Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        addProfessorButton = view.findViewById(R.id.add_professor);
        addProfessorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfOrCategoryButton(true, getView(), getContext());
            }
        });

        addCategoryButton = view.findViewById(R.id.add_category);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfOrCategoryButton(false, getView(), getContext());
            }
        });
    }

    /**
     * Additional method used when the on click listener is called for adding a new
     * professor or category to the deck. Helps setup and inject the new widgets
     *
     * @param isProf
     *          true if the addProfessor button method was called, false if addCategory button
     *          method was called
     * @param v
     *          the activity view to inject the widgets into
     * @param context
     *          the context for the activity
     */
    public void addProfOrCategoryButton(boolean isProf, View v, Context context) {
        int linearLayoutId;
        int lblTxtKey;
        int hintTxtKey;

        if (isProf) {
            linearLayoutId = R.id.professor_container;
            lblTxtKey = R.string.ProfessorNameLabel;
            hintTxtKey = R.string.ProfessorString;
        } else {
            linearLayoutId = R.id.category_container;
            lblTxtKey = R.string.CategoryNameLabel;
            hintTxtKey = R.string.CategoryString;
        }

        LinearLayout ll = (LinearLayout) v.findViewById(linearLayoutId);
        if (ll != null) {
            // Create a new layout
            ConstraintLayout layout = new ConstraintLayout(context);
            layout.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int layoutId = View.generateViewId();
            layout.setId(layoutId);

            // Setup the widgets
            TextView lbl = new TextView(context);
            lbl.setText(lblTxtKey);
            ViewGroup.LayoutParams lblParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((ConstraintLayout.LayoutParams) lblParams).setMargins(toDp(16), toDp(16), 0, 0);
            lbl.setLayoutParams(lblParams);
            int lblId = View.generateViewId();
            lbl.setId(lblId);

            EditText editText = new EditText(context);
            editText.setHint(hintTxtKey);
            ViewGroup.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((ConstraintLayout.LayoutParams) layoutParams).setMargins(toDp(16), toDp(8), toDp(16), 0);
            editText.setLayoutParams(layoutParams);
            int editTxtId = View.generateViewId();
            editText.setId(editTxtId);

            if (isProf) {
                professorIds.add(editTxtId);
            } else {
                categoryIds.add(editTxtId);
            }

            lbl.setLabelFor(editTxtId);

            layout.addView(lbl);
            layout.addView(editText);

            // Add constraints
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);
            constraintSet.connect(lblId, ConstraintSet.TOP, layoutId, ConstraintSet.TOP);
            constraintSet.connect(lblId, ConstraintSet.START, layoutId, ConstraintSet.START);
            constraintSet.connect(editTxtId, ConstraintSet.BOTTOM, layoutId, ConstraintSet.BOTTOM);
            constraintSet.connect(editTxtId, ConstraintSet.START, layoutId, ConstraintSet.START);
            constraintSet.connect(editTxtId, ConstraintSet.END, layoutId, ConstraintSet.END);
            constraintSet.connect(editTxtId, ConstraintSet.TOP, lblId, ConstraintSet.BOTTOM);

            // Apply constraints
            constraintSet.applyTo(layout);

            // Add layout
            ll.addView(layout);
        }
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



    /*
     * Overriding lifestyle methods for logging.
     */

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}

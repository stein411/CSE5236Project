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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeckHomeFragment extends Fragment {
    private Button deckViewButton;
    private Button backButton;
    private Button addFlashcardButton;
    private int flashcardCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deck_home, container, false);
        deckViewButton = (Button) v.findViewById(R.id.LaunchDeckButton);
        deckViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().startActivity(new Intent(getActivity(), DeckEditActivity.class));
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
        return v;
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
                    int layoutId = View.generateViewId();
                    layout.setId(layoutId);

                    // Setup the widgets
                    TextView lbl = new TextView(getContext());
                    lbl.setText(Integer.toString(++flashcardCount));
                    lbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    ViewGroup.LayoutParams lblParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ((ConstraintLayout.LayoutParams) lblParams).setMargins(toDp(16), toDp(12),0,0);
                    lbl.setLayoutParams(lblParams);
                    int lblId = View.generateViewId();
                    lbl.setId(lblId);

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

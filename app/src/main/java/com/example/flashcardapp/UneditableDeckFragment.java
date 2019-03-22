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

public class UneditableDeckFragment extends Fragment {
    private TextView ratingsText;
    private SeekBar ratingsBar;
    private int mRating;
    private Button studyDeckButton;
    private Button backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uneditable_deck, container, false);
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
        return v;
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

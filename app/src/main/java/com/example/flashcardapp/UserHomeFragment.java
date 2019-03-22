package com.example.flashcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.flashcardapp.Activities.DeckMenuActivity;
import com.example.flashcardapp.Activities.MapsActivity;
import com.example.flashcardapp.Activities.SearchActivity;

public class UserHomeFragment extends Fragment {
    private Button myDecksButton;
    private Button searchDecksButton;
    private Button mapsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_home, container, false);
        myDecksButton = (Button) v.findViewById(R.id.my_decks_button);
        myDecksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().startActivity(new Intent(getContext(), DeckMenuActivity.class));
                }
            }
        });
        searchDecksButton = (Button) v.findViewById(R.id.search_decks_button);
        searchDecksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().startActivity(new Intent(getContext(), SearchActivity.class));
                }
            }
        });
        mapsButton = (Button) v.findViewById(R.id.find_decks_button);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().startActivity(new Intent(getContext(), MapsActivity.class));
                }
            }
        });
        return v;
    }
}

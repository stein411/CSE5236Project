package com.example.flashcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class UserHomeFragment extends Fragment {
    private Button myDecksButton;
    private Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_home, container, false);
        myDecksButton = (Button) v.findViewById(R.id.my_decks_button);
        myDecksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().startActivity(new Intent(getActivity(), DeckMenuActivity.class));
                }
            }
        });
        searchButton = (Button) v.findViewById(R.id.search_decks_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), SearchDecksActivity.class));
            }
        });
        return v;
    }
}

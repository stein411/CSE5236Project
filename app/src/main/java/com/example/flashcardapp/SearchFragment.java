package com.example.flashcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.flashcardapp.Activities.SearchActivity;
import com.example.flashcardapp.Activities.UneditableDeckActivity;


import java.util.ArrayList;
import java.util.Arrays;

public class SearchFragment extends Fragment {
    private ListView decksList;
    private ArrayAdapter<String> adapter;
    private String deckKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        decksList = (ListView) v.findViewById(R.id.decks_list);
        decksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = decksList.getItemAtPosition(i).toString().trim();
                Intent intent = new Intent(getContext(), UneditableDeckActivity.class);
                intent.putExtra(deckKey, text);
                startActivity(intent);
            }
        });
        ArrayList<String> decks = new ArrayList<>();
        String[] dummyData = {"Deck1", "Deck2", "Deck3", "Deck4", "Deck5"};
        decks.addAll(Arrays.asList(dummyData));
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, decks);
        decksList.setAdapter(adapter);
        setHasOptionsMenu(true);
        deckKey = getString(R.string.NameString);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_decks);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}

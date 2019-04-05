package com.example.flashcardapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcardapp.Activities.SearchActivity;
import com.example.flashcardapp.Activities.UneditableDeckActivity;
import com.example.flashcardapp.RoomDatabase.Deck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    private ListView decksList;
    private ArrayAdapter<String> adapter;
    private String deckKey;
    private CollectionReference decksCollection;
    private ArrayList<String> decks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        decksList = (ListView) v.findViewById(R.id.decks_list);
        decksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = decksList.getItemAtPosition(i).toString();
                Intent intent = new Intent(getContext(), UneditableDeckActivity.class);
                intent.putExtra(deckKey, item);
                startActivity(intent);
            }
        });
        decks = new ArrayList<>();
        decksCollection = FirebaseFirestore.getInstance().collection("decks");
        decksCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTest101", "Accessing firebase");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document != null && document.get("name") != null) {
                            decks.add(document.get("name").toString());
                            decksList.requestLayout();
                        }
                    }
                } else {
                    Log.d("FirebaseTest101", "Task was unsuccessful");
                }
            }
        });
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, decks);
        decksList.setAdapter(adapter);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
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
        super.onCreateOptionsMenu(menu, getActivity().getMenuInflater());
    }
}

package com.example.flashcardapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
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

public class SearchFragment extends Fragment {
    private ListView decksList;
    private DeckAdapter adapter;
    private String deckKey;
    private CollectionReference decksCollection;
    private ArrayList<Deck> decks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        decksList = (ListView) v.findViewById(R.id.decks_list);
        decksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Deck item = (Deck) decksList.getItemAtPosition(i);
                String text = item.getName();
                Intent intent = new Intent(getContext(), UneditableDeckActivity.class);
                intent.putExtra(deckKey, text);
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
                            Deck deck = new Deck(document.get("name").toString());

                            if (document.get("owner") != null) {
                                deck.setOwnerEmail(document.get("owner").toString());
                            }
                            decks.add(deck);
                            decksList.requestLayout();
                        }
                    }
                } else {
                    Log.d("FirebaseTest101", "Task was unsuccessful");
                }
            }
        });
        adapter = new DeckAdapter(getContext(), R.layout.deck_list_item, decks);
        decksList.setAdapter(adapter);
        setHasOptionsMenu(true);
        deckKey = getString(R.string.NameString);
        return v;
    }

    public class DeckAdapter extends ArrayAdapter<Deck> {
        private ArrayList<Deck> items;
        private DeckViewHolder deckHolder;

        private class DeckViewHolder {
            TextView name;
            TextView ownerName;
        }

        public DeckAdapter(Context context, int resId, ArrayList<Deck> items) {
            super(context, resId, items);
            this.items = items;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.deck_list_item, null);
                deckHolder = new DeckViewHolder();
                deckHolder.name = (TextView) v.findViewById(R.id.deck_name);
                deckHolder.ownerName = (TextView) v.findViewById(R.id.deck_owner_name);
                v.setTag(deckHolder);
            } else {
                deckHolder = (DeckViewHolder) v.getTag();
            }

            Deck deck = items.get(pos);

            if (deck != null) {
                deckHolder.name.setText(deck.getName());
                deckHolder.ownerName.setText("Made by " + deck.getOwnerEmail());
            }

            return v;
        }
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

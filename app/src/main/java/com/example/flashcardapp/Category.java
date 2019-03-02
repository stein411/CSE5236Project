package com.example.flashcardapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Category {
    @PrimaryKey
    @NonNull
    @ForeignKey(entity = Deck.class, parentColumns = "name", childColumns = "deck_name")
    public String deckName;

    @ColumnInfo(name = "c_name")
    public String cName;
}

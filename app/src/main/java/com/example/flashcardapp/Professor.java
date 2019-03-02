package com.example.flashcardapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Professor {
    @PrimaryKey
    @NonNull
    @ForeignKey(entity = Deck.class, parentColumns = "name", childColumns = "deck_name")
    public String deckName;

    @ColumnInfo(name = "p_name")
    public String pName;
}

package com.example.flashcardapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Deck.class, parentColumns = "name", childColumns = "deck_name", onDelete = CASCADE), tableName = "flashcard_table")
public class Flashcard {
    @NonNull
    @ColumnInfo(name = "term")
    private String term;

    @NonNull
    @ColumnInfo(name = "definition")
    private String definition;

    @NonNull
    @ColumnInfo(name = "deck_name")
    private String deckName;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(@NonNull String deckName) {
        this.deckName = deckName;
    }

    @NonNull
    public String getTerm() {
        return term;
    }

    public void setTerm(@NonNull String term) {
        this.term = term;
    }

    @NonNull
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(@NonNull String definition) {
        this.definition = definition;
    }
}

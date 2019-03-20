package com.example.flashcardapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.util.TableInfo;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Deck.class, parentColumns = "name", childColumns = "deck_name", onDelete = CASCADE),
        tableName = "professor_table")
public class Professor {
    @NonNull
    @ColumnInfo(name = "professor_name")
    private String professorName;

    @NonNull
    @ColumnInfo(name = "deck_name")
    private String deckName;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(@NonNull String professorName) {
        this.professorName = professorName;
    }

    @NonNull
    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(@NonNull String deckName) {
        this.deckName = deckName;
    }
}

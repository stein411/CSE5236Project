package com.example.flashcardapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Deck.class, parentColumns = "name", childColumns = "deck_name", onDelete = CASCADE),
        tableName = "category_table")
public class Category {
    @NonNull
    @ColumnInfo(name = "category_name")
    private String categoryName;

    @NonNull
    @ColumnInfo(name = "deck_name")
    private String deckName;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }

    @NonNull
    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(@NonNull String deckName) {
        this.deckName = deckName;
    }
}

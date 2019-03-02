package com.example.flashcardapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Deck.class, Professor.class, Category.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeckDao mDeckDao();
    public abstract ProfessorDao mProfessorDao();
    public abstract CategoryDao mCategoryDao();
}

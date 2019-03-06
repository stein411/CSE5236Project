package com.example.flashcardapp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Deck.class}, version = 1)
public abstract class DeckRoomDatabase extends RoomDatabase {
    public abstract DeckDao deckDao();

    private static volatile DeckRoomDatabase INSTANCE;

    static DeckRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DeckRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DeckRoomDatabase.class, "deck_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

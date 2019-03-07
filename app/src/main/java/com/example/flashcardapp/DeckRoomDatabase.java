package com.example.flashcardapp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Deck.class, Professor.class, Flashcard.class}, version = 4)
public abstract class DeckRoomDatabase extends RoomDatabase {
    public abstract DeckDao deckDao();
    public abstract ProfessorDao professorDao();
    public abstract FlashcardDao flashcardDao();

    private static volatile DeckRoomDatabase INSTANCE;

    static DeckRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DeckRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DeckRoomDatabase.class, "deck_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `professor_table` (`professor_name` TEXT NOT NULL, " +
                    "`deck_name` TEXT NOT NULL," + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "FOREIGN KEY(`deck_name`) REFERENCES `deck_table`(`name`))");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS `professor_table`");
            database.execSQL("CREATE TABLE `professor_table` (`professor_name` TEXT NOT NULL, " +
                    "`deck_name` TEXT NOT NULL," + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "FOREIGN KEY(`deck_name`) REFERENCES `deck_table`(`name`) ON DELETE CASCADE)");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS `flashcard_table`");
            database.execSQL("CREATE TABLE `flashcard_table` (`term` TEXT NOT NULL, `definition` TEXT NOT NULL, " +
                    "`deck_name` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "FOREIGN KEY(`deck_name`) REFERENCES `deck_table`(`name`) ON DELETE CASCADE)");
        }
    };
}

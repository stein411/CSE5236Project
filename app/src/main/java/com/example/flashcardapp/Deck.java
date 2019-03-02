package com.example.flashcardapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Deck {
    @PrimaryKey
    @NonNull
    public String name;

    @ColumnInfo(name = "course_name")
    public String courseName;

    @ColumnInfo(name = "school")
    public String school;
}

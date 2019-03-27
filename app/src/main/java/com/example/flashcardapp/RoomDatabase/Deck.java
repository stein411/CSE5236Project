package com.example.flashcardapp.RoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "deck_table")
public class Deck {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "course")
    private String mCourse;

    @ColumnInfo(name = "school")
    private String mSchool;

    @ColumnInfo(name = "owner_email")
    private String mOwnerEmail;

    public Deck(String name) {
        this.mName = name;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public String getCourse() {
        return mCourse;
    }

    public String getSchool() {
        return mSchool;
    }

    public void setCourse(String course) {
        mCourse = course;
    }

    public void setSchool(String school) {
        mSchool = school;
    }

    public String getOwnerEmail() {
        return mOwnerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        mOwnerEmail = ownerEmail;
    }
}

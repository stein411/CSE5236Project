package com.example.flashcardapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent activityIntent;

        //TODO replace with login token logic
        //if (Util.getToken() != null) {
        //activityIntent = new Intent(this, UserHomeActivity.class);
        //} else {
        activityIntent = new Intent(this, LoginActivity.class);
        //}

        startActivity(activityIntent);
        finish();
    }
}

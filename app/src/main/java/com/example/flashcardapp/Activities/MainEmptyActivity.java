package com.example.flashcardapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.flashcardapp.LoginActivity;

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

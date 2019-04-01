package com.example.flashcardapp.Activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.flashcardapp.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


//google import tutorial start

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.quickstart.auth.R;

//google import tutorial end

public class MainEmptyActivity extends AppCompatActivity {

    //GOOGLE SIGN IN PASTE START
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9011;
    //GOOGLE SIGN IN PASTE END

    //old onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent activityIntent;

        //TODO replace with login token logic
        //if (Util.getToken() != null) {
        //activityIntent = new Intent(this, UserHomeActivity.class);
        //} else {
        //activityIntent = new Intent(this, LoginActivity.class);
        //}
        // Configure Google Sign In
        // TODO add google sign in support

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null || user.getEmail().length() == 0) {
            // No user signed in; direct them to sign in
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.AnonymousBuilder().build());
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), RC_SIGN_IN);
        } else {
            // User is signed in; take them to the user home activity
            startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
            finish();
        }

        //startActivity(activityIntent);
        //finish();
    }

    //old onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Issue", Toast.LENGTH_LONG).show();
            }
        }
    }

    //email verification
    //does not work currently, not sure where to insert
    public void sendEmailVerification() {
        // [START send_email_verification]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
        // [END send_email_verification]
    }

}

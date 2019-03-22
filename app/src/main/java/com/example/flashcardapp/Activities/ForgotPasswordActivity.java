package com.example.flashcardapp.Activities;

import android.support.v4.app.Fragment;

import com.example.flashcardapp.ForgotPasswordFragment;

public class ForgotPasswordActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ForgotPasswordFragment();
    }
}

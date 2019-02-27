package com.example.flashcardapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ForgotPasswordFragment extends Fragment {
    private Button mRetrievePasswordButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        mRetrievePasswordButton = (Button) v.findViewById(R.id.retrieve_password);
        mRetrievePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {
                    /*TODO check for valid firstname, lastname, email against the db */
                    getActivity().finish();
                    Toast.makeText(getContext(), R.string.password_retrieval_confirmation, Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }
}

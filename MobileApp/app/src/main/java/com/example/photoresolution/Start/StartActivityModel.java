package com.example.photoresolution.Start;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivityModel implements StartActivityContract.Model{
    private StartActivityContract.onStartListener mOnStartListener;
    FirebaseUser user;

    public StartActivityModel(StartActivityContract.onStartListener mOnStartListener){
        this.mOnStartListener = mOnStartListener;
    }

    @Override
    public void logOutUser() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public String getUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String login;
        if (user != null) {
            login = user.getEmail();
            Log.d("Login", login);
        } else {
            login = "";
        }
        return login;
    }
}

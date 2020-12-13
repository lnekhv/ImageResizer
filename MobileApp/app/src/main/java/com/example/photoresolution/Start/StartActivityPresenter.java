package com.example.photoresolution.Start;

import android.app.Activity;

import com.example.photoresolution.Login.LoginActivityContract;
import com.example.photoresolution.Login.LoginActivityModel;

public class StartActivityPresenter implements StartActivityContract.Presenter, StartActivityContract.onStartListener{
    StartActivityContract.View view;
    private StartActivityModel model;

    public StartActivityPresenter(StartActivityContract.View view) {
        this.view = view;
        model = new StartActivityModel(this);
    }

    @Override
    public void doLogOut() {
        model.logOutUser();
    }

    @Override
    public String getUser() {
        return model.getUser();
    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}

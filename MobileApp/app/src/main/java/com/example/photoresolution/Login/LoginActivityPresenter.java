package com.example.photoresolution.Login;

import android.app.Activity;

public class LoginActivityPresenter implements LoginActivityContract.Presenter, LoginActivityContract.onLoginListener {

    LoginActivityContract.View view;
    private LoginActivityModel model;

    public LoginActivityPresenter(LoginActivityContract.View view) {
        this.view = view;
        model = new LoginActivityModel(this);
    }


    @Override
    public void doLogin(Activity activity, String email, String password) {
        model.AutheticateUser(activity, email, password);
    }

    @Override
    public void onSuccess(String message) {
        view.onSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        view.onFailure(message);
    }
}

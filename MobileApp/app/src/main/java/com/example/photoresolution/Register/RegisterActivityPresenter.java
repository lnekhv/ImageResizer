package com.example.photoresolution.Register;
import android.app.Activity;

public class RegisterActivityPresenter implements RegisterActivityContract.Presenter, RegisterActivityContract.onRegisterListener {
    RegisterActivityContract.View view;
    private RegisterActivityModel model;

    public RegisterActivityPresenter(RegisterActivityContract.View view) {
        this.view = view;
        model = new RegisterActivityModel(this);
    }

    @Override
    public void onSuccess(String message) {
        view.onSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        view.onFailure(message);
    }

    @Override
    public void doRegistration(Activity activity, String email, String password) {
        model.AutheticateUser(activity, email, password);
    }
}

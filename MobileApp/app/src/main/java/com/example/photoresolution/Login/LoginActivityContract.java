package com.example.photoresolution.Login;

import android.app.Activity;

public interface LoginActivityContract {
    interface View {
        void onSuccess(String message);
        void onFailure(String message);
    }

    interface Presenter {
        void doLogin(Activity activity, String email, String password);
    }

    interface Model{
        void AutheticateUser(Activity activity, String username, String password);
    }

    interface onLoginListener{
        void onSuccess(String message);
        void onFailure(String message);
    }
}

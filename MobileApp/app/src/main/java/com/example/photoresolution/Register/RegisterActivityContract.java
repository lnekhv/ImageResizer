package com.example.photoresolution.Register;

import android.app.Activity;

public interface RegisterActivityContract {
    interface View {
        void onSuccess(String message);
        void onFailure(String message);
    }

    interface Presenter {
        void doRegistration(Activity activity, String email, String password);
    }

    interface Model{
        void AutheticateUser(Activity activity, String username, String password);
    }

    interface onRegisterListener{
        void onSuccess(String message);
        void onFailure(String message);
    }
}

package com.example.photoresolution.Start;

import android.app.Activity;

public interface StartActivityContract {
    interface View {
        void onSuccess(String message);

        void onFailure(String message);
    }

    interface Presenter {
        void doLogOut();
        String getUser();
    }

    interface Model {
        void logOutUser();
        String getUser();
    }

    interface onStartListener {
        void onSuccess(String message);
        void onFailure(String message);
    }
}

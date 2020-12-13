package com.example.photoresolution.Database;

import android.app.Activity;

import com.example.photoresolution.model.ImageDatabase;

import java.util.ArrayList;

public interface DatabaseActivityContract {
    interface View {
        void setToolbarText();
        void setData();
        void onSuccess(String message);
        void onFailure(String message);
    }

    interface Presenter {
        ArrayList<ImageDatabase> getData(Activity activity);
        String getUser();
    }

    interface Model{
        void getDataFromDatabase(Activity activity);
        String getUser();
    }

    interface onLoginListener{
        void onSuccess(String message);
        void onFailure(String message);
    }
}

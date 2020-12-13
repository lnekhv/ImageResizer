package com.example.photoresolution.Database;

import android.app.Activity;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.photoresolution.Adapter;
import com.example.photoresolution.Login.LoginActivityContract;
import com.example.photoresolution.Login.LoginActivityModel;
import com.example.photoresolution.model.ImageDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class DatabaseActivityPresenter implements DatabaseActivityContract.Presenter, DatabaseActivityContract.onLoginListener {
    DatabaseActivityContract.View view;
    private DatabaseActivityModel model;
    ArrayList<ImageDatabase> list;


    public DatabaseActivityPresenter(DatabaseActivityContract.View view) {
        this.view = view;
        model = new DatabaseActivityModel(this);
        list = new ArrayList<ImageDatabase>();
    }

    @Override
    public ArrayList<ImageDatabase> getData(Activity activity) {
        model.getDataFromDatabase(activity);
        list = model.list;
        Log.d("list", Integer.toString(list.size()));
        return list;
    }

    @Override
    public String getUser() {
        String login = model.getUser();
        return login;
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

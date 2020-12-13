package com.example.photoresolution.Database;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.photoresolution.Adapter;
import com.example.photoresolution.Login.LoginActivityContract;
import com.example.photoresolution.model.ImageDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseActivityModel implements DatabaseActivityContract.Model {
    DatabaseReference myRef;
    RecyclerView recyclerView;
    ArrayList<ImageDatabase> list;
    Adapter myAdapter;
    FirebaseUser user;

    private DatabaseActivityContract.onLoginListener mOnDatabaseListener;

    public DatabaseActivityModel(DatabaseActivityContract.onLoginListener mOnDatabaseListener) {
        this.mOnDatabaseListener = mOnDatabaseListener;
        list = new ArrayList<ImageDatabase>();
    }

    @Override
    public void getDataFromDatabase(final Activity activity) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("images").child(user.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mOnDatabaseListener.onSuccess("Database connected");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ImageDatabase imageDatabaseModel = dataSnapshot1.getValue(ImageDatabase.class);
                    imageDatabaseModel.setImageId(dataSnapshot1.getKey());
                    list.add(imageDatabaseModel);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                mOnDatabaseListener.onFailure("Something wrong");
            }
        });
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

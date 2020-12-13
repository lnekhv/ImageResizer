package com.example.photoresolution.Database;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoresolution.Adapter;
import com.example.photoresolution.Login.LoginActivity;
import com.example.photoresolution.Login.LoginActivityContract;
import com.example.photoresolution.Login.LoginActivityPresenter;
import com.example.photoresolution.R;
import com.example.photoresolution.model.ImageDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseActivity extends AppCompatActivity implements DatabaseActivityContract.View {
    DatabaseReference myRef;
    RecyclerView recyclerView;
    Adapter myAdapter;

    private Toolbar toolbar;
    private TextView toolbar_text;

    DatabaseActivityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        presenter = new DatabaseActivityPresenter(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.myToolbar);
        toolbar_text = findViewById(R.id.text_user_login_toolbar);

        setSupportActionBar(toolbar);
        setToolbarText();

        setData();

    }

    @Override
    public void setToolbarText() {
        String login = presenter.getUser();
        toolbar_text.setText(login);
    }

    @Override
    public void setData() {
        myAdapter = new Adapter(DatabaseActivity.this, presenter.getData(this));
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onSuccess(String message) {
        Toast.makeText(DatabaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(DatabaseActivity.this, "Authentication failed.." + message, Toast.LENGTH_SHORT).show();
    }
}
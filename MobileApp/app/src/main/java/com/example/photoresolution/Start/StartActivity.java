package com.example.photoresolution.Start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoresolution.Login.LoginActivity;
import com.example.photoresolution.Login.LoginActivityContract;
import com.example.photoresolution.Login.LoginActivityPresenter;
import com.example.photoresolution.MainActivity;
import com.example.photoresolution.R;
import com.example.photoresolution.Register.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity implements StartActivityContract.View {
    private TextView textLogin;

    StartActivityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        presenter = new StartActivityPresenter(this);

        textLogin = (TextView) findViewById(R.id.text_user_login);

        setUserLogin();

        final Button logButton = findViewById(R.id.log_button);
        logButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        final Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        final Button continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button logOut = findViewById(R.id.button_log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userLogOut();
            }
        });
    }

    private void userLogOut() {
        presenter.doLogOut();
        Toast.makeText(getApplication(), "User logged out successfully", Toast.LENGTH_SHORT).show();
        textLogin.setText("");
    }

    private void setUserLogin() {
        String login = presenter.getUser();
        textLogin.setText(login);
    }


    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}
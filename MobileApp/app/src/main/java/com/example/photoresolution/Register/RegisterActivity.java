package com.example.photoresolution.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.photoresolution.Login.LoginActivity;
import com.example.photoresolution.Login.LoginActivityContract;
import com.example.photoresolution.Login.LoginActivityPresenter;
import com.example.photoresolution.MainActivity;
import com.example.photoresolution.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements RegisterActivityContract.View {

    RegisterActivityContract.Presenter presenter;

    private EditText inputUsername, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        presenter = new RegisterActivityPresenter(this);

        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);


        final Button registerButton = findViewById(R.id.register_button_reg);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        System.out.println("Username: " + username + " password: " + password);

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must have at least 6 characters...", Toast.LENGTH_SHORT).show();
        } else {
            presenter.doRegistration(this, username, password);
        }
    }

    @Override
    public void onSuccess(String message) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(RegisterActivity.this, "Authentication failed.." + message, Toast.LENGTH_SHORT).show();
    }
}